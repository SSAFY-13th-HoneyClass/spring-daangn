package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKEN_PREFIX = "user_token:";

    /**
     * RefreshToken 저장
     * @param userId 사용자 ID
     * @param refreshToken 리프레시 토큰
     * @param expirationTimeInSeconds 만료 시간 (초)
     */
    public void saveRefreshToken(Long userId, String refreshToken, long expirationTimeInSeconds) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String userKey = USER_TOKEN_PREFIX + userId;
        
        // 기존 사용자의 토큰이 있다면 삭제
        String existingToken = (String) redisTemplate.opsForValue().get(userKey);
        if (existingToken != null) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + existingToken);
        }
        
        // 새로운 토큰 저장
        redisTemplate.opsForValue().set(tokenKey, userId.toString(), expirationTimeInSeconds, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(userKey, refreshToken, expirationTimeInSeconds, TimeUnit.SECONDS);
        
        log.info("RefreshToken saved for user: {} with expiration: {} seconds", userId, expirationTimeInSeconds);
    }

    /**
     * RefreshToken으로 사용자 ID 조회
     * @param refreshToken 리프레시 토큰
     * @return 사용자 ID (토큰이 유효하지 않으면 null)
     */
    public Long getUserIdByRefreshToken(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String userIdStr = (String) redisTemplate.opsForValue().get(tokenKey);
        
        if (userIdStr != null) {
            log.info("Found userId: {} for refreshToken", userIdStr);
            return Long.parseLong(userIdStr);
        }
        
        log.warn("RefreshToken not found or expired: {}", refreshToken);
        return null;
    }

    /**
     * 사용자 ID로 RefreshToken 조회
     * @param userId 사용자 ID
     * @return RefreshToken (없으면 null)
     */
    public String getRefreshTokenByUserId(Long userId) {
        String userKey = USER_TOKEN_PREFIX + userId;
        String refreshToken = (String) redisTemplate.opsForValue().get(userKey);
        
        if (refreshToken != null) {
            log.info("Found refreshToken for userId: {}", userId);
        } else {
            log.warn("RefreshToken not found for userId: {}", userId);
        }
        
        return refreshToken;
    }

    /**
     * RefreshToken 존재 여부 확인
     * @param refreshToken 리프레시 토큰
     * @return 존재 여부
     */
    public boolean existsRefreshToken(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        Boolean exists = redisTemplate.hasKey(tokenKey);
        return exists != null && exists;
    }

    /**
     * 사용자의 RefreshToken 삭제
     * @param userId 사용자 ID
     */
    public void deleteRefreshTokenByUserId(Long userId) {
        String userKey = USER_TOKEN_PREFIX + userId;
        String refreshToken = (String) redisTemplate.opsForValue().get(userKey);
        
        if (refreshToken != null) {
            String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(userKey);
            log.info("RefreshToken deleted for userId: {}", userId);
        } else {
            log.warn("No RefreshToken found to delete for userId: {}", userId);
        }
    }

    /**
     * 특정 RefreshToken 삭제
     * @param refreshToken 리프레시 토큰
     */
    public void deleteRefreshToken(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String userIdStr = (String) redisTemplate.opsForValue().get(tokenKey);
        
        if (userIdStr != null) {
            Long userId = Long.parseLong(userIdStr);
            String userKey = USER_TOKEN_PREFIX + userId;
            
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(userKey);
            log.info("RefreshToken deleted: {} for userId: {}", refreshToken, userId);
        } else {
            log.warn("RefreshToken not found to delete: {}", refreshToken);
        }
    }

    /**
     * RefreshToken의 남은 TTL 조회 (초)
     * @param refreshToken 리프레시 토큰
     * @return TTL (초), 존재하지 않으면 -1
     */
    public long getRefreshTokenTTL(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        Long ttl = redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS);
        return ttl != null ? ttl : -1;
    }
} 