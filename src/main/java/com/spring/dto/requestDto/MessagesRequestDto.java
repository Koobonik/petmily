package com.spring.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessagesRequestDto {
    private String to;
    private String content;
}
