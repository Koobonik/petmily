package com.spring;

import com.spring.service.LocationSearchService;
import com.spring.util.ValidSomething;
import com.spring.util.cryptors.AES256Cipher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class ApplicationTests {

    @Autowired
    AES256Cipher aes256Cipher;

    @Autowired
    LocationSearchService locationSearchService;
    @Test
    public void asd() throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, URISyntaxException {
        System.out.println(aes256Cipher.AES_Decode("66Pt4I7SrkIU9a3BeauAOn2UpvJ8P+qOIX/2wxo5EnU="));
        System.out.println(ValidSomething.canUseNickName("구백군"));
        System.out.println(aes256Cipher.AES_Decode("VowNY3dnXQPCyMxYM6NKog=="));
        System.out.println(ValidSomething.isValidDate("2020-11-31"));
    }

}
