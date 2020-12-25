package com.spring.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareTheResponsibilityForLifeRepository extends JpaRepository<ShareTheResponsibilityForLife, Integer> {
    ShareTheResponsibilityForLife findById(int id);

}
