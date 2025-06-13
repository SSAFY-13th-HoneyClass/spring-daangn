package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.config.TokenProvider;
import org.example.springboot.domain.User;
import org.example.springboot.dto.AuthDto;
import org.example.springboot.exception.CustomException;
import org.example.springboot.exception.ErrorCode;
import org.example.springboot.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RedisRefreshTokenService redisRefreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthDto.SignUpResponse signUp(AuthDto.SignUpRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 저장 (즉시 flush하여 ID 생성)
        User userToSave = request.toEntity(encodedPassword);
        log.debug("저장 전 User: userId={}", userToSave.getUserId());
        
        User savedUser = userRepository.saveAndFlush(userToSave);
        log.debug("저장 후 User: userId={}", savedUser.getUserId());

        log.info("회원가입 완료: userId={}, email={}", savedUser.getUserId(), savedUser.getEmail());
        return AuthDto.SignUpResponse.from(savedUser);
    }

    @Transactional
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        // 사용자 존재 여부 확인
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                String.valueOf(user.getUserId()),
                null,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );

        // 토큰 생성
        String accessToken = tokenProvider.createAccessToken(user.getUserId(), authentication);
        String refreshToken = tokenProvider.createRefreshToken(user.getUserId());

        // 리프레시 토큰을 Redis에 저장 (7일 = 604800초)
        redisRefreshTokenService.saveRefreshToken(user.getUserId(), refreshToken, 604800L);

        log.info("로그인 성공: userId={}, email={}", user.getUserId(), user.getEmail());
        return AuthDto.LoginResponse.of(user, accessToken, refreshToken);
    }

    @Transactional
    public AuthDto.RefreshTokenResponse refreshToken(AuthDto.RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 리프레시 토큰 유효성 검사
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // Redis에서 RefreshToken으로 사용자 ID 조회
        Long userId = redisRefreshTokenService.getUserIdByRefreshToken(refreshToken);
        if (userId == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새로운 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                String.valueOf(user.getUserId()),
                null,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );

        String newAccessToken = tokenProvider.createAccessToken(user.getUserId(), authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(user.getUserId());

        // 새로운 리프레시 토큰을 Redis에 저장 (기존 토큰은 자동으로 교체됨)
        redisRefreshTokenService.saveRefreshToken(user.getUserId(), newRefreshToken, 604800L);

        log.info("토큰 재발급 완료: userId={}", user.getUserId());
        return AuthDto.RefreshTokenResponse.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        redisRefreshTokenService.deleteRefreshTokenByUserId(userId);
        log.info("로그아웃 완료: userId={}", userId);
    }

    public String getRefreshTokenForUser(Long userId) {
        String refreshToken = redisRefreshTokenService.getRefreshTokenByUserId(userId);
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        return refreshToken;
    }

    public Long getUserIdFromRefreshToken(String refreshToken) {
        Long userId = redisRefreshTokenService.getUserIdByRefreshToken(refreshToken);
        if (userId == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        return userId;
    }


} 