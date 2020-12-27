package com.spring.dto.responseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GeocodingResponseDto {
    private String status;
    private String errorMessage;
    private MetaResponseDto meta;
    private AddressesResponseDto[] addresses;
}