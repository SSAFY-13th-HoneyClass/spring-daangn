package com.example.daangn.security.service;

import com.example.daangn.domain.user.dto.LoginRequestDto;
import com.example.daangn.domain.user.dto.LoginResponseDto;
import com.example.daangn.domain.user.dto.UserResponseDto;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.repository.UserRepository;
import com.example.daangn.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    /**
     * 로그인 처리
     */
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        try {
            // Spring Security를 통한 인증
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            // 인증 성공 시 토큰 발급
            CustomUserDetailsService.CustomUserPrinciple principal =
                    (CustomUserDetailsService.CustomUserPrinciple) authentication.getPrincipal();

            String userId = principal.getUsername();
            String role = authentication.getAuthorities().iterator().next().getAuthority();

            // Access Token 생성
            String accessToken = jwtUtil.createAccessToken(userId, role);

            // Refresh Token 생성 및 저장
            String refreshToken = jwtUtil.createRefreshToken(userId);
            long refreshTokenExpiration = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpireTime();
            refreshTokenService.saveRefreshToken(userId, refreshToken, refreshTokenExpiration);

            // 사용자 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

            UserResponseDto userInfo = UserResponseDto.fromEntity(user);

            log.info("로그인 성공: {}", userId);

            return LoginResponseDto.success(accessToken, refreshToken, jwtUtil.getAccessTokenExpireTime(), userInfo);

        } catch (AuthenticationException e) {
            log.warn("로그인 실패: {}", e.getMessage());
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    /**
     * Access Token 재발급
     */
    public LoginResponseDto refreshAccessToken(String refreshToken) {
        // Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        String userId = jwtUtil.getUserId(refreshToken);

        // 저장된 Refresh Token과 비교
        if (!refreshTokenService.isValidRefreshToken(userId, refreshToken)) {
            throw new IllegalArgumentException("만료되거나 유효하지 않은 Refresh Token입니다.");
        }

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 새로운 Access Token 발급
        String newAccessToken = jwtUtil.createAccessToken(userId, "ROLE_" + user.getRole().name());
        UserResponseDto userInfo = UserResponseDto.fromEntity(user);

        log.info("Access Token 재발급: {}", userId);

        return LoginResponseDto.success(newAccessToken, refreshToken, jwtUtil.getAccessTokenExpireTime(), userInfo);
    }

    /**
     * 로그아웃 처리
     */
    public void logout(String userId) {
        // Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(userId);
        log.info("로그아웃 완료: {}", userId);
    }
}