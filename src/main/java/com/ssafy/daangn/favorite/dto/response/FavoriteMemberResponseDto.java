package com.ssafy.daangn.favorite.dto.response;

import com.ssafy.daangn.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteMemberResponseDto {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "회원 이름", example = "홍길동")
    private String membername;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileUrl;

    public static FavoriteMemberResponseDto from(Member member) {
        return new FavoriteMemberResponseDto(
                member.getMemberId(),
                member.getMembername(),
                member.getProfileUrl()
        );
    }
}
