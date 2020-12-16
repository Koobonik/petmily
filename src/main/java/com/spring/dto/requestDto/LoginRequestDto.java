package com.spring.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "01012345678", value = "로그인 휴대폰 번호 & 이메일", required = true)
    private String ID;

    @ApiModelProperty(example = "test_login_password", value = "로그인 비밀번호", required = true)
    private String userPassword;
}
