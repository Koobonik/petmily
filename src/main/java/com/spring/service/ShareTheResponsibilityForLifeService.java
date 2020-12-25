package com.spring.service;

import com.spring.domain.PetmilyUsers;
import com.spring.domain.ShareTheResponsibilityForLife;
import com.spring.domain.ShareTheResponsibilityForLifeRepository;
import com.spring.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


@RequiredArgsConstructor
@Service
@Log4j2
public class ShareTheResponsibilityForLifeService {

    private final ShareTheResponsibilityForLifeRepository shareTheResponsibilityForLifeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    // 저장 C
    public ShareTheResponsibilityForLife save(ShareTheResponsibilityForLife shareTheResponsibilityForLife){
        log.info("게시글 저장 '{}'", shareTheResponsibilityForLife.getId());
        return shareTheResponsibilityForLifeRepository.save(shareTheResponsibilityForLife);
    }


    // 읽기 R
    @Transactional(readOnly = true)
    @Cacheable("shareTheResponsibilityForLifeFindById")
    public ShareTheResponsibilityForLife findById(int id){
        log.info("특정 게시글 조회 '{}'", id);
        return shareTheResponsibilityForLifeRepository.findById(id);
    }

    // 업데이트 U
    public ShareTheResponsibilityForLife update(ShareTheResponsibilityForLife shareTheResponsibilityForLife, HttpServletRequest httpServletRequest){
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        if(petmilyUsers.getId() == shareTheResponsibilityForLife.getUserId() && !petmilyUsers.getIsOut()){
            log.info("게시글 업데이트! '{}' : 유저 이름 : '{}'", shareTheResponsibilityForLife.getId(), petmilyUsers.getUserNickName());
            return shareTheResponsibilityForLifeRepository.save(shareTheResponsibilityForLife);
        }
        log.info("비인가 업데이트! 접근 유저 아이디 '{}'  : 게시글에 있는 유저 아이디 '{}'", petmilyUsers.getId(),shareTheResponsibilityForLife.getId());
        return null;
    }

    // 삭제 D
    public boolean delete(ShareTheResponsibilityForLife shareTheResponsibilityForLife, HttpServletRequest httpServletRequest){
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        if(petmilyUsers.getId() == shareTheResponsibilityForLife.getUserId() && !petmilyUsers.getIsOut()){
            log.info("게시글 삭제! '{}' : 유저 이름 : '{}'", shareTheResponsibilityForLife.getId(), petmilyUsers.getUserNickName());
            shareTheResponsibilityForLife.setIdDelete(true);
            return true;
        }
        log.info("비인가 삭제! 접근 유저 아이디 '{}'  : 게시글에 있는 유저 아이디 '{}'", petmilyUsers.getId(),shareTheResponsibilityForLife.getId());
        return false;
    }


}