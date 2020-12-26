package com.spring.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IDRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "1", value = "어디에든 쓰이는 단일 int id", required = true)
    private int id;
}
