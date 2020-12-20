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
/**
 *
  * 로그인 dto 클래스가 쓰일 경우에는 암복호화 및 패스워드 매칭이 빈번히 쓰입니다.
  * 그렇기에 클래스 안에 암복호화 및 패스워드매칭 메소드를 선언해 두었습니다.
 */
public class PasswordConfirmRequestDto {

    @ApiModelProperty(example = "password", value = "로그인 비밀번호", required = true)
    private String userLoginPassword;
}