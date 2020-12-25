package com.spring.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareTheResponsibilityForLifeRepository extends JpaRepository<ShareTheResponsibilityForLife, Integer> {
    // 쿼리 작성해줘야함 boolean 값들 위주로
    ShareTheResponsibilityForLife findById(int id);


}
