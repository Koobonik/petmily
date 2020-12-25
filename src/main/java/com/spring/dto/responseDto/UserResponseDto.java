package com.spring.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private String userPhoneNumber;
    private String userEmail;
    private String userFirebaseToken;
    private String userNickName;
    private String userImageUrl;
    private List<Integer> pets;
    private List<String> roles;
}