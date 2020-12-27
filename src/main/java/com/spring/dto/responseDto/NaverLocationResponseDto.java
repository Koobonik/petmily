package com.spring.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;


@Service
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaverLocationResponseDto {
    private Timestamp lastBuildDate;
    private int total;
    private int start;
    private int display;
    private String category;
    private Items[] items;
}