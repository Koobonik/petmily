package com.spring.configuration.web;

import com.spring.service.PricingPlanService;
import io.github.bucket4j.Bucket;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Log4j2
public class HttpInterceptor extends HandlerInterceptorAdapter {
	PricingPlanService pricingPlanService = new PricingPlanService();
	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response,
							 Object handler) {
		Bucket bucket = pricingPlanService.resolveBucket(request);
		log.info("================ Before Method");
		log.info("접속 ip 주소 '{}'", request.getRemoteAddr());
		log.info(request.getRemoteAddr());

		if (bucket.tryConsume(1)) { // 1개 사용 요청
			// 초과하지 않음
			log.info("초과 안함");
			return true;
		} else {
			// 제한 초과
			log.info("{} 트래픽 초과!!!", request.getRemoteAddr());
			return false;
		}
	}
	@Override
	public void postHandle( HttpServletRequest request,
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
}