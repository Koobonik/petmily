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
@Entity(name = "parcel_out")
public class ParcelOut {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "user_id", columnDefinition = "INT(11)", nullable = false)
    private int userId;

    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255)")
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "pet_name", columnDefinition = "VARCHAR(50)", nullable = false)
    private String petName;

    @Column(name = "pet_birth_day", columnDefinition = "VARCHAR(10)")
    private String petBirthDay;

    @Column(name = "pet_kind", columnDefinition = "VARCHAR(20)", nullable = false)
    private String petKind;

    @Column(name = "pet_gender", columnDefinition = "VARCHAR(20)", nullable = false)
    private String petGender;

    @Column(name = "created_date", nullable = false, columnDefinition = "datetime")
    private Timestamp createdDate;

    @Column(name = "update_date", columnDefinition = "datetime")
    private Timestamp updateDate;

    // 신고 회수
    @Column(name = "report_count", nullable = false)
    private int reportCount = 0;

    // 비디오를 요구하는지 안하는지
    @Column(name = "is_video", nullable = false, columnDefinition = "TINYINT(4)")
    private boolean isVideo;

    // 분양이 완료 되었는지!
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(4)")
    private boolean isActive = true;

    // 게시글 신고회수 누적으로 숨김처리된 것.
    @Column(name = "is_hide", nullable = false, columnDefinition = "TINYINT(4)")
    private boolean isHide = false;

    // 유저가 삭제할 경우
    @Column(name = "is_delete", nullable = false, columnDefinition = "TINYINT(4)")
    private boolean isDelete = false;



    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> images = new ArrayList<>();
}