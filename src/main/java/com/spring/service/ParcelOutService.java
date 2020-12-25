package com.spring.service;

import com.spring.domain.PetmilyUsers;
import com.spring.domain.ParcelOut;
import com.spring.domain.ParcelOutRepository;
import com.spring.dto.requestDto.ParcelOutRequestDto;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


@RequiredArgsConstructor
@Service
@Log4j2
public class ParcelOutService {

    private final ParcelOutRepository shareTheResponsibilityForLifeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    // 저장 C
    public ParcelOut save(ParcelOut parcelOut){
        log.info("게시글 저장 '{}'", parcelOut.getId());
        return shareTheResponsibilityForLifeRepository.save(parcelOut);
    }


    // 읽기 R
    @Transactional(readOnly = true)
    @Cacheable("shareTheResponsibilityForLifeFindById")
    public ParcelOut findById(int id){
        log.info("특정 게시글 조회 '{}'", id);
        return shareTheResponsibilityForLifeRepository.findById(id);
    }

    // 업데이트 U
    public ParcelOut update(ParcelOut parcelOut, HttpServletRequest httpServletRequest){
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        if(petmilyUsers.getId() == parcelOut.getUserId() && !petmilyUsers.getIsOut()){
            log.info("게시글 업데이트! '{}' : 유저 이름 : '{}'", parcelOut.getId(), petmilyUsers.getUserNickName());
            return shareTheResponsibilityForLifeRepository.save(parcelOut);
        }
        log.info("비인가 업데이트! 접근 유저 아이디 '{}'  : 게시글에 있는 유저 아이디 '{}'", petmilyUsers.getId(), parcelOut.getId());
        return null;
    }

    // 삭제 D
    public boolean delete(ParcelOut parcelOut, HttpServletRequest httpServletRequest){
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        if(petmilyUsers.getId() == parcelOut.getUserId() && !petmilyUsers.getIsOut()){
            log.info("게시글 삭제! '{}' : 유저 이름 : '{}'", parcelOut.getId(), petmilyUsers.getUserNickName());
            parcelOut.setIdDelete(true);
            return true;
        }
        log.info("비인가 삭제! 접근 유저 아이디 '{}'  : 게시글에 있는 유저 아이디 '{}'", petmilyUsers.getId(), parcelOut.getId());
        return false;
    }


    public ResponseEntity<?> writeParcelOut(ParcelOutRequestDto parcelOutRequestDto) {
        if(validateData(parcelOutRequestDto).getStatusCodeValue() != 200){
            return validateData(parcelOutRequestDto);
        }
        return new ResponseEntity<>(save(parcelOutRequestDto.toEntity()), HttpStatus.OK);
    }

    public ResponseEntity<?> getParcelOut(int id) {
        ParcelOut parcelOut = findById(id);
        if(parcelOut != null){
            log.info("분양글 조회 성공 '{}'", id);
            return new ResponseEntity<>(parcelOut, HttpStatus.OK);
        }
        log.info("조회되는 게시글 없음 '{}'", id);
        return new ResponseEntity<>(new DefaultResponseDto(409, "조회되는 게시글이 없습니다."), HttpStatus.CONFLICT);
    }

    private ResponseEntity<?> validateData(ParcelOutRequestDto parcelOutRequestDto){
        if(parcelOutRequestDto.getDescription() == null || parcelOutRequestDto.getTitle() == null || parcelOutRequestDto.getPetBirthDay() == null
                || parcelOutRequestDto.getPetGender() == null || parcelOutRequestDto.getPetKind() == null || parcelOutRequestDto.getPetName() == null){
            return new ResponseEntity<>(new DefaultResponseDto(409, "비어있는 값이 있습니다."), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}