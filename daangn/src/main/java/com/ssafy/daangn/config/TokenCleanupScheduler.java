package com.ssafy.daangn.config;

import com.ssafy.daangn.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    // 매일 자정에 만료된 토큰들을 정리
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens() {
        log.info("만료된 리프레시 토큰 정리 시작");
        refreshTokenService.deleteExpiredTokens();
        log.info("만료된 리프레시 토큰 정리 완료");
    }
}
