package com.spring.service;

import com.spring.domain.PetmilyRepository;
import com.spring.util.cryptors.AES256Cipher;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Log4j2
public class UsersService implements UserDetailsService {
    private final PetmilyRepository petmilyRepository;
    private final AES256Cipher aes256Cipher;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        log.info("유저 읽기 '{}'", phoneNumber);
        return petmilyRepository.findByUserPhoneNumber(phoneNumber);
    }
}
