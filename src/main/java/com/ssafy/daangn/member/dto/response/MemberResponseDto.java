package com.ssafy.daangn.member.dto.response;

import java.time.LocalDateTime;

import com.ssafy.daangn.member.entity.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {
    private Long memberId;
    private String membername;
    private String email;
    private String profileUrl;
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
