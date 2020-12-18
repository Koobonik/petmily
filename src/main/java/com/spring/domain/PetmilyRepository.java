package com.spring.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetmilyRepository extends JpaRepository<PetmilyUsers, Integer> {
    PetmilyUsers findByUserPhoneNumber(String phoneNumber);

    PetmilyUsers findByUserNickNameAndUserPhoneNumber(String aes_encode, String aes_encode1);

    PetmilyUsers findByUserEmailAndUserLoginPassword(String email, String password);

    PetmilyUsers findByUserEmail(String email);

    PetmilyUsers findByUserNickName(String nickName);

}
