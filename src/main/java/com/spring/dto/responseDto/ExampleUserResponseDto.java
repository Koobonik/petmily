package com.spring.dto.responseDto;

import lombok.*;
import org.springframework.stereotype.Service;


@Service
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleUserResponseDto {
    private String encryptedUserId;
    private String encryptedUserPassword;
}