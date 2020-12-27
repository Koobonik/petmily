package com.spring.dto.responseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddressesResponseDto {
    private String roadAddress;
    private String jibunAddress;
    private String englishAddress;
    private AddressElementsResponseDto[] addressElements;
    private String x;
    private String y;
    private double distance;
}