package com.spring.controller;

import com.spring.domain.Location1;
import com.spring.domain.Location1Repository;
import com.spring.domain.Location2;
import com.spring.domain.Location2Repository;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.service.ParcelOutService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@Api(value = "이거저거 테스트해보는 API", tags = "펫 관련")
@RequestMapping("api/v1/parcelOut")
@Log4j2
public class API_Pets {
    // 동물(견종)에 대한 데이터를 반환해야함
    private final ParcelOutService parcelOutService;
    private final Location1Repository location1Repository;
    private final Location2Repository location2Repository;
}