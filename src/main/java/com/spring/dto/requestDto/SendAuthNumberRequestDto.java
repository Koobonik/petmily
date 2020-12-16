package com.spring.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter

@Service
@Getter
public class SendAuthNumberRequestDto  {
    @ApiModelProperty(example = "01011112222", value = "로그인 전화번호", required = true)
    private String callNumber;
    @ApiModelProperty(example = "userName", value = "성함", required = true)
    private String userName;
}
