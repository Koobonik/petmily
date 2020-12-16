package com.spring.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter

@Service
@Getter
public class ValidateAuthNumberRequestDto extends SendAuthNumberRequestDto{
    @ApiModelProperty(example = "147258", value = "인증번호")
    private int authSms;
}
