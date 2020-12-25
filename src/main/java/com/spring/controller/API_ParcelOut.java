package com.spring.controller;

import com.spring.domain.*;
import com.spring.dto.requestDto.ParcelOutRequestDto;
import com.spring.service.ParcelOutService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Api(value = "이거저거 테스트해보는 API", tags = "분양 게시글")
@RequestMapping("api/v1/parcelOut")
@Log4j2

public class API_ParcelOut {
    private final ParcelOutService parcelOutService;
    // C
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 저장 후 게시글 내용 반환", response = Location2.class)
    })
    @ApiOperation(value = "분양글 작성", notes = "")
    @PostMapping("writeParcelOut")
    public ResponseEntity<?> writeParcelOut(@RequestBody ParcelOutRequestDto parcelOutRequestDto){
        return parcelOutService.writeParcelOut(parcelOutRequestDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 게시글 내용 반환", response = Location2.class)
    })
    @ApiOperation(value = "분양글 조회", notes = "")
    @GetMapping("getParcelOut")
    public ResponseEntity<?> getParcelOut(@RequestParam int id){
        return parcelOutService.getParcelOut(id);
    }


}