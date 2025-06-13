package com.ssafy.daangn.repository;


import com.ssafy.daangn.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserNo(Long userNo);
    void deleteByUserNo(Long userNo);
    void deleteByExpiryDateBefore(LocalDateTime now);

    // IP 기반 조회 추가
    List<RefreshToken> findByUserNoAndIpAddress(Long userNo, String ipAddress);

    // 사용자별 활성 토큰 조회
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userNo = :userNo AND rt.expiryDate > :now")
    List<RefreshToken> findActiveTokensByUserNo(@Param("userNo") Long userNo, @Param("now") LocalDateTime now);

    // 의심스러운 IP 활동 감지용
    @Query("SELECT DISTINCT rt.ipAddress FROM RefreshToken rt WHERE rt.userNo = :userNo AND rt.createdAt > :since")
    List<String> findRecentIpAddressesByUserNo(@Param("userNo") Long userNo, @Param("since") LocalDateTime since);
}
