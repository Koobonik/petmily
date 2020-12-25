package com.spring.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParcelOutRepository extends JpaRepository<ParcelOut, Integer> {
    // 쿼리 작성해줘야함 boolean 값들 위주로
    @Query(
            nativeQuery = true,
            value = "select * from parcel_out where is_hide = 0 and is_delete = 0 and order by id desc"
    )
    ParcelOut findById(int id);


}
