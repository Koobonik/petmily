package com.spring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spring.configuration.web.PricingPlanService;
import com.spring.domain.PetmilyRepository;
import com.spring.domain.PetmilyUsers;
import com.spring.domain.SmsAuth;
import com.spring.domain.SmsAuthRepository;
import com.spring.dto.requestDto.ValidateAuthNumberRequestDto;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.dto.responseDto.SendSmsResponseDto;
import com.spring.util.DateCreator;
import com.spring.util.ValidSomething;
import com.spring.util.cryptors.AES256Cipher;
import com.spring.util.jwt.JwtTokenProvider;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Log4j2
public class SmsAuthService {

    private final SmsService smsService;
    private final SmsAuthRepository smsAuthRepository;
    private final AES256Cipher aes256Cipher;
    private final PetmilyRepository petmilyRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public SmsAuth save(SmsAuth smsAuth) {
        return smsAuthRepository.save(smsAuth);
    }

    public SmsAuth findSmsAuth(int number) {
        return smsAuthRepository.findByAuthNumber(number);
    }

    public SmsAuth findBySecret(String secret) {
        return smsAuthRepository.findBySecret(secret);
    }

    public List<SmsAuth> findAllByCanUse() {
        return smsAuthRepository.findAllByCanUse();
    }

    private final PricingPlanService pricingPlanService = new PricingPlanService();
    // 각종 인증번호 검증
    @Transactional
    public ResponseEntity<?> validateAuthNumber(ValidateAuthNumberRequestDto validateAuthNumberRequestDto) throws ParseException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Timestamp today = new DateCreator().getTimestamp(new DateCreator().getSimpleDateFormat_yyyy_MM_dd_HH_mm_ss_SSS(), new DateCreator().getCreatedDate_yyyy_MM_dd_HH_mm_ss_SSS());
        // 휴대폰 번호도 양방향 암호화 하여 저장한다.
        String encrypted = aes256Cipher.AES_Encode(validateAuthNumberRequestDto.getUserName() + ":" + validateAuthNumberRequestDto.getCallNumber() + ":" + validateAuthNumberRequestDto.getAuthSms());
        SmsAuth smsAuth = findBySecret(encrypted);
        if (smsAuth != null) {
            log.info("매칭되는 데이터가 있음 {}:{}:{}", validateAuthNumberRequestDto.getUserName(), validateAuthNumberRequestDto.getCallNumber(), validateAuthNumberRequestDto.getAuthSms());
            if (!today.before(new DateCreator().getAfterThreeMinutes(smsAuth.getCreatedDate()))) {
                log.info("3분 지났다");
                return new ResponseEntity<>(new DefaultResponseDto(409, "인증시간이 초과되었습니다."), HttpStatus.CONFLICT);
            }
            if (!smsAuth.isCanUse()) {
                log.info("사용 불가능한 코드 입니다.");
                return new ResponseEntity<>(new DefaultResponseDto(409, "사용 불가능한 코드 입니다."), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(new DefaultResponseDto(200, "인증되었습니다."), HttpStatus.OK);
        } else {
            // 인증번호는 유효한데 유저가 인증번호 요청뒤 이름이나 전화번호를 바꾼경우
            if (findSmsAuth(validateAuthNumberRequestDto.getAuthSms()) != null) {
                log.info("이름이나 전화번호를 바꿈.");
                return new ResponseEntity<>(new DefaultResponseDto(409, "인증번호 요청시 입력했던 이름과 전화번호이어야 합니다."), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(new DefaultResponseDto(409, "매칭되는 코드를 찾을 수 없습니다."), HttpStatus.CONFLICT);
        }
    }

    // 회원가입시 sms 보내는 기능
    @Transactional
    public ResponseEntity<?> sendAuthNumberForSignUp(String name, String callNumber) throws JsonProcessingException, ParseException, UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        log.info("회원가입을 위해 SMS 인증 name : '{}' , callNumber : '{}'", name, callNumber);
        if (!ValidSomething.isValidNumber(callNumber)) {
            log.info("휴대폰 번호가 아님 : '{}'", callNumber);
            return new ResponseEntity<>(new DefaultResponseDto(409, "휴대폰 번호가 아닙니다."), HttpStatus.CONFLICT);
        }
        if (name == null) {
            return new ResponseEntity<>(new DefaultResponseDto(409, "이름을 입력해주세요."), HttpStatus.CONFLICT);
        } else if (name.equals("")) {
            return new ResponseEntity<>(new DefaultResponseDto(409, "이름을 입력해주세요."), HttpStatus.CONFLICT);
        } else if (!ValidSomething.isValidName(name)) {
            return new ResponseEntity<>(new DefaultResponseDto(409, "이름은 한글, 영문, 숫자만 입력 가능합니다."), HttpStatus.CONFLICT);
        } else if(!ValidSomething.canUseNickName(name)){
            return new ResponseEntity<>(new DefaultResponseDto(409, "불용어가 포함되어 있습니다."), HttpStatus.CONFLICT);
        }
        PetmilyUsers petmilyUsers = petmilyRepository.findByUserPhoneNumber(aes256Cipher.AES_Encode(callNumber));
        if (petmilyUsers != null) {
            if (petmilyUsers.getIsOut()) {
                log.info("탈퇴 처리된 유저의 핸드폰 번호 입니다.");
                return new ResponseEntity<>(new DefaultResponseDto(409, "탈퇴 처리된 유저의 핸드폰 번호 입니다."), HttpStatus.CONFLICT);
            }
            log.info("이미 가입된 휴대폰 번호 : '{}'", callNumber);
            return new ResponseEntity<>(new DefaultResponseDto(409, "이미 가입된 휴대폰 번호 입니다."), HttpStatus.CONFLICT);
        }

        SmsAuth smsAuth = new SmsAuth(getSecureNumber());
        log.info("'{}'님의 인증번호 : '{}'", name, smsAuth.getAuthNumber());
        Bucket bucket = pricingPlanService.smsBucket(callNumber);
        if (bucket.tryConsume(1)) { // 1개 사용 요청
            // 초과하지 않음
            log.info("SMS 회원가입 초과 안함");
        } else {
            // 제한 초과
            log.info("SMS {} 트래픽 초과!!!", callNumber);
            return new ResponseEntity<>(new DefaultResponseDto(409, "인증 문자는 30초에 한번 씩 보낼 수 있습니다."), HttpStatus.CONFLICT);
        }
        SendSmsResponseDto sendSmsResponseDto = smsService.sendSms(callNumber, "[펫밀리]\n" + name + "님\n인증번호는 " + smsAuth.getAuthNumber() + " 입니다.");
        if (sendSmsResponseDto.getStatusCode().equals("202")) {
            smsAuth.setSecret(createSecret(name, callNumber, smsAuth.getAuthNumber()));
            smsAuth.setWhereToUse("회원가입");
            smsAuthRepository.save(smsAuth);
            blockDuplicateCode(smsAuth.getSecret()); // 코드 중복 인증 방지 함수 추가
            log.info("서버에 저장된 암호화된 코드 값 : '{}'", smsAuth.getSecret());
            return new ResponseEntity<>(new DefaultResponseDto(200, "인증번호가 전송되었습니다."), HttpStatus.OK);
        }
        log.info("'{}'님께 인증번호 전송 실패", name);
        return new ResponseEntity<>(new DefaultResponseDto(409, "전송에 실패하였습니다."), HttpStatus.CONFLICT);
    }

    // 필요에 따라 각종 (로그인시 필요한) SMS를 보내는 함수
    @Transactional
    public ResponseEntity<?> sendAuthNumber(String callNumber, String type, HttpServletRequest httpServletRequest) throws JsonProcessingException, ParseException, UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        PetmilyUsers petmilyUsers;
        if (type.equals("비밀번호찾기")) {
            String[] str = callNumber.split(":");
            petmilyUsers = petmilyRepository.findByUserNickNameAndUserPhoneNumber(str[0], aes256Cipher.AES_Encode(str[1]));
            callNumber = str[1];
            if (petmilyUsers == null)
                return new ResponseEntity<>(new DefaultResponseDto(409, "정보가 올바르지 않습니다."), HttpStatus.CONFLICT);
        }
        else if(type.equals("정보수정")){
            petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        }
        else {
            petmilyUsers = petmilyRepository.findByUserPhoneNumber(aes256Cipher.AES_Encode(callNumber));
        }
        log.info("sms '{}' '{}'", type, callNumber);
        if (petmilyUsers == null) {
            log.info("매칭되는 전화번호 없음");
            return new ResponseEntity<>(new DefaultResponseDto(409, "올바르지 않은 번호 입니다."), HttpStatus.CONFLICT);
        }
        if (petmilyUsers.getIsOut()) {
            log.info("탈퇴한 유저의 핸드폰 번호 '{}'", callNumber);
            return new ResponseEntity<>(new DefaultResponseDto(409, "탈퇴한 유저의 핸드폰 번호 입니다."), HttpStatus.CONFLICT);
        }
        log.info("{}시 필요한 인증번호 전송 name : '{}' , callNumber : '{}'", type, petmilyUsers.getUserNickName(), callNumber);

        SmsAuth smsAuth = new SmsAuth(getSecureNumber());
        Bucket bucket = pricingPlanService.smsBucket(callNumber);
        if (bucket.tryConsume(1)) { // 1개 사용 요청
            // 초과하지 않음
            log.info("SMS 초과 안함");
        } else {
            // 제한 초과
            log.info("SMS {} 트래픽 초과!!!", callNumber);
            return new ResponseEntity<>(new DefaultResponseDto(409, "인증 문자는 30초에 한번 씩 보낼 수 있습니다."), HttpStatus.CONFLICT);
        }
        SendSmsResponseDto sendSmsResponseDto = smsService.sendSms(callNumber, "[펫밀리]\n" + petmilyUsers.getUserNickName() + "님\n인증번호는 " + smsAuth.getAuthNumber() + " 입니다.");
        if (sendSmsResponseDto.getStatusCode().equals("202")) {
            smsAuth.setSecret(createSecret(petmilyUsers.getUserNickName(), callNumber, smsAuth.getAuthNumber()));
            smsAuth.setWhereToUse(type);
            smsAuthRepository.save(smsAuth);
            blockDuplicateCode(smsAuth.getSecret()); // 코드 중복 인증 방지 함수 추가
            log.info("callNumber : {} , secureNumber : {} , type : {}", callNumber, smsAuth.getAuthNumber(), type);
            return new ResponseEntity<>(new DefaultResponseDto(200, "인증번호가 전송되었습니다."), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DefaultResponseDto(409, "전송에 실패하였습니다."), HttpStatus.CONFLICT);
    }

    // 회원 탈퇴시 보내는 문자 인증번호
    @Transactional
    public ResponseEntity<?> deleteSendAuthNumber(String callNumber, HttpServletRequest httpServletRequest) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException, JsonProcessingException, URISyntaxException {
        PetmilyUsers authUser = petmilyRepository.findByUserPhoneNumber(aes256Cipher.AES_Encode(callNumber));
        log.info("회원탈퇴시 필요한 인증번호 전송 name : '{}' , callNumber : '{}'", authUser.getUserNickName(), callNumber);

        SmsAuth smsAuth = new SmsAuth(getSecureNumber());
        Bucket bucket = pricingPlanService.smsBucket(callNumber);
        if (bucket.tryConsume(1)) { // 1개 사용 요청
            // 초과하지 않음
            log.info("회원 탈퇴 SMS 초과 안함");
        } else {
            // 제한 초과
            log.info("회원 탈퇴 SMS {} 트래픽 초과!!!", httpServletRequest.getRemoteAddr());
            return new ResponseEntity<>(new DefaultResponseDto(409, "인증 문자는 30초에 한번 씩 보낼 수 있습니다."), HttpStatus.CONFLICT);
        }
        SendSmsResponseDto sendSmsResponseDto = smsService.sendSms(callNumber, "[펫밀리]\n" + authUser.getUserNickName() + "님\n탈퇴 인증번호는 " + smsAuth.getAuthNumber() + " 입니다.");
        if (sendSmsResponseDto.getStatusCode().equals("202")) {
            log.info("회원 탈퇴 문자 정상 전송 '{}'", authUser.getUserNickName());
            smsAuth.setSecret(createSecret(authUser.getUserNickName(), callNumber, smsAuth.getAuthNumber()));
            smsAuth.setWhereToUse("회원탈퇴");
            smsAuthRepository.save(smsAuth);
            blockDuplicateCode(smsAuth.getSecret()); // 코드 중복 인증 방지 함수 추가
            return new ResponseEntity<>(new DefaultResponseDto(200, "인증번호가 전송되었습니다."), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DefaultResponseDto(409, "전송에 실패하였습니다."), HttpStatus.CONFLICT);
    }

    public String createSecret(String name, String callNumber, int authNumber) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return aes256Cipher.AES_Encode(name + ":" + callNumber + ":" + authNumber);
    }

    public void doCertificated(SmsAuth smsAuth, int id) throws ParseException {
        smsAuth.setCanUse(false);
        smsAuth.setCertifiedDate(new DateCreator().getTimestamp());
        smsAuth.setUserId(id);
        save(smsAuth);
    }

    private void blockDuplicateCode(String secret) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        List<SmsAuth> smsAuthList = findAllByCanUse();
        // 사이즈가 2이상일 경우 처리해주면 된다. 그보다 낮을때는 블락시킬 것이 당연히 없다.
        Map<String, Boolean> list = new HashMap<>();
        if (smsAuthList != null && smsAuthList.size() >= 2) {
            log.info("중복인증 방지 함수 작동! '{}'", secret);
            for (SmsAuth smsAuth : smsAuthList) {
                // callNumber 가져온다.
                // Map에 일단 등록한다.
                // 이후에 돌다가 또 뭐가 보여?
                // 바로 sms.canuse false로 돌려버린다!
                String decryptedSecret = aes256Cipher.AES_Decode(smsAuth.getSecret());
                String[] decryptedSecretArray = decryptedSecret.split(":");
                if (list.get(decryptedSecretArray[1]) != null) {
                    smsAuth.setCanUse(false);
                    save(smsAuth);
                }
                list.put(decryptedSecretArray[1], true);
            }
        }
    }

    private int getSecureNumber() {
        int secureNumber;
        do {
            secureNumber = new SecureRandom().nextInt(999999);
        } while (secureNumber < 100000);
        return secureNumber;
    }
}