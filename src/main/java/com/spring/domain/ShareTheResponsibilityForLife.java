package com.spring.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "share_the_responsibility_for_life")
public class ShareTheResponsibilityForLife {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "user_id", columnDefinition = "INT(11)")
    private int userId;

    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "pet_name", columnDefinition = "VARCHAR(50)")
    private String petName;

    @Column(name = "pet_birth_day", columnDefinition = "VARCHAR(10)")
    private String petBirthDay;

    @Column(name = "pet_kind", columnDefinition = "VARCHAR(20)")
    private String petKind;

    @Column(name = "pet_gender", columnDefinition = "VARCHAR(20)")
    private String petGender;

    @Column(name = "created_date", nullable = false, columnDefinition = "datetime")
    private Timestamp createdDate;

    @Column(name = "update_date", columnDefinition = "datetime")
    private Timestamp updateDate;

    @Column(name = "is_video", nullable = false, columnDefinition = "TINYINT(4)")
    private boolean isVideo;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(4)")
    private boolean isActive = true;


    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> images = new ArrayList<>();
}