package com.spring.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParcelOutRepository extends JpaRepository<ParcelOut, Integer> {
    // 쿼리 작성해줘야함 boolean 값들 위주로
    @Query(
            nativeQuery = true,
            value = "select * from parcel_out where id = :id and is_hide = 0 and is_delete = 0 "
    )
    ParcelOut findById(int id);

    @Query(
            nativeQuery = true,
            value = "select * from parcel_out where id < :id and is_hide = 0 and is_delete = 0 order by id desc limit 10"
    )
    List<ParcelOut> findAllList(int id);

    @Query(
            nativeQuery = true,
            value = "select * from parcel_out where is_hide = 0 and is_delete = 0 order by id desc limit 10"
    )
    List<ParcelOut> findAllListFirst();


}
