package com.spring.dto.responseDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultResponseDto {
    @ApiModelProperty(example = "응답 코드 번호", value = "에러 코드값")
    private int code;
    @ApiModelProperty(example = "안내 메시지", value = "메시지")
    private String message;
}