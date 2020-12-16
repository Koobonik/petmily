package com.spring.dto.requestDto;

import com.spring.domain.PetmilyUsers;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.stereotype.Service;


@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "01012345678", value = "유저 휴대폰 번호", required = true)
    private String userPhoneNumber;

    @ApiModelProperty(example = "데브쿠", value = "유저의 닉네임", required = true)
    private String userNickName;
    @ApiModelProperty(example = "helloworld123!@#", value = "유저의 패스워드", required = true)
    private String userLoginPassword;

    @ApiModelProperty(example = "147258", value = "문자 인증번호", required = true)
    private int smsAuthNumber;

    public PetmilyUsers toEntity(SignUpRequestDto signUpRequestDto){
        return PetmilyUsers.builder()
                .userPhoneNumber(signUpRequestDto.getUserPhoneNumber())
                .userNickName(signUpRequestDto.getUserNickName())
                .userLoginPassword(signUpRequestDto.getUserLoginPassword())
        .build();
    }
}
