package com.spring.dto.requestDto;

import com.spring.domain.ParcelOut;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParcelOutRequestDto{

    @ApiModelProperty(value = "제목", example = "너무나 귀여운 푸들입니다.", required = true)
    private String title;

    @ApiModelProperty(value = "내용", example = "적당히 내용을 때려박습니다.", required = true)
    private String description;

    @ApiModelProperty(value = "동물의 이름", example = "백구", required = true)
    private String petName;

    @ApiModelProperty(value = "동물의 생년월일", example = "2020-12-25", required = true)
    private String petBirthDay = "";

    @ApiModelProperty(value = "동물 품종", example = "푸들", required = true)
    private String petKind = "";

    @ApiModelProperty(value = "성별(수컷/암컷)", example = "수컷", required = true)
    private String petGender = "";

    public ParcelOut toEntity(){
        return new ParcelOut().builder()
                .title(title)
                .description(description)
                .petName(petName)
                .petBirthDay(petBirthDay)
                .petKind(petKind)
                .petGender(petGender)
                .build();
    }
}
