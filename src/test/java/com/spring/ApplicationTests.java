package com.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spring.dto.requestDto.EmailSenderRequestDto;
import com.spring.service.EmailService;
import com.spring.service.SmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@SpringBootTest
class ApplicationTests {


    @Autowired
    private EmailService emailService;

    @Test
    public void sendEmail() throws Exception {
        // 변수는 차례대로 받는사람의 메일주소, 제목, 내용 입니다.
        emailService.sendEmail(new EmailSenderRequestDto("받는사람이메일주소@naver.com", "안녕하십니까", "잘 보내집니다."));
    }

    @Autowired
    private SmsService smsService;

    @Test
    void contextLoads() {
        System.out.println("asdasd");
    }

    @Test
    void sendSms() throws JsonProcessingException, ParseException, UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException {
        smsService.sendSms("01011112222","오예");
    }

}
