package com.spring.service;

import com.spring.domain.ShareTheResponsibilityForLife;
import com.spring.domain.ShareTheResponsibilityForLifeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Log4j2
public class ShareTheResponsibilityForLifeService {
    private final ShareTheResponsibilityForLifeRepository shareTheResponsibilityForLifeRepository;

    // 저장 C
    public int save(ShareTheResponsibilityForLife shareTheResponsibilityForLife){

        return shareTheResponsibilityForLifeRepository.save(shareTheResponsibilityForLife).getId();
    }

    @Transactional(readOnly = true)
    @Cacheable("shareTheResponsibilityForLifeFindById")
    public ShareTheResponsibilityForLife findById(int id){
        log.info("특정 게시글 조회 '{}'", id);
        return shareTheResponsibilityForLifeRepository.findById(id);
    }



}