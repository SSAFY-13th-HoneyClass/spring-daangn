package com.ssafy.daangn.member.dto.response;

import java.time.LocalDateTime;

import com.ssafy.daangn.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {
    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "회원 이름", example = "홍길동")
    private String membername;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileUrl;

    @Schema(description = "가입일시", example = "2025-06-01T00:00:00")
    private LocalDateTime createdAt;

    public static MemberResponseDto from(Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .membername(member.getMembername())
                .email(member.getEmail())
                .profileUrl(member.getProfileUrl())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
