package com.spring.service;

import com.spring.domain.PetmilyRepository;
import com.spring.domain.PetmilyUsers;
import com.spring.domain.SmsAuth;
import com.spring.dto.requestDto.LoginRequestDto;
import com.spring.dto.requestDto.SignUpRequestDto;
import com.spring.dto.requestDto.ValidateAuthNumberRequestDto;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.dto.responseDto.JwtResponseDto;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

    public PetmilyUsers save(PetmilyUsers petmilyUsers){
        log.info("유저 저장 '{}'", petmilyUsers.getUserNickName());
        return petmilyRepository.save(petmilyUsers);
    }

    public PetmilyUsers findByUserPhoneNumber(String phoneNumber) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return petmilyRepository.findByUserPhoneNumber(aes256Cipher.AES_Encode(phoneNumber));
    }

    public PetmilyUsers findByUserEmail(String email) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return petmilyRepository.findByUserEmail(aes256Cipher.AES_Encode(email));
    }

//    public PetmilyUsers findByUserEmailAndUserLoginPassword(String id, String password) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
//        return petmilyRepository.findByUserEmailAndUserLoginPassword(aes256Cipher.AES_Encode(id),unidirectionalEncrypt.matches(password) )
//    }

    @Transactional
    public ResponseEntity<?> saveUser(SignUpRequestDto signUpRequestDto) throws Exception {
        if(!ValidSomething.isValidNumber(signUpRequestDto.getUserPhoneNumber())){
            return new ResponseEntity<>(new DefaultResponseDto(409, "숫자만 입력해주세요."), HttpStatus.CONFLICT);
        }
        if(!ValidSomething.isValidPassword(signUpRequestDto.getUserLoginPassword())){
            return new ResponseEntity<>(new DefaultResponseDto(409, "비밀번호 양식을 벗어났습니다. 8~32자 이내로 영문+숫자+특수문자를 조합하여 입력해주세요"), HttpStatus.CONFLICT);
        }
        if(isExitsPhoneNumber(signUpRequestDto.getUserPhoneNumber())){
            return new ResponseEntity<>(new DefaultResponseDto(409, "이미 사용중인 휴대폰 번호 입니다."), HttpStatus.CONFLICT);
        }
        ValidateAuthNumberRequestDto validateAuthNumberRequestDto = new ValidateAuthNumberRequestDto();
        validateAuthNumberRequestDto.setUserName(signUpRequestDto.getUserNickName());
        validateAuthNumberRequestDto.setCallNumber(signUpRequestDto.getUserPhoneNumber());
        validateAuthNumberRequestDto.setAuthSms(signUpRequestDto.getSmsAuthNumber());
        SmsAuth smsAuth = smsAuthService.findBySecret(smsAuthService.createSecret(signUpRequestDto.getUserNickName(), signUpRequestDto.getUserPhoneNumber(), signUpRequestDto.getSmsAuthNumber()));
        if(smsAuthService.validateAuthNumber(validateAuthNumberRequestDto).getStatusCodeValue() != 200){
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
        petmilyUsers.setLastLoginDateTime(new DateCreator().getTimestamp());

        smsAuthService.doCertificated(smsAuth,petmilyUsers.getId());
        save(petmilyUsers);
        return new ResponseEntity<>(jwtTokenProvider.createTokens(petmilyUsers.getUserPhoneNumber(), petmilyUsers.getRoles()), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> login(LoginRequestDto loginRequestDto) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException {
        PetmilyUsers petmilyUsers = null;
        // 이메일인지 검증
        if(ValidSomething.isValidEmail(loginRequestDto.getID())){
            petmilyUsers = findByUserEmail(loginRequestDto.getID());
        }
        else if(ValidSomething.isValidNumber(loginRequestDto.getID())){
            petmilyUsers = findByUserPhoneNumber(loginRequestDto.getID());
        }

        // 유저 정보 일치한다는 뜻
        if(petmilyUsers != null && unidirectionalEncrypt.matches(loginRequestDto.getUserPassword(), petmilyUsers.getUserLoginPassword())){
            log.info("유저 있으므로 반환");
            petmilyUsers.setLastLoginDateTime(new DateCreator().getTimestamp());
            save(petmilyUsers);
            return new ResponseEntity<>(jwtTokenProvider.createTokens(petmilyUsers.getUserPhoneNumber(), petmilyUsers.getRoles()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DefaultResponseDto(404, "해당되는 계정을 찾을 수 없습니다."), HttpStatus.NOT_FOUND);
    }

    // 토큰 갱신
    public ResponseEntity<?> renewalToken(String token) throws ParseException {
        log.info("토큰 재발급! '{}' 유효한 값 '{}'", token, jwtTokenProvider.validateToken(token));
        if(jwtTokenProvider.validateToken(token)) {
            log.info("권한 통과");
            PetmilyUsers petmilyUsers = ((PetmilyUsers) jwtTokenProvider.getAuthentication(token).getPrincipal());
            if(petmilyUsers.getIsOut()){
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
        log.info("refreshToken 토큰 유효하지 않아서 401 반환");
        return new ResponseEntity<>( new DefaultResponseDto(401, "토큰이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
    }
    @Transactional
    public void invalidationToken(String token){
        ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
        PetmilyUsers petmilyUsers = (PetmilyUsers) jwtTokenProvider.getAuthentication(token).getPrincipal();
        logoutValueOperations.set(token, String.valueOf(petmilyUsers.getId())); // redis set 명령어

        log.info("토큰 무효화! 유저 아이디 : '{}' , 유저 이름 : '{}'", petmilyUsers.getId(), petmilyUsers.getUserNickName());
    }
}
