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
@Builder(access = AccessLevel.PRIVATE)            // builder도 외부에서 못 쓰게
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void markDeleted() {
        this.isDeleted = true;
    }

    // 정적 팩토리 메서드
    public static Member of(String membername, String email, String password, String profileUrl) {
        Member member = new Member();
        member.membername = membername;
        member.email = email;
        member.password = password;
        member.profileUrl = profileUrl;
        member.isDeleted = false;
        member.createdAt = LocalDateTime.now();
        member.updatedAt = member.createdAt;
        return member;
    }
}
