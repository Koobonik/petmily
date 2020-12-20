package com.spring.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailAuthCodeRepository extends JpaRepository<EmailAuthCode, Integer> {
    @Query(
            nativeQuery = true,
            value = "select * from email_auth_code where is_can_use = true AND auth_number = :auth_number"
    )
    EmailAuthCode findByAuthNumber(int auth_number);

    EmailAuthCode findBySecret(String secret);

    @Query(
            nativeQuery = true,
            value = "select * from email_auth_code where is_can_use = true AND secret = :secret order by id DESC"
    )
    List<EmailAuthCode> findAllBySecretOrderByIdDesc(String secret);

    @Query(
            nativeQuery = true,
            value = "select * from email_auth_code where is_can_use = true order by id DESC"
    )
    List<EmailAuthCode> findAllByCanUse();
}