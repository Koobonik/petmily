package com.spring.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PricingPlanService {
 
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private String getHost(HttpServletRequest httpServletRequest){
        return httpServletRequest.getHeader("Host");
    }

    /* ---------------------- 접속 제한! ----------------- */
    public Bucket resolveBucket(HttpServletRequest httpServletRequest) {
        return cache.computeIfAbsent(getHost(httpServletRequest), this::newBucket);
    }
 
    private Bucket newBucket(String apiKey) {
        return Bucket4j.builder()
                // 10개의 클라이언트가 10초에 1000개씩 보낼 수 있는 대역폭
            .addLimit(Bandwidth.classic(1000, Refill.intervally(10, Duration.ofSeconds(10))))
            .build();
    }


    /* ---------------------- sms 문자 제한! ----------------- */
    public Bucket smsBucket(String callNumber) {
        return cache.computeIfAbsent(callNumber, this::newSmsBucket);
    }

    private Bucket newSmsBucket(String apiKey){
        return Bucket4j.builder()
                // 10개의 클라이언트가 1에 10개씩 보낼 수 있는 대역폭
                .addLimit(Bandwidth.classic(1, Refill.intervally(10, Duration.ofMinutes(1))))
                .build();
    }
}