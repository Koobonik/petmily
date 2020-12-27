package com.spring.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Items{
    private String title;
    private String link;
    private String category;
    private String description;
    private String address;
    private String roadAddress;
    private int mapx;
    private int mapy;
}