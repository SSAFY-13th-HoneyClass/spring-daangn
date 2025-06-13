package com.ssafy.daangn.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long userNo;

    @Column(nullable = false)
    private String ipAddress; // IP 주소 추가

    @Column(nullable = false)
    private String userAgent; // User-Agent 추가 (선택사항)

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastUsedAt; // 마지막 사용 시간 추가

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastUsedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    public void updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }
}
