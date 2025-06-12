package com.example.daangn.domain.user.dto;

import com.example.daangn.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 사용자 조회 응답을 위한 DTO 클래스
 */
@Getter
@Builder
public class UserResponseDto {

    private Long uuid;                  // 사용자 고유 ID
    private String id;                  // 사용자 ID
    private String name;               // 이름
    private String nickname;           // 닉네임
    private String phone;              // 전화번호
    private String profileImg;         // 프로필 이미지 URL
    private BigDecimal manner;         // 매너 온도
    private LocalDateTime lastest;     // 최근 활동 날짜
    private LocalDateTime created;     // 가입 일자
    private User.Role role;               // 사용자 권한

    /**
     * User 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .uuid(user.getUuid())
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .profileImg(user.getProfileImg())
                .manner(user.getManner())
                .lastest(user.getLastest())
                .created(user.getCreated())
                .role(user.getRole())
                .build();
    }
}