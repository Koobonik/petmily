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
@Entity(name = "location2")
// 관리자 - 매니저 관계 매핑
public class Location2 {

    @Id
    @Column(name = "idx")
    private int id;

    @Column(name = "name", columnDefinition = "VARCHAR(45)", nullable = false)
    private String name;

    @Column(name = "region")
    private int region;
}