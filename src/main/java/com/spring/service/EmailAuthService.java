package com.spring.service;

import com.spring.domain.*;
import com.spring.dto.requestDto.EmailAuthRequestDto;
import com.spring.dto.requestDto.EmailRequestDto;
import com.spring.dto.requestDto.ValidateAuthNumberRequestDto;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.util.DateCreator;
import com.spring.util.EmailServiceImpl;
import com.spring.util.ValidSomething;
import com.spring.util.cryptors.AES256Cipher;
import com.spring.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Log4j2
public class EmailAuthService {

    private final EmailServiceImpl emailServiceImpl;
    private final JwtTokenProvider jwtTokenProvider;
    private final AES256Cipher aes256Cipher;
    private final EmailAuthCodeRepository emailAuthCodeRepository;
    private final PetmilyRepository petmilyRepository;
    public void save(EmailAuthCode emailAuthCode){
        emailAuthCodeRepository.save(emailAuthCode);
    }
    public List<EmailAuthCode> findAllByCanUse(){
        return emailAuthCodeRepository.findAllByCanUse();
    }
    public EmailAuthCode findBySecret(String secret){
        return emailAuthCodeRepository.findBySecret(secret);
    }

    public EmailAuthCode findSmsAuth(int authNumber){
        return emailAuthCodeRepository.findByAuthNumber(authNumber);
    }

    @Transactional
    @SneakyThrows
    public ResponseEntity<?> sendEmailForAuthEmail(EmailRequestDto emailRequestDto, HttpServletRequest httpServletRequest){
        if(!ValidSomething.isValidEmail(emailRequestDto.getRecipient())){
            return new ResponseEntity<>(new DefaultResponseDto(409, "이메일이 아닙니다."), HttpStatus.CONFLICT);
        }
        EmailAuthCode emailAuthCode = new EmailAuthCode(getSecureNumber());
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        emailAuthCode.setUserId(petmilyUsers.getId());
        emailAuthCode.setWhereToUse("이메일인증");
        emailAuthCode.setSecret(createSecret(petmilyUsers.getUserNickName(), aes256Cipher.AES_Decode(petmilyUsers.getUserPhoneNumber()), emailAuthCode.getAuthNumber()));
        if(emailServiceImpl.sendSimpleMessage(emailRequestDto.getRecipient(), "[펫밀리]이메일 인증번호 입니다.",
                "이메일 인증번호 : [" + emailAuthCode.getAuthNumber() + "]를 앱에 입력해주세요.")){
            emailAuthCodeRepository.save(emailAuthCode);
            blockDuplicateCode(emailAuthCode.getSecret());
            return new ResponseEntity<>(new DefaultResponseDto(200,"성공적으로 이메일을 발송하였습니다."), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DefaultResponseDto(500,"이메일 발송에 에러가 발생하였습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SneakyThrows
    @Transactional
    public ResponseEntity<?> authEmail(EmailAuthRequestDto emailAuthRequestDto, HttpServletRequest httpServletRequest){
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        EmailAuthCode emailAuthCode = emailAuthCodeRepository.findByAuthNumber(emailAuthRequestDto.getAuthNumber());
        ValidateAuthNumberRequestDto validateAuthNumberRequestDto = new ValidateAuthNumberRequestDto();
        validateAuthNumberRequestDto.setAuthSms(emailAuthCode.getAuthNumber());
        validateAuthNumberRequestDto.setCallNumber(aes256Cipher.AES_Decode(petmilyUsers.getUserPhoneNumber()));
        validateAuthNumberRequestDto.setUserName(petmilyUsers.getUserNickName());
        if(validateAuthNumber(validateAuthNumberRequestDto).getStatusCodeValue() != 200) {
            log.info("이메일이 유효하지 않음");
            return validateAuthNumber(validateAuthNumberRequestDto);
        }

        String secret = aes256Cipher.AES_Decode(emailAuthCode.getSecret());
        String[] str = secret.split(":");
        if(str[0].equals(petmilyUsers.getUserNickName()) && str[1].equals(aes256Cipher.AES_Decode(petmilyUsers.getUserPhoneNumber()))){
            petmilyUsers.setUserEmail(aes256Cipher.AES_Encode(emailAuthRequestDto.getRecipient()));
            petmilyRepository.save(petmilyUsers);
            doCertificated(emailAuthCode, petmilyUsers.getId());
            return new ResponseEntity<>(new DefaultResponseDto(200, "이메일이 성공적으로 등록되었습니다."), HttpStatus.OK);
        }

        return new ResponseEntity<>(new DefaultResponseDto(409, "인증정보가 유효하지 않습니다."), HttpStatus.CONFLICT);
    }


    @Transactional
    public ResponseEntity<?> validateAuthNumber(ValidateAuthNumberRequestDto validateAuthNumberRequestDto) throws ParseException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Timestamp today = new DateCreator().getTimestamp(new DateCreator().getSimpleDateFormat_yyyy_MM_dd_HH_mm_ss_SSS(), new DateCreator().getCreatedDate_yyyy_MM_dd_HH_mm_ss_SSS());
        // 휴대폰 번호도 양방향 암호화 하여 저장한다.
        String encrypted = aes256Cipher.AES_Encode(validateAuthNumberRequestDto.getUserName() + ":" + validateAuthNumberRequestDto.getCallNumber() + ":" + validateAuthNumberRequestDto.getAuthSms());
        EmailAuthCode emailAuthCode = findBySecret(encrypted);
        if (emailAuthCode != null) {
            log.info("매칭되는 데이터가 있음 {}:{}:{}", validateAuthNumberRequestDto.getUserName(), validateAuthNumberRequestDto.getCallNumber(), validateAuthNumberRequestDto.getAuthSms());
            if (!today.before(new DateCreator().getAfterThreeMinutes(emailAuthCode.getCreatedDate()))) {
                log.info("3분 지났다");
                return new ResponseEntity<>(new DefaultResponseDto(409, "인증시간이 초과되었습니다."), HttpStatus.CONFLICT);
            }
            if (!emailAuthCode.isCanUse()) {
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

    public String createSecret(String name, String callNumber, int authNumber) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return aes256Cipher.AES_Encode(name + ":" + callNumber + ":" + authNumber);
    }

    public void doCertificated(EmailAuthCode emailAuthCode, int id) throws ParseException {
        emailAuthCode.setCanUse(false);
        emailAuthCode.setCertifiedDate(new DateCreator().getTimestamp());
        emailAuthCode.setUserId(id);
        save(emailAuthCode);
    }

    private void blockDuplicateCode(String secret) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        List<EmailAuthCode> emailAuthCodeList = findAllByCanUse();
        // 사이즈가 2이상일 경우 처리해주면 된다. 그보다 낮을때는 블락시킬 것이 당연히 없다.
        Map<String, Boolean> list = new HashMap<>();
        if (emailAuthCodeList != null && emailAuthCodeList.size() >= 2) {
            log.info("중복인증 방지 함수 작동! '{}'", secret);
            for (EmailAuthCode emailAuthCode : emailAuthCodeList) {
                // callNumber 가져온다.
                // Map에 일단 등록한다.
                // 이후에 돌다가 또 뭐가 보여?
                // 바로 sms.canuse false로 돌려버린다!
                String decryptedSecret = aes256Cipher.AES_Decode(emailAuthCode.getSecret());
                String[] decryptedSecretArray = decryptedSecret.split(":");
                if (list.get(decryptedSecretArray[1]) != null) {
                    emailAuthCode.setCanUse(false);
                    save(emailAuthCode);
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