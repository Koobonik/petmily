package com.spring.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Service
@Getter
public class NewPasswordRequestDto  {
    @ApiModelProperty(example = "abcdefg1234!@#$", value = "새로운 비밀번호")
    private String newPassword;
    @ApiModelProperty(example = "147258", value = "인증번호")
    private int authSms;
    @ApiModelProperty(example = "01011112222", value = "로그인 전화번호", required = true)
    private String callNumber;
}
