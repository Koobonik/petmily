package com.spring.dto.responseDto;

import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Service
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicKeyResponseDto {
    private String publicKey;
    private String RSAModulus;
    private String RSAExponent;


}