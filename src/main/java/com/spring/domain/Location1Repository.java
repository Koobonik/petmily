package com.spring.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Location1Repository extends JpaRepository<Location1, Integer> {
    Location1 findByName(String name);
}