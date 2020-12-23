package com.spring.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Location2Repository extends JpaRepository<Location2, Integer> {
    @Query("select p " +
            "from location2 p " +
            "where p.region = :regionNumber")
    List<Location2> findAllByRegion(int regionNumber);
}