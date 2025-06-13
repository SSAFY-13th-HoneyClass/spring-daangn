package com.ssafy.daangn.service;

import com.ssafy.daangn.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityMonitoringServiceImpl implements SecurityMonitoringService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    // 사용자의 최근 IP 활동 모니터링
    public void monitorUserIpActivity(Long userNo) {
        LocalDateTime since = LocalDateTime.now().minusDays(7); // 최근 7일
        List<String> recentIps = refreshTokenRepository.findRecentIpAddressesByUserNo(userNo, since);

        if (recentIps.size() > 5) { // 7일간 5개 이상의 다른 IP에서 접근
            log.warn("의심스러운 IP 활동 감지 - 사용자: {}, IP 개수: {}", userNo, recentIps.size());
            // 추가 보안 조치를 여기서 구현 (이메일 알림, 계정 잠금 등)
        }
    }
}