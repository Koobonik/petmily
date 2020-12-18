package com.spring;

import com.spring.util.cryptors.AES256Cipher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class ApplicationTests {

    @Autowired
    AES256Cipher aes256Cipher;
    @Test
    public void asd() throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        System.out.println(aes256Cipher.AES_Decode("66Pt4I7SrkIU9a3BeauAOn2UpvJ8P+qOIX/2wxo5EnU="));
    }

}
