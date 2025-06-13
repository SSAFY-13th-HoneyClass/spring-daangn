package com.ssafy.spring_boot.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 응답 DTO")
public class SignupResponseDTO {

    @Schema(description = "생성된 사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "사용자 이메일", example = "test@test.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "테스터")
    private String nickname;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @Schema(description = "프로필 이미지 URL", example = "profile.jpg")
    private String profileUrl;

    @Schema(description = "초기 매너 온도", example = "36.5")
    private Double temperature;

    @Schema(description = "지역 ID", example = "1")
    private Integer regionId;

    @Schema(description = "지역명", example = "서울시 강남구")
    private String regionName;

    @Schema(description = "계정 생성 시간")
    private LocalDateTime createAt;

    @Schema(description = "회원가입 성공 메시지", example = "회원가입이 완료되었습니다.")
    private String message;
}