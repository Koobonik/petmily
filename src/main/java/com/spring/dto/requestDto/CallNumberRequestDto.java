package com.spring.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Service
@Getter
public class CallNumberRequestDto {
    @ApiModelProperty(example = "01011112222", value = "로그인 전화번호", required = true)
    private String callNumber;
}
