package com.spring.configuration.web;

import com.spring.util.jwt.JwtTokenProvider;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Log4j2
@RequiredArgsConstructor
public class HttpInterceptor extends HandlerInterceptorAdapter {
    PricingPlanService pricingPlanService = new PricingPlanService();
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String jwt = request.getHeader("JWT");
        String remoteAddr = request.getHeader("Remote-Address");
        if (tokenFilter(request) && jwt != null && !jwt.equals("undefined")) {
            Bucket bucket = pricingPlanService.jwtBucket(jwt);
            log.info("jwt값 : '{}'", jwt);
            if (!jwtTokenProvider.validateToken(jwt)) {
                log.info("토큰만료 : '{}'", jwt);
                response.setStatus(401);
                return false;
            }
            return validOverTraffic(bucket, jwt);

        } else if (remoteAddr != null && !remoteAddr.equals("undefined")) {
            Bucket bucket = pricingPlanService.remoteAddressBucket(remoteAddr);
            log.info("ip주소 : '{}'", remoteAddr);

            return validOverTraffic(bucket, remoteAddr);
        }
        log.info("host : '{}'", request.getRemoteHost());
        Bucket bucket = pricingPlanService.resolveBucket(request);
        return validOverTraffic(bucket, request.getRemoteHost());
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        log.info("================ Method Executed");
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        log.info("================ Method Completed");
    }

    private boolean validOverTraffic(Bucket bucket, String data) {
        if (bucket.tryConsume(1)) { // 1개 사용 요청
            // 초과하지 않음
            return true;
        } else {
            // 제한 초과
            log.info("{} 트래픽 초과!!!", data);
            return false;
        }
    }

    // 밑에 있는 uri는 토큰 필터를 거치치않는다는 뜻
    public static boolean tokenFilter(HttpServletRequest re) {
        if (re.getRequestURI().contains("user/login") ||
                re.getRequestURI().contains("loginManager") ||
                re.getRequestURI().contains("logout") ||
                //re.getRequestURI().contains("renewalToken")||
                re.getRequestURI().contains("sendAuthNumber") ||
                re.getRequestURI().contains("jwtValidation") ||
                re.getRequestURI().contains("validateAuthNumber") ||
                re.getRequestURI().contains("region/region1") ||
                re.getRequestURI().contains("region/region2") ||
                re.getRequestURI().contains("user/signUp") ||
                re.getRequestURI().contains("find/")) {
            return false;
        }
        return true;
    }
}