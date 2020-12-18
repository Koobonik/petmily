package com.spring.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Service
@Getter
public class NewPasswordRequestDto extends ValidateAuthNumberRequestDto {
    @ApiModelProperty(example = "abcdefg1234!@#$", value = "새로운 비밀번호")
    private String newPassword;
}
