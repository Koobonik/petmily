package com.spring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.dto.requestDto.MessagesRequestDto;
import com.spring.dto.requestDto.SmsRequestDto;
import com.spring.dto.responseDto.GeocodingResponseDto;
import com.spring.dto.responseDto.NaverLocationResponseDto;
import com.spring.dto.responseDto.SendSmsResponseDto;
import com.spring.util.DateCreator;
import com.spring.util.yml.ApplicationGeocoding;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Component
@Log4j2
public class LocationSearchService {

    private final ApplicationGeocoding applicationGeocoding;


    public ResponseEntity<GeocodingResponseDto> searchLocation(String query) throws URISyntaxException {


        // 헤더에서 여러 설정값들을 잡아준다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-APIGW-API-KEY-ID", applicationGeocoding.getKeyid());
        headers.set("X-NCP-APIGW-API-KEY", applicationGeocoding.getKey());

        // 위에서 조립한 jsonBody와 헤더를 조립한다.
        HttpEntity<String> body = new HttpEntity<>( headers);
        log.info("헤더 : '{}'",body);
        // restTemplate로 post 요청을 보낸다. 별 일 없으면 202 코드 반환된다.
        RestTemplate restTemplate = new RestTemplate();
        query = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = URLEncoder.encode("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="+query, StandardCharsets.UTF_8);
        log.info("query -> '{}'", query);
        ResponseEntity<GeocodingResponseDto> geocodingResponseDto = restTemplate.exchange(new URI("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="+query), HttpMethod.GET, body, GeocodingResponseDto.class);
//        ResponseEntity<GeocodingResponseDto> geocodingResponseDto = restTemplate.exchange(url, HttpMethod.GET, body, GeocodingResponseDto.class);
        System.out.println(geocodingResponseDto.getStatusCodeValue());
        return geocodingResponseDto;
    }


    public NaverLocationResponseDto searchLocation2(String query) throws URISyntaxException {


        // 헤더에서 여러 설정값들을 잡아준다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", applicationGeocoding.getClientid());
        headers.set("X-Naver-Client-Secret", applicationGeocoding.getClientsecret());

        // 위에서 조립한 jsonBody와 헤더를 조립한다.
        HttpEntity<String> body = new HttpEntity<>( headers);
        log.info("헤더 : '{}'",body);
        // restTemplate로 post 요청을 보낸다. 별 일 없으면 202 코드 반환된다.
        RestTemplate restTemplate = new RestTemplate();
        query = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = URLEncoder.encode("https://openapi.naver.com/v1/search/local.json"+query, StandardCharsets.UTF_8);
        log.info("query -> '{}'", query);
        ResponseEntity<NaverLocationResponseDto> naverLocationResponseDtoResponseEntity = restTemplate.exchange(new URI("https://openapi.naver.com/v1/search/local.json?query="+query+"&display=10&start=1&sort=random"), HttpMethod.GET, body, NaverLocationResponseDto.class);
//        ResponseEntity<GeocodingResponseDto> geocodingResponseDto = restTemplate.exchange(url, HttpMethod.GET, body, GeocodingResponseDto.class);
        System.out.println(naverLocationResponseDtoResponseEntity.getBody());
        return naverLocationResponseDtoResponseEntity.getBody();
    }

}
