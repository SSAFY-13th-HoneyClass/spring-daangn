package com.example.daangn.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

/**
 * Redis 기반 Refresh Token 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    // Redis 키 접두사
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    /**
     * Refresh Token Redis에 저장
     */
    public void saveRefreshToken(String userId, String refreshToken, long expirationTimeMs) {
        try {
            String key = REFRESH_TOKEN_PREFIX + userId;
            // 토큰 만료기간을 설정하여 향후 자동 폐기되도록함
            Duration expiration = Duration.ofMillis(expirationTimeMs - System.currentTimeMillis());

            // 만료 시간이 양수인 경우에만 저장
            if (expiration.toMillis() > 0) {
                redisTemplate.opsForValue().set(key, refreshToken, expiration);
                log.debug("Refresh Token 저장 완료 - 사용자: {}, 만료시간: {}분",
                        userId, expiration.toMinutes());
            } else {
                log.warn("유효하지 않은 만료 시간 - 사용자: {}, 만료시간: {}ms", userId, expiration.toMillis());
            }
        } catch (Exception e) {
            log.error("Refresh Token 저장 실패 - 사용자: {}", userId, e);
            throw new RuntimeException("Refresh Token 저장 중 오류가 발생했습니다", e);
        }
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(String userId) {
        try {
            String key = REFRESH_TOKEN_PREFIX + userId;
            String refreshToken = redisTemplate.opsForValue().get(key);

            if (refreshToken != null) {
                log.debug("Refresh Token 조회 성공 - 사용자: {}", userId);
            } else {
                log.debug("Refresh Token 없음 또는 만료 - 사용자: {}", userId);
            }

            return refreshToken;
        } catch (Exception e) {
            log.error("Refresh Token 조회 실패 - 사용자: {}", userId, e);
            return null;
        }
    }

    /**
     * Refresh Token 유효성 확인
     */
    public boolean isValidRefreshToken(String userId, String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return false;
            }

            String storedToken = getRefreshToken(userId);
            boolean isValid = refreshToken.equals(storedToken);

            log.debug("Refresh Token 유효성 검사 - 사용자: {}, 유효: {}", userId, isValid);
            return isValid;

        } catch (Exception e) {
            log.error("Refresh Token 유효성 검사 실패 - 사용자: {}", userId, e);
            return false;
        }
    }

    /**
     * Refresh Token 삭제 (로그아웃 시)
     */
    public void deleteRefreshToken(String userId) {
        try {
            String key = REFRESH_TOKEN_PREFIX + userId;
            Boolean deleted = redisTemplate.delete(key);

            if (deleted) {
                log.debug("Refresh Token 삭제 완료 - 사용자: {}", userId);
            } else {
                log.debug("삭제할 Refresh Token 없음 - 사용자: {}", userId);
            }
        } catch (Exception e) {
            log.error("Refresh Token 삭제 실패 - 사용자: {}", userId, e);
        }
    }

    /**
     * 특정 사용자의 모든 Refresh Token 삭제
     */
    public void deleteAllRefreshTokens(String userId) {
        try {
            String pattern = REFRESH_TOKEN_PREFIX + userId + "*";
            Set<String> keys = redisTemplate.keys(pattern);

            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("사용자의 모든 Refresh Token 삭제 완료 - 사용자: {}, 삭제된 토큰 수: {}",
                        userId, keys.size());
            }
        } catch (Exception e) {
            log.error("모든 Refresh Token 삭제 실패 - 사용자: {}", userId, e);
        }
    }
}