package com.spring.service;

import com.spring.domain.PetmilyRepository;
import com.spring.domain.PetmilyUsers;
import com.spring.domain.SmsAuth;
import com.spring.dto.requestDto.SignUpRequestDto;
import com.spring.dto.requestDto.ValidateAuthNumberRequestDto;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.util.DateCreator;
import com.spring.util.ValidSomething;
import com.spring.util.cryptors.AES256Cipher;
import com.spring.util.cryptors.UnidirectionalEncrypt;
import com.spring.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
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
    private boolean isExitsPhoneNumber(String phoneNumber) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("유저 존재 유무 확인");
        return petmilyRepository.findByUserPhoneNumber(aes256Cipher.AES_Encode(phoneNumber)) != null;
    }

    public PetmilyUsers save(PetmilyUsers petmilyUsers){
        log.info("유저 저장 '{}'", petmilyUsers.getUserNickName());
        return petmilyRepository.save(petmilyUsers);
    }

    @Transactional
    public ResponseEntity<?> saveUser(SignUpRequestDto signUpRequestDto) throws Exception {
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


        smsAuthService.doCertificated(smsAuth,petmilyUsers.getId());
        return new ResponseEntity<>(jwtTokenProvider.createTokens(petmilyUsers.getUserPhoneNumber(), petmilyUsers.getRoles()), HttpStatus.OK);
    }
}
