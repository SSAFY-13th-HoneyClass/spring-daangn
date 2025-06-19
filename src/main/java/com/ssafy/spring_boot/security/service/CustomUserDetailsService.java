package com.ssafy.spring_boot.security.service;

import com.ssafy.spring_boot.user.domain.User;
import com.ssafy.spring_boot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("사용자 정보 로드 시도: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        log.debug("사용자 정보 로드 성공: {}", email);

        return new CustomUserDetails(user);
    }

    /**
     * UserDetails 구현체
     */
    public static class CustomUserDetails implements UserDetails {
        private final User user;

        public CustomUserDetails(User user) {
            this.user = user;
        }

        // User 엔티티 반환 (필요시 사용)
        public User getUser() {
            return user;
        }

        // 사용자 ID 반환
        public Long getUserId() {
            return user.getId().longValue();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> authorities = new ArrayList<>();

            // 기본 역할 부여 (추후 역할 관리 테이블 생성 시 수정)
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            return authorities;
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true; // 계정 만료 여부 (필요시 User 엔티티에 필드 추가)
        }

        @Override
        public boolean isAccountNonLocked() {
            return true; // 계정 잠금 여부 (필요시 User 엔티티에 필드 추가)
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true; // 자격증명 만료 여부 (필요시 User 엔티티에 필드 추가)
        }

        @Override
        public boolean isEnabled() {
            return true; // 계정 활성화 여부 (필요시 User 엔티티에 필드 추가)
        }
    }
}