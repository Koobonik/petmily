package com.spring.util.cryptors;

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class UnidirectionalEncrypt {
    private final Pbkdf2PasswordEncoder pbkdf2PasswordEncoder;

    public UnidirectionalEncrypt(){
        this.pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();
        this.pbkdf2PasswordEncoder.setAlgorithm(Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        this.pbkdf2PasswordEncoder.setEncodeHashAsBase64(true);
    }

    public boolean matches(String rawPassword, String encodedPassword){
        return pbkdf2PasswordEncoder.matches(rawPassword, encodedPassword);
    }

    public String encode(String rawPassword){
        return pbkdf2PasswordEncoder.encode(rawPassword);
    }
}
