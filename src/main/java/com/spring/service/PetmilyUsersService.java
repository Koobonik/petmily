package com.spring.service;

import com.spring.domain.PetmilyRepository;
import com.spring.domain.PetmilyUsers;
import com.spring.domain.SmsAuth;
import com.spring.dto.requestDto.*;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.dto.responseDto.JwtResponseDto;
import com.spring.dto.responseDto.UserResponseDto;
import com.spring.util.DateCreator;
import com.spring.util.ValidSomething;
import com.spring.util.cryptors.AES256Cipher;
import com.spring.util.cryptors.UnidirectionalEncrypt;
import com.spring.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Log4j2
public class PetmilyUsersService {
    private final PetmilyRepository petmilyRepository;
    private final AES256Cipher aes256Cipher;
    private final SmsAuthService smsAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UnidirectionalEncrypt unidirectionalEncrypt = new UnidirectionalEncrypt();
    @Autowired
    StringRedisTemplate redisTemplate;

    private boolean isExitsPhoneNumber(String phoneNumber) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("유저 존재 유무 확인");
        return petmilyRepository.findByUserPhoneNumber(aes256Cipher.AES_Encode(phoneNumber)) != null;
    }

    public PetmilyUsers save(PetmilyUsers petmilyUsers) {
        log.info("유저 저장 '{}'", petmilyUsers.getUserNickName());
        return petmilyRepository.save(petmilyUsers);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PetmilyUsers findByUserPhoneNumber(String phoneNumber) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("폰 번호로 유저 찾기 '{}'", phoneNumber);
        return petmilyRepository.findByUserPhoneNumber(aes256Cipher.AES_Encode(phoneNumber));
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PetmilyUsers findByUserEmail(String email) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("이메일 주소로 유저 찾기 '{}'", email);
        return petmilyRepository.findByUserEmail(aes256Cipher.AES_Encode(email));
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public boolean findCheckUserNickName(String nickName) {
        log.info("유저 닉네임 사용 가능한지 조회 '{}'", nickName);
        return petmilyRepository.findByUserNickName(nickName) == null;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PetmilyUsers findByUserNickNameAndUserPhoneNumber(String nickName, String phoneNumber) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("닉네임과 폰 번호로 유저 찾기 '{}' : '{}'", nickName, phoneNumber);
        return petmilyRepository.findByUserNickNameAndUserPhoneNumber(nickName, aes256Cipher.AES_Encode(phoneNumber));
    }

    //    public PetmilyUsers findByUserEmailAndUserLoginPassword(String id, String password) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
//        return petmilyRepository.findByUserEmailAndUserLoginPassword(aes256Cipher.AES_Encode(id),unidirectionalEncrypt.matches(password) )
//    }
    public ResponseEntity<?> checkPassword(String password) {
        if (ValidSomething.isValidPassword(password)) {
            return new ResponseEntity<>(new DefaultResponseDto(200, "사용 가능한 비밀번호 입니다."), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DefaultResponseDto(409, "비밀번호 양식을 벗어났습니다. 8~32자 이내로 영문+숫자+특수문자를 조합하여 입력해주세요"), HttpStatus.CONFLICT);
    }

    @Transactional
    public ResponseEntity<?> saveUser(SignUpRequestDto signUpRequestDto) throws Exception {
        if (!ValidSomething.isValidNumber(signUpRequestDto.getUserPhoneNumber())) {
            return new ResponseEntity<>(new DefaultResponseDto(409, "숫자만 입력해주세요."), HttpStatus.CONFLICT);
        }
        if (!ValidSomething.isValidPassword(signUpRequestDto.getUserLoginPassword())) {
            return new ResponseEntity<>(new DefaultResponseDto(409, "비밀번호 양식을 벗어났습니다. 8~32자 이내로 영문+숫자+특수문자를 조합하여 입력해주세요"), HttpStatus.CONFLICT);
        }
        if (isExitsPhoneNumber(signUpRequestDto.getUserPhoneNumber())) {
            return new ResponseEntity<>(new DefaultResponseDto(409, "이미 사용중인 휴대폰 번호 입니다."), HttpStatus.CONFLICT);
        }
        ValidateAuthNumberRequestDto validateAuthNumberRequestDto = new ValidateAuthNumberRequestDto();
        validateAuthNumberRequestDto.setUserName(signUpRequestDto.getUserNickName());
        validateAuthNumberRequestDto.setCallNumber(signUpRequestDto.getUserPhoneNumber());
        validateAuthNumberRequestDto.setAuthSms(signUpRequestDto.getSmsAuthNumber());
        SmsAuth smsAuth = smsAuthService.findBySecret(smsAuthService.createSecret(signUpRequestDto.getUserNickName(), signUpRequestDto.getUserPhoneNumber(), signUpRequestDto.getSmsAuthNumber()));
        if (smsAuthService.validateAuthNumber(validateAuthNumberRequestDto).getStatusCodeValue() != 200) {
            return smsAuthService.validateAuthNumber(validateAuthNumberRequestDto);
        }
        //smsAuthService.doCertificated();
        UnidirectionalEncrypt unidirectionalEncrypt = new UnidirectionalEncrypt();
        Timestamp today = new DateCreator().getTimestamp();
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");


        PetmilyUsers petmilyUsers = signUpRequestDto.toEntity(signUpRequestDto);
        log.info("비밀번호 {}", petmilyUsers.getUserLoginPassword());
        petmilyUsers.setUserLoginPassword(unidirectionalEncrypt.encode(petmilyUsers.getUserLoginPassword()));
        petmilyUsers.setUserPhoneNumber(aes256Cipher.AES_Encode(petmilyUsers.getUserPhoneNumber()));
        petmilyUsers.setLastLoginDateTime(today);
        petmilyUsers.setSignUpDateTime(today);
        petmilyUsers.setIsOut(false);
        petmilyUsers.setRoles(roles);
        smsAuthService.doCertificated(smsAuth, petmilyUsers.getId());
        save(petmilyUsers);
        return new ResponseEntity<>(jwtTokenProvider.createTokens(petmilyUsers.getUserPhoneNumber(), petmilyUsers.getRoles()), HttpStatus.OK);
    }

    // 토큰으로 유저 데이터 반환해주는거 만들자 // 뷰 만들거나 dto
    @SneakyThrows
    public UserResponseDto getUserDataUsingToken(HttpServletRequest httpServletRequest) {
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        log.info("유저 정보 반환 '{}'", petmilyUsers.getUserNickName());
        return new UserResponseDto().builder()
                .userPhoneNumber(aes256Cipher.AES_Decode(petmilyUsers.getUserPhoneNumber()))
                .userEmail(petmilyUsers.getUserEmail() != null ? aes256Cipher.AES_Decode(petmilyUsers.getUserEmail()) : "")
                .userFirebaseToken(petmilyUsers.getUserFirebaseToken())
                .userNickName(petmilyUsers.getUserNickName())
                .userImageUrl(petmilyUsers.getUserImageUrl() == null ? "https://firebasestorage.googleapis.com/v0/b/petmily-dab67.appspot.com/o/Pets%2F%EB%8C%80%EB%B0%95%EC%9D%B4.jpg?alt=media&token=330f86c3-acbb-4a27-b27d-357599852ed8" : petmilyUsers.getUserImageUrl())
                .pets(petmilyUsers.getPets())
                .roles(petmilyUsers.getRoles())
                .build();
    }

    @Transactional
    public ResponseEntity<?> login(LoginRequestDto loginRequestDto) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException {
        PetmilyUsers petmilyUsers = null;
        // 이메일인지 검증
        if (ValidSomething.isValidNumber(loginRequestDto.getId())) {
            petmilyUsers = findByUserPhoneNumber(loginRequestDto.getId());
        } else if (ValidSomething.isValidEmail(loginRequestDto.getId())) {
            petmilyUsers = findByUserEmail(loginRequestDto.getId());
        }

        // 유저 정보 일치한다는 뜻
        if (petmilyUsers != null && unidirectionalEncrypt.matches(loginRequestDto.getUserPassword(), petmilyUsers.getUserLoginPassword())) {
            log.info("유저 있으므로 반환");
            petmilyUsers.setLastLoginDateTime(new DateCreator().getTimestamp());
            save(petmilyUsers);
            return new ResponseEntity<>(jwtTokenProvider.createTokens(petmilyUsers.getUserPhoneNumber(), petmilyUsers.getRoles()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DefaultResponseDto(404, "해당되는 계정을 찾을 수 없습니다."), HttpStatus.NOT_FOUND);
    }

    // 토큰 갱신
    public ResponseEntity<?> renewalToken(String token) throws ParseException {
        if (jwtTokenProvider.tokenIsInvalidation(token).getStatusCodeValue() != 202) {
            log.info("리뉴얼 토큰 만료됨");
            return jwtTokenProvider.tokenIsInvalidation(token);
        }
        log.info("권한 통과");
        PetmilyUsers petmilyUsers = ((PetmilyUsers) jwtTokenProvider.getAuthentication(token).getPrincipal());
        if (petmilyUsers.getIsOut()) {
            log.info("계정의 is_active 값이 0 임");
            return new ResponseEntity<>(new DefaultResponseDto(409, "활성화 되지 않은 계정입니다."), HttpStatus.CONFLICT);
        }
        invalidationToken(token);
        log.info("새로운 리뉴얼 토큰 발행");
        JwtResponseDto jwtResponseDto = jwtTokenProvider.createTokens(petmilyUsers.getUserPhoneNumber(), petmilyUsers.getRoles());
        log.info("jwt 토큰값 '{}'\nrefreshJWT 토큰값 '{}'", jwtResponseDto.getJwt(), jwtResponseDto.getRefreshJwt());
        petmilyUsers.setLastLoginDateTime(new DateCreator().getTimestamp());
        save(petmilyUsers);
        return new ResponseEntity<>(jwtResponseDto, HttpStatus.OK);

    }

    // 로그아웃
    @Transactional
    public ResponseEntity<?> logout(JwtRequestDto jwtRequestDto) {
        PetmilyUsers authUser = (PetmilyUsers) jwtTokenProvider.getAuthentication(jwtRequestDto.getJwt()).getPrincipal();
        invalidationToken(jwtRequestDto.getJwt());
        invalidationToken(jwtRequestDto.getRefreshJwt());
        log.info("로그아웃 유저 아이디 : '{}' , 유저 이름 : '{}'", authUser.getId(), authUser.getUserNickName());
        return new ResponseEntity<>(new DefaultResponseDto(200, "로그아웃 되었습니다."), HttpStatus.OK);
    }

    @Transactional
    public void invalidationToken(String token) {
        ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
        PetmilyUsers petmilyUsers = (PetmilyUsers) jwtTokenProvider.getAuthentication(token).getPrincipal();
        logoutValueOperations.set(token, String.valueOf(petmilyUsers.getId())); // redis set 명령어

        log.info("토큰 무효화! 유저 아이디 : '{}' , 유저 이름 : '{}'", petmilyUsers.getId(), petmilyUsers.getUserNickName());
    }

    @Transactional
    public ResponseEntity<?> resetPassword(NewPasswordRequestDto newPasswordRequestDto) throws NoSuchPaddingException, ParseException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        PetmilyUsers petmilyUsers = findByUserPhoneNumber(newPasswordRequestDto.getCallNumber());
        if (petmilyUsers == null)
            return new ResponseEntity<>(new DefaultResponseDto(409, "잘못된 정보 입니다."), HttpStatus.CONFLICT);
        ValidateAuthNumberRequestDto validateAuthNumberRequestDto = new ValidateAuthNumberRequestDto(newPasswordRequestDto.getAuthSms(), newPasswordRequestDto.getCallNumber(), petmilyUsers.getUserNickName());
        if (smsAuthService.validateAuthNumber(validateAuthNumberRequestDto).getStatusCodeValue() != 200) {
            return smsAuthService.validateAuthNumber(validateAuthNumberRequestDto);
        }
        log.info("코드 검증됨!");
        SmsAuth smsAuth = smsAuthService.findSmsAuth(newPasswordRequestDto.getAuthSms());
        UnidirectionalEncrypt unidirectionalEncrypt = new UnidirectionalEncrypt();
        petmilyUsers.setUserLoginPassword(unidirectionalEncrypt.encode(newPasswordRequestDto.getNewPassword()));
        smsAuthService.doCertificated(smsAuth, petmilyUsers.getId());
        return new ResponseEntity<>(new DefaultResponseDto(200, "성공적으로 비밀번호를 재설정 하였습니다."), HttpStatus.OK);
    }

    public ResponseEntity<?> checkUserNickName(String nickName) {
        if (!ValidSomething.canUseNickName(nickName)) {
            return new ResponseEntity<>(new DefaultResponseDto(409, "불용어가 포함되어 있습니다."), HttpStatus.CONFLICT);
        }
        if (!ValidSomething.isValidName(nickName)) {
            return new ResponseEntity<>(new DefaultResponseDto(409, "닉네임 양식을 벗어난 닉네임 입니다."), HttpStatus.CONFLICT);
        }
        if (findCheckUserNickName(nickName)) {
            return new ResponseEntity<>(new DefaultResponseDto(200, "사용 가능한 닉네임 입니다."), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new DefaultResponseDto(409, "이미 사용중인 닉네임 입니다."), HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity<?> validPassword(PasswordConfirmRequestDto passwordConfirmRequestDto, HttpServletRequest httpServletRequest) {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        if (jwtTokenProvider.validateToken(token)) {
            PetmilyUsers petmilyUsers = (PetmilyUsers) jwtTokenProvider.getAuthentication(token).getPrincipal();
            UnidirectionalEncrypt unidirectionalEncrypt = new UnidirectionalEncrypt();
            if (unidirectionalEncrypt.matches(passwordConfirmRequestDto.getUserLoginPassword(), petmilyUsers.getPassword())) {
                return new ResponseEntity<>(new DefaultResponseDto(200, "성공적으로 인증되었습니다."), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new DefaultResponseDto(409, "비밀번호가 일치하지 않습니다."), HttpStatus.CONFLICT);
            }
        }
        return jwtTokenProvider.tokenIsInvalidation(token);
    }
}
