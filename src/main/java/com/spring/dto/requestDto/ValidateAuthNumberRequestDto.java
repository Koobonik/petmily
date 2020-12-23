package com.spring.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.stereotype.Service;

@Setter
@Service
@Getter
@NoArgsConstructor
public class ValidateAuthNumberRequestDto extends SendAuthNumberRequestDto{
    @ApiModelProperty(example = "147258", value = "인증번호")
    private int authSms;


    public ValidateAuthNumberRequestDto(int authSms, String callNumber, String userName) {
        this.authSms = authSms;
        super.setCallNumber(callNumber);
        super.setUserName(userName);
    }
}
