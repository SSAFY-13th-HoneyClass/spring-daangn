package com.ssafy.daangn.service;

import com.ssafy.daangn.domain.RefreshToken;
import com.ssafy.daangn.exception.TokenRefreshException;
import com.ssafy.daangn.repository.RefreshTokenRepository;
import com.ssafy.daangn.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    @Value("${security.ip-validation.enabled:true}")
    private boolean ipValidationEnabled;

    @Value("${security.ip-validation.strict-mode:false}")
    private boolean strictMode; // 엄격 모드: IP가 완전히 일치해야 함

    @Override
    public RefreshToken createRefreshToken(Long userNo, HttpServletRequest request) {
        // 기존 리프레시 토큰이 있다면 삭제
        refreshTokenRepository.deleteByUserNo(userNo);

        String clientIp = IpUtils.getClientIpAddress(request);
        String userAgent = IpUtils.getUserAgent(request);

        RefreshToken refreshToken = RefreshToken.builder()
                .userNo(userNo)
                .token(UUID.randomUUID().toString())
                .ipAddress(clientIp)
                .userAgent(userAgent)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenValidityInSeconds))
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);

        log.info("리프레시 토큰 생성 - 사용자: {}, IP: {}, User-Agent: {}",
                userNo, IpUtils.maskIpAddress(clientIp), userAgent);

        return saved;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
        }
        return token;
    }

    @Override
    public RefreshToken verifyIpAddress(RefreshToken token, HttpServletRequest request) {
        if (!ipValidationEnabled) {
            log.debug("IP 검증이 비활성화되어 있습니다.");
            return token;
        }

        String currentIp = IpUtils.getClientIpAddress(request);
        String storedIp = token.getIpAddress();

        log.debug("IP 검증 - 저장된 IP: {}, 현재 IP: {}",
                IpUtils.maskIpAddress(storedIp), IpUtils.maskIpAddress(currentIp));

        if (strictMode) {
            // 엄격 모드: IP가 완전히 일치해야 함
            if (!currentIp.equals(storedIp)) {
                log.warn("IP 주소 불일치로 토큰 갱신 거부 - 사용자: {}, 저장된 IP: {}, 요청 IP: {}",
                        token.getUserNo(), IpUtils.maskIpAddress(storedIp), IpUtils.maskIpAddress(currentIp));

                // 보안을 위해 해당 토큰을 삭제
                refreshTokenRepository.delete(token);
                throw new TokenRefreshException("보안상의 이유로 토큰 갱신이 거부되었습니다. 다시 로그인해주세요.");
            }
        } else {
            // 유연한 모드: 같은 네트워크 대역 허용 (예: 192.168.1.*)
            if (!isSameNetwork(currentIp, storedIp)) {
                log.warn("네트워크 대역 불일치로 토큰 갱신 거부 - 사용자: {}, 저장된 IP: {}, 요청 IP: {}",
                        token.getUserNo(), IpUtils.maskIpAddress(storedIp), IpUtils.maskIpAddress(currentIp));

                refreshTokenRepository.delete(token);
                throw new TokenRefreshException("다른 네트워크에서의 접근이 감지되었습니다. 다시 로그인해주세요.");
            }
        }

        // 검증 성공 시 마지막 사용 시간 업데이트
        token.updateLastUsed();
        return refreshTokenRepository.save(token);
    }

    private boolean isSameNetwork(String ip1, String ip2) {
        if (ip1.equals(ip2)) {
            return true;
        }

        // 간단한 네트워크 대역 검사 (처음 3개 옥텟이 같으면 허용)
        String[] parts1 = ip1.split("\\.");
        String[] parts2 = ip2.split("\\.");

        if (parts1.length != 4 || parts2.length != 4) {
            return false;
        }

        // 192.168.1.* 같은 사설 IP 대역에서는 유연하게 처리
        return parts1[0].equals(parts2[0]) &&
                parts1[1].equals(parts2[1]) &&
                parts1[2].equals(parts2[2]);
    }

    @Override
    public void deleteByUserNo(Long userNo) {
        refreshTokenRepository.deleteByUserNo(userNo);
        log.info("사용자 {}의 모든 리프레시 토큰이 삭제되었습니다.", userNo);
    }

    @Override
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }

    @Override
    // 의심스러운 활동 감지
    public void checkSuspiciousActivity(RefreshToken token, HttpServletRequest request) {
        String currentIp = IpUtils.getClientIpAddress(request);
        String currentUserAgent = IpUtils.getUserAgent(request);

        // IP 주소 변경 감지
        if (!token.getIpAddress().equals(currentIp)) {
            log.warn("IP 주소 변경 감지 - 사용자: {}, 기존 IP: {}, 새 IP: {}",
                    token.getUserNo(),
                    IpUtils.maskIpAddress(token.getIpAddress()),
                    IpUtils.maskIpAddress(currentIp));
        }

        // User-Agent 변경 감지
        if (!token.getUserAgent().equals(currentUserAgent)) {
            log.warn("User-Agent 변경 감지 - 사용자: {}", token.getUserNo());
        }
    }
}
