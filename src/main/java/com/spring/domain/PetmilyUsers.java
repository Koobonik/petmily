package com.spring.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class PetmilyUsers implements UserDetails {
    // long 으로 해줘야 이후에 편하다.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false, unique = true)
    private String userPhoneNumber;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String userLoginPassword;

    @Column(columnDefinition = "VARCHAR(50)", nullable = false, unique = true)
    private String userNickName;

    @Column(columnDefinition = "VARCHAR(50)", unique = true)
    private String userEmail;

    @Column(columnDefinition = "VARCHAR(100)")
    private String userFirebaseToken;

    @Column(columnDefinition = "TEXT")
    private String userImageUrl;

    @Column(columnDefinition = "DATETIME")
    private Timestamp signUpDateTime;

    @Column(columnDefinition = "DATETIME")
    private Timestamp lastLoginDateTime;

    @Column(name = "is_out")
    private Boolean isOut;

    @Column(name = "out_datetime", columnDefinition = "datetime")
    private Timestamp outDatetime;


    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<Integer> pets = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.userLoginPassword;
    }

    @Override
    public String getUsername() {
        return this.userPhoneNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return this.isOut;
    }
}
