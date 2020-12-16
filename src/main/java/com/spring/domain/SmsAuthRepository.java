package com.spring.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SmsAuthRepository extends JpaRepository<SmsAuth, Integer> {
    @Query(
            nativeQuery = true,
            value = "select * from sms_auth where is_can_use = true AND auth_number = :auth_number"
    )
    SmsAuth findByAuthNumber(int auth_number);

    SmsAuth findBySecret(String secret);

    @Query(
            nativeQuery = true,
            value = "select * from sms_auth where is_can_use = true AND secret = :secret order by id DESC"
    )
    List<SmsAuth> findAllBySecretOrderByIdDesc(String secret);

    @Query(
            nativeQuery = true,
            value = "select * from sms_auth where is_can_use = true order by id DESC"
    )
    List<SmsAuth> findAllByCanUse();
}