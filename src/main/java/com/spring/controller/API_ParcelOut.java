package com.spring.controller;

import com.spring.domain.*;
import com.spring.dto.requestDto.IDRequestDto;
import com.spring.dto.requestDto.ParcelOutRequestDto;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.service.ParcelOutService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

@RestController
@AllArgsConstructor
@Api(value = "이거저거 테스트해보는 API", tags = "분양 게시글")
@RequestMapping("api/v1/parcelOut")
@Log4j2
public class API_ParcelOut {
    private final ParcelOutService parcelOutService;
    // 
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 저장 후 게시글에 대한 데이터 반환", response = ParcelOut.class)
    })
    @ApiOperation(value = "분양글 작성", notes = "")
    @PostMapping("postParcelOut")
    public ResponseEntity<?> writeParcelOut(@RequestBody ParcelOutRequestDto parcelOutRequestDto, HttpServletRequest httpServletRequest) throws ParseException {
        return parcelOutService.postParcelOut(parcelOutRequestDto, httpServletRequest);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "터치한 게시글에 대한 데이터 반환", response = ParcelOut.class)
    })
    @ApiOperation(value = "분양글 조회", notes = "")
    @GetMapping("getParcelOut")
    public ResponseEntity<?> getParcelOut(@RequestParam int id){
        return parcelOutService.getParcelOut(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 게시글 내용들 반환", response = ParcelOut[].class),
            @ApiResponse(code = 304, message = "조회했더니 개수가 0개인경우", response = ParcelOut[].class),
    })
    @ApiOperation(value = "분양글 리스트 조회", notes = "")
    @GetMapping("getParcelOutList")
    public ResponseEntity<?> getParcelOutList(@RequestParam(required = false, defaultValue = "0") int id){
        return parcelOutService.getParcelOutList(id);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 게시글 내용들 업데이트 후 게시글 데이터 다시 반환", response = ParcelOut.class),
            @ApiResponse(code = 409, message = "조회했더니 개수가 0개인경우", response = DefaultResponseDto.class),
    })
    @ApiOperation(value = "분양글 업데이트", notes = "")
    @PatchMapping("updateParcelOut")
    public ResponseEntity<?> updateParcelOut(@RequestBody ParcelOut parcelOut, HttpServletRequest httpServletRequest){
        return parcelOutService.updateParcelOut(parcelOut , httpServletRequest);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "성공적으로 삭제 되었을 경우", response = DefaultResponseDto.class),
            @ApiResponse(code = 409, message = "비인가된 접근일경우", response = DefaultResponseDto.class),
    })
    @ApiOperation(value = "분양글  삭제", notes = "")
    @DeleteMapping("deleteParcelOutList")
    public ResponseEntity<?> deleteParcelOut(@RequestBody IDRequestDto idRequestDto, HttpServletRequest httpServletRequest){
        return parcelOutService.deleteParcelOut(idRequestDto.getId() , httpServletRequest);
    }


}