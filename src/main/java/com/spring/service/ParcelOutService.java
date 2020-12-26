package com.spring.service;

import com.spring.domain.PetmilyUsers;
import com.spring.domain.ParcelOut;
import com.spring.domain.ParcelOutRepository;
import com.spring.dto.requestDto.ParcelOutRequestDto;
import com.spring.dto.responseDto.DefaultResponseDto;
import com.spring.util.DateCreator;
import com.spring.util.ValidSomething;
import com.spring.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


@RequiredArgsConstructor
@Service
@Log4j2
public class ParcelOutService {

    private final ParcelOutRepository parcelOutRepository;
    private final JwtTokenProvider jwtTokenProvider;
    // 저장 C

    public ParcelOut save(ParcelOut parcelOut){
        log.info("게시글 저장 '{}'", parcelOut.getId());
        return parcelOutRepository.save(parcelOut);
    }


    // 읽기 R
    @Transactional(readOnly = true)
    @Cacheable("parcelOutFindById")
    public ParcelOut findById(int id){
        log.info("특정 게시글 조회 '{}'", id);
        return parcelOutRepository.findById(id);
    }

    // 읽기 R
    @Transactional(readOnly = true)
    @Cacheable("listParcelOutFindAllList")
    public List<ParcelOut> findAllList(int id){
        if(id == 0){
            parcelOutRepository.findAllListFirst();
        }
        log.info("게시글 10개 조회 '{}'", id);
        return parcelOutRepository.findAllList(id);
    }

    // 업데이트 U
    @CacheEvict(value="listParcelOutFindAllList") // 캐시 지움
    public ParcelOut update(ParcelOut parcelOut, HttpServletRequest httpServletRequest){
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        if(petmilyUsers.getId() == parcelOut.getUserId() && !petmilyUsers.getIsOut()){
            log.info("게시글 업데이트! '{}' : 유저 이름 : '{}'", parcelOut.getId(), petmilyUsers.getUserNickName());
            return parcelOutRepository.save(parcelOut);
        }
        log.info("비인가 업데이트! 접근 유저 아이디 '{}'  : 게시글에 있는 유저 아이디 '{}'", petmilyUsers.getId(), parcelOut.getId());
        return null;
    }

    // 삭제 D
    @CacheEvict(value="listParcelOutFindAllList") // 캐시 지움
    public boolean delete(ParcelOut parcelOut, HttpServletRequest httpServletRequest){
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);
        if(petmilyUsers.getId() == parcelOut.getUserId() && !petmilyUsers.getIsOut()){
            log.info("게시글 삭제! '{}' : 유저 이름 : '{}'", parcelOut.getId(), petmilyUsers.getUserNickName());
            parcelOut.setDelete(true);
            parcelOutRepository.save(parcelOut);
            return true;
        }
        log.info("비인가 삭제! 접근 유저 아이디 '{}'  : 게시글에 있는 유저 아이디 '{}'", petmilyUsers.getId(), parcelOut.getId());
        return false;
    }


    public ResponseEntity<?> postParcelOut(ParcelOutRequestDto parcelOutRequestDto, HttpServletRequest httpServletRequest) throws ParseException {
        if(validateData(parcelOutRequestDto).getStatusCodeValue() != 200){
            return validateData(parcelOutRequestDto);
        }
        PetmilyUsers petmilyUsers = jwtTokenProvider.getPetmilyUsersFromToken(httpServletRequest);

        return new ResponseEntity<>(save(parcelOutRequestDto.toEntity(petmilyUsers.getId())), HttpStatus.OK);
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

    public ResponseEntity<?> getParcelOutList(int id) {
        List<ParcelOut> parcelOutList = findAllList(id);
        if(parcelOutList != null){
            log.info("분양글 List 조회 성공 '{}'", id);
            return new ResponseEntity<>(parcelOutList, HttpStatus.OK);
        }
        log.info("조회되는 게시글 없음 '{}'", id);
        return new ResponseEntity<>(new DefaultResponseDto(304, "마지막 게시글 입니다."), HttpStatus.NOT_MODIFIED);
    }

    @SneakyThrows
    private ResponseEntity<?> validateData(ParcelOutRequestDto parcelOutRequestDto){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(parcelOutRequestDto.getDescription() == null || parcelOutRequestDto.getTitle() == null
                || parcelOutRequestDto.getPetGender() == null || parcelOutRequestDto.getPetKind() == null || parcelOutRequestDto.getPetName() == null){
            return new ResponseEntity<>(new DefaultResponseDto(409, "비어있는 값이 있습니다."), HttpStatus.CONFLICT);
        }
        if(!parcelOutRequestDto.getPetBirthDay().equals("null") && !ValidSomething.isValidDate(parcelOutRequestDto.getPetBirthDay())){
            return new ResponseEntity<>(new DefaultResponseDto(409,"생일 포맷이 맞지 않습니다."), HttpStatus.CONFLICT);
        }
        if(new DateCreator().getTimestamp().before(dateFormat.parse(parcelOutRequestDto.getPetBirthDay()))){
            return new ResponseEntity<>(new DefaultResponseDto(409,"생일이 오늘보다 미래일 수 없습니다"), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> updateParcelOut(ParcelOut parcelOut, HttpServletRequest httpServletRequest) {
        ParcelOut parcelOut1 = update(parcelOut, httpServletRequest);
        if(parcelOut1 != null){
            return new ResponseEntity<>(parcelOut1, HttpStatus.OK);
        }
        return new ResponseEntity<>(new DefaultResponseDto(409, "비인가된 접근입니다."), HttpStatus.CONFLICT);
    }

    public ResponseEntity<?> deleteParcelOut(ParcelOut parcelOut, HttpServletRequest httpServletRequest) {

        if(delete(parcelOut, httpServletRequest)){
            return new ResponseEntity<>(new DefaultResponseDto(200, "성공적으로 삭제되었습니다."), HttpStatus.OK);
        }
        return new ResponseEntity<>(new DefaultResponseDto(409, "비인가된 접근입니다."), HttpStatus.CONFLICT);
    }
}