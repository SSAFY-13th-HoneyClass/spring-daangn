package com.ssafy.daangn.security;


import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userNo) throws UsernameNotFoundException {
        return userRepository.findByNo(Long.parseLong(userNo))
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userNo));
    }

    // User 엔티티를 UserDetails로 변환
    private UserDetails createUserDetails(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new org.springframework.security.core.userdetails.User(
                user.getNo().toString(),
                user.getPassword(),
                authorities
        );
    }
}
