package com.spring.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "location1")
// 관리자 - 매니저 관계 매핑
public class Location1 {

    @Id
    @Column(name = "idx")
    private int id;

    @Column(name = "location1_name", columnDefinition = "INT(11)", nullable = false)
    private String name;

}