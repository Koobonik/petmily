package com.spring.controller;

import com.spring.domain.Location1;
import com.spring.domain.Location1Repository;
import com.spring.domain.Location2;
import com.spring.domain.Location2Repository;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.service.LocationSearchService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@Api(value = "이거저거 테스트해보는 API", tags = "지역")
@RequestMapping("api/v1/region")
@Log4j2
public class API_Region {
    private final Location1Repository location1Repository;
    private final Location2Repository location2Repository;
    private final LocationSearchService locationSearchService;
        // 토큰 재발급

    // @ApiImplicitParams({@ApiImplicitParam(name = "refreshJwt", value = "로그인후 JWT 토큰을 발급받아야 합니다.", required = true, dataType = "String", paramType = "header")})
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "JWT (Json Web Token) 발행", response = Region.class),
//            @ApiResponse(code = 401, message = "토큰 유효하지 않음", response = DefaultResponseDto.class)
//    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "구, 군 반환", response = Location2.class)
    })
    @Transactional(readOnly = true)
    @ApiOperation(value = "대한민국의 지역(시, 도) 조회", notes = "")
    @GetMapping("region1")
    @Async
    public CompletableFuture<ResponseEntity<?>> region(){
        return CompletableFuture.completedFuture(new ResponseEntity<>(location1Repository.findAll(), HttpStatus.OK));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "구, 군 반환", response = Location1[].class)
    })
    @ApiOperation(value = "대한민국의 지역(구, 군) 조회", notes = "")
    @GetMapping("region2")
    @Transactional(readOnly = true)
    @Async
    public CompletableFuture<ResponseEntity<?>> city(@ApiParam(value = "지역이름 ex) 경기도", required = true, example = "경기도") @RequestParam String region){
        Location1 region1 = location1Repository.findByName(region);
        if(region1 == null) return CompletableFuture.completedFuture(new ResponseEntity<>(new DefaultResponseDto(409, region + " 에 매핑되는 구, 군이 없습니다."), HttpStatus.CONFLICT));
        return CompletableFuture.completedFuture(new ResponseEntity<>(location2Repository.findAllByRegion(region1.getId()), HttpStatus.OK));
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "위치에 대한 데이터 반환", response = Location1[].class)
    })
    @ApiOperation(value = "위치 반환", notes = "")
    @GetMapping("search")
    public ResponseEntity<?> search(@ApiParam(value = "지역이름 ex) 경기도", example = "경기도") @RequestParam(required = false) String query) throws URISyntaxException {
        log.info("'{}' 로 검색",query);
        return new ResponseEntity<>(locationSearchService.searchLocation2(query),HttpStatus.OK);
    }
}