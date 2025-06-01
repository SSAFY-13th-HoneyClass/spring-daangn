package com.ssafy.daangn.member.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 직접 생성 제한
@Builder(access = AccessLevel.PUBLIC)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, length = 50)
    private String membername;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    private String profileUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isDeleted;

    // JPA persist 시점에 기본값 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.isDeleted = false;
    }

    // JPA update 시점에 수정 시간 갱신
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 소프트 삭제 처리
    public void markDeleted() {
        this.isDeleted = true;
    }

    // 정적 팩토리 메서드 - builder 대신 사용 가능
    public static Member of(String membername, String email, String password, String profileUrl) {
        LocalDateTime now = LocalDateTime.now();
        return Member.builder()
                .membername(membername)
                .email(email)
                .password(password)
                .profileUrl(profileUrl)
                .isDeleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
