package com.spring.domain;

import com.spring.util.DateCreator;
import lombok.*;
import javax.persistence.*;
import java.sql.Timestamp;
import java.text.ParseException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "email_auth_code")
public class EmailAuthCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "user_id", columnDefinition = "INT(11)")
    private int userId;

    @Column(name = "auth_number", nullable = false)
    private int authNumber;

    @Column(name = "created_date", nullable = false, columnDefinition = "datetime")
    private Timestamp createdDate;

    @Column(name = "certified_date", columnDefinition = "datetime")
    private Timestamp certifiedDate;

    @Column(name = "is_can_use", nullable = false, columnDefinition = "TINYINT(4)")
    private boolean isCanUse = true;

    @Column(name = "secret", nullable = false, columnDefinition = "TEXT")
    private String secret;

    @Column(name = "where_to_use", columnDefinition = "VARCHAR(30)")
    private String whereToUse;

    public EmailAuthCode(int authNumber) throws ParseException {
        this.authNumber = authNumber;
        this.createdDate = new DateCreator().getTimestamp();
    }
}