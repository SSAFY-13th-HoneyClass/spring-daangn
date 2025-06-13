package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.config.TokenProvider;
import org.example.springboot.domain.RefreshToken;
import org.example.springboot.domain.User;
import org.example.springboot.dto.AuthDto;
import org.example.springboot.exception.CustomException;
import org.example.springboot.exception.ErrorCode;
import org.example.springboot.repository.RefreshTokenRepository;
import org.example.springboot.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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

        // 리프레시 토큰 저장 또는 업데이트
        saveOrUpdateRefreshToken(user.getUserId(), refreshToken);

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

        // 저장된 리프레시 토큰 확인
        RefreshToken storedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 만료 시간 확인
        if (storedRefreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedRefreshToken);
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // 사용자 정보 조회
        User user = userRepository.findById(storedRefreshToken.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새로운 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                String.valueOf(user.getUserId()),
                null,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );

        String newAccessToken = tokenProvider.createAccessToken(user.getUserId(), authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(user.getUserId());

        // 새로운 리프레시 토큰으로 업데이트
        Date expirationDate = tokenProvider.getExpirationDateFromToken(newRefreshToken);
        storedRefreshToken.updateRefreshToken(newRefreshToken, 
                expirationDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());

        log.info("토큰 재발급 완료: userId={}", user.getUserId());
        return AuthDto.RefreshTokenResponse.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        log.info("로그아웃 완료: userId={}", userId);
    }

    public String getRefreshTokenForUser(Long userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        return refreshToken.getRefreshToken();
    }

    public Long getUserIdFromRefreshToken(String refreshToken) {
        RefreshToken storedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        return storedRefreshToken.getUserId();
    }

    private void saveOrUpdateRefreshToken(Long userId, String refreshToken) {
        Date expirationDate = tokenProvider.getExpirationDateFromToken(refreshToken);
        LocalDateTime expiresAt = expirationDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();

        RefreshToken existingToken = refreshTokenRepository.findByUserId(userId).orElse(null);

        if (existingToken != null) {
            existingToken.updateRefreshToken(refreshToken, expiresAt);
        } else {
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .userId(userId)
                    .refreshToken(refreshToken)
                    .expiresAt(expiresAt)
                    .build();
            refreshTokenRepository.save(newRefreshToken);
        }
    }
} 