package com.spring.util;

import com.spring.dto.responseDto.PublicKeyResponseDto;
import lombok.Getter;

import javax.servlet.http.HttpSession;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

@Getter
public class KeyCreator {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String publicKeyModulus;
    private final String publicKeyExponent;

    public KeyCreator() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.genKeyPair();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
        RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
        this.publicKeyModulus = publicSpec.getModulus().toString(16);
        this.publicKeyExponent = publicSpec.getPublicExponent().toString(16);
    }

    public PublicKeyResponseDto getPublicKey(HttpSession httpSession) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyCreator keyCreator = new KeyCreator();
        // httpSession(세션) : 서버단에서 관리! -> 개인키가 안전하게 보관됨 -> 이후에 자동적으로 만료되며 소멸되기에 관리에 용이함
        httpSession.setAttribute("privateKey", keyCreator.getPrivateKey());   // 세션에 개인키를 담아서 보관한다.
        httpSession.setAttribute("publicKey", keyCreator.getPublicKey());     // 세션에 공개키를 담아서 보관한다.
        return PublicKeyResponseDto.builder()
                .publicKey(keyCreator.getPublicKey().toString()) // SunRsaSign RSA private CRT key, 2048 bits
                .RSAExponent(keyCreator.getPublicKeyExponent())  // Exponent 값
                .RSAModulus(keyCreator.getPublicKeyModulus())    // Modulus 값
                .build();
    }
}
