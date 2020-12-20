package com.spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spring.dto.requestDto.*;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.dto.responseDto.JwtResponseDto;
import com.spring.service.EmailAuthService;
import com.spring.service.PetmilyUsersService;
import com.spring.service.SmsAuthService;
import com.spring.util.jwt.JwtTokenProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
import java.text.ParseException;

@Log4j2
@RestController
@RequiredArgsConstructor
@Api(value = "API", tags = "유저 정보 관련")
@RequestMapping("api/v1/auth")
public class API_Auth {

    private final PetmilyUsersService petmilyUsersService;
    private final EmailAuthService emailAuthService;

    // 비밀번호 찾기
    @ApiResponses({
            @ApiResponse(code = 200, message = "인증번호 입력과 함께 비밀번호를 성공적으로 변경하였을 경우", response = DefaultResponseDto.class)
    })
    @ApiOperation(value = "비밀번호 재설정하기", notes = "비밀번호를 재설정 할 수 있는 API 입니다.")
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody NewPasswordRequestDto newPasswordRequestDto) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ParseException, UnsupportedEncodingException {
        return petmilyUsersService.resetPassword(newPasswordRequestDto);
    }

    //
    @ApiOperation(value = "비밀번호 확인 API", notes = "토큰값과 매칭되는 유저를 찾고 비밀번호를 대조 하여 검증 합니다.\n" +
            "유효하다면 200을 반환합니다.\n" +
            "프론트엔드에서는 개인정보 수정 페이지를 보여줍니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "true/false 반환", response = DefaultResponseDto.class),
    })
    @PostMapping("/password")
    public ResponseEntity<?> password(@RequestBody PasswordConfirmRequestDto passwordConfirmRequestDto, HttpServletRequest httpServletRequest) {
        return petmilyUsersService.validPassword(passwordConfirmRequestDto, httpServletRequest);
    }

    // 이메일 등록하기위한 인증번호 전송
    @ApiOperation(value = "이메일로 인증번호 보내기", notes = "이메일 인증을 위해 인증번호를 보냅니다.\n" +
            "유저는 이메일로 온 인증번호를 앱 상에서 입력하여 이메일 인증을 완료합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이메일 전송", response = DefaultResponseDto.class),
            @ApiResponse(code = 500, message = "이메일 전송에 에러가 발생한 경우", response = DefaultResponseDto.class),
    })
    @PostMapping("/sendEmailAuthNumber")
    public ResponseEntity<?> sendEmailAuthNumber(@RequestBody EmailRequestDto emailRequestDto, HttpServletRequest httpServletRequest) {
        return emailAuthService.sendEmailForAuthEmail(emailRequestDto, httpServletRequest);
    }

    // 이메일 등록하기
    @ApiOperation(value = "이메일로 온 인증번호 입력하기", notes = "이메일로 온 인증번호를 앱 상에서 입력하여 이메일 인증을 완료합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "이메일 전송", response = DefaultResponseDto.class),
            @ApiResponse(code = 500, message = "이메일 전송에 에러가 발생한 경우", response = DefaultResponseDto.class),
    })
    @PostMapping("/emailAuth")
    public ResponseEntity<?> emailAuth(@RequestBody EmailAuthRequestDto emailAuthRequestDto, HttpServletRequest httpServletRequest) {
        return emailAuthService.authEmail(emailAuthRequestDto, httpServletRequest);
    }


//    // 공개키 발급
//    @ApiOperation(value = "공개키 api", notes = "로그인이나 회원가입시 쓰이는 공개키 가져오는 api")
//    @GetMapping("/getPublicKey")
//    public PublicKeyResponseDto getPublicKey(HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        HttpSession httpSession = request.getSession();
//        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
//        generator.initialize(2048);
//        KeyPair keyPair = generator.genKeyPair();
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
//        // httpSession(세션) : 서버단에서 관리! -> 개인키가 안전하게 보관됨 -> 이후에 자동적으로 만료되며 소멸되기에 관리에 용이함
//        // 회원가입에 성공하거나 로그인 했을경우에는 세션에서 개인키를 지워 주면 Best
//        httpSession.setAttribute("privateKey", privateKey);
//        log.info("개인키");
//        log.info(httpSession.getAttribute("privateKey"));
//        // 추출
//        RSAPublicKeySpec publicSpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
//        String publicKeyModulus = publicSpec.getModulus().toString(16);
//        String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
//        PublicKeyResponseDto publicKeyResponseDto = PublicKeyResponseDto.builder()
//                .publicKey(publicKey.toString())
//                .RSAExponent(publicKeyModulus)
//                .RSAModulus(publicKeyExponent)
//                .build();
//        log.info(publicKeyResponseDto);
//        return publicKeyResponseDto;
//    }




}
