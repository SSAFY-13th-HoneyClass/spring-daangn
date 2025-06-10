package com.example.daangn.security.service;

import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security의 UserDetailsService 인터페이스 구현
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Spring Security가 인증 시 호출하는 메서드
     * AuthenticationProvider가 이 메서드를 통해 사용자 정보를 가져옴
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 id의 사용자를 찾을 수 없음: " + username));

        return new CustomUserPrinciple(user);
    }

    /**
     * User 엔티티를 Spring Security의 UserDetails로 변환하는 내부 클래스
     * UserDetails 인터페이스 구현체
     */
    public static class CustomUserPrinciple implements UserDetails {

        private final User user;

        public CustomUserPrinciple(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getId();
        }
    }
}
