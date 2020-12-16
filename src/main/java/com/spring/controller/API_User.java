package com.spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spring.dto.requestDto.LoginRequestDto;
import com.spring.dto.requestDto.SendAuthNumberRequestDto;
import com.spring.dto.requestDto.SignUpRequestDto;
import com.spring.dto.requestDto.ValidateAuthNumberRequestDto;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.dto.responseDto.JwtResponseDto;
import com.spring.dto.responseDto.PublicKeyResponseDto;
import com.spring.service.PetmilyUsersService;
import com.spring.service.SmsAuthService;
import com.spring.util.jwt.JwtTokenProvider;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.Collections;

@Log4j2
@RestController
@RequiredArgsConstructor
@Api(value = "API", tags = "회원가입 및 로그인")
@RequestMapping("api/v1/user")
public class API_User {

    private final JwtTokenProvider jwtTokenProvider;
    private final SmsAuthService smsAuthService;
    private final PetmilyUsersService petmilyUsersService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "회원가입이 정상적으로 이루어졌을 경우", response = JwtResponseDto.class),
            @ApiResponse(code = 409, message = "회원가입 과정에서 에러가 있을경우", response = DefaultResponseDto.class),
    })
    @ApiOperation(value = "회원가입", notes = "회원가입 대한 요청을 보냅니다.")
    @PostMapping(value = "signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto signUpRequestDto) throws Exception {
        return petmilyUsersService.saveUser(signUpRequestDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 정상적으로 이루어졌을 경우", response = JwtResponseDto.class),
            @ApiResponse(code = 404, message = "매칭되는 계정이 없을경우", response = DefaultResponseDto.class),
    })
    @ApiOperation(value = "로그인", notes = "로그인 대한 요청을 보냅니다.\n" +
            "이메일 or 휴대폰 번호로 로그인이 가능합니다.")
    @PostMapping(value = "login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ParseException {
        return petmilyUsersService.login(loginRequestDto);
    }


    @PostMapping("jwtValidation")
    public ResponseEntity<?> jwtValidation(@RequestHeader @RequestParam String jwt){
        // 헤더에서 토큰값 추출
        log.info(jwt);
        // 토큰값이 유효한 경우
        if(jwtTokenProvider.validateToken(jwt)) {
            log.info("토큰 유효함");
            // 유저 정보 추출 (아이디)
            log.info(jwtTokenProvider.getUserPk(jwt));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    @Transactional
    @ApiOperation(value = "회원가입 sms 인증번호 요청", notes = "가입하려는 사람의 전화번호로 인증번호를 보냅니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "인증번호 정상적으로 전송", response = DefaultResponseDto.class),
            @ApiResponse(code = 409, message = "SMS를 보내는 과정에서 에러가 있을경우", response = DefaultResponseDto.class),
    })
    @PostMapping("/sendAuthNumber")
    public ResponseEntity<?> sendAuthNumber(@RequestBody SendAuthNumberRequestDto sendAuthNumberRequestDto) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException, JsonProcessingException, URISyntaxException, InvalidAlgorithmParameterException {
        return smsAuthService.sendAuthNumberForSignUp(sendAuthNumberRequestDto.getUserName(), sendAuthNumberRequestDto.getCallNumber());
    }

    @Transactional
    @ApiOperation(value = "회원가입 sms 인증번호 확인", notes = "인증번호를 확인하여 가입 가능 유무를 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "인증번호 정상적으로 확인", response = DefaultResponseDto.class),
            @ApiResponse(code = 409, message = "인증과정에서 에러가 있을 경우", response = DefaultResponseDto.class),
    })
    @PostMapping("/validateAuthNumber")
    public ResponseEntity<?> validateAuthNumber(@RequestBody ValidateAuthNumberRequestDto validateAuthNumberRequestDto) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException, JsonProcessingException, URISyntaxException, InvalidAlgorithmParameterException {
        return smsAuthService.validateAuthNumber(validateAuthNumberRequestDto);
    }

    // 토큰 재발급
    //@ApiImplicitParams({@ApiImplicitParam(name = "refreshJwt", value = "로그인후 JWT 토큰을 발급받아야 합니다.", required = true, dataType = "String", paramType = "header")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "JWT (Json Web Token) 발행", response = JwtResponseDto.class),
            @ApiResponse(code = 401, message = "토큰 유효하지 않음", response = DefaultResponseDto.class),
            @ApiResponse(code = 409, message = "유저 계정에 문제가 있을경우", response = DefaultResponseDto.class)
    })
    @ApiOperation(value = "토큰 재발급", notes = "파라미터(token)에 담겨오는 토큰값으로 jwt와 refreshJwt를 재발급 받는다.")
    @GetMapping("renewalToken")
    public ResponseEntity<?> renewalToken(@RequestParam String token) throws ParseException {
        log.info("renewalToken API를 이용하여 들어온 토큰 값 : {}", token);
        return petmilyUsersService.renewalToken(token);
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