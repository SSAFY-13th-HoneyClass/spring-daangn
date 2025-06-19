package com.ssafy.spring_boot.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 요청 DTO")
public class SignupRequestDTO {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Schema(description = "사용자 이메일", example = "test@test.com", required = true)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 4, max = 20, message = "비밀번호는 4자 이상 20자 이하여야 합니다")
    @Schema(description = "사용자 비밀번호", example = "1234", required = true)
    private String password;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다")
    @Schema(description = "사용자 닉네임", example = "테스터", required = true)
    private String nickname;

    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;

    @NotNull(message = "지역 ID는 필수입니다")
    @Schema(description = "지역 ID", example = "1", required = true)
    private Integer regionId;

    @Schema(description = "프로필 이미지 URL", example = "profile.jpg")
    private String profileUrl;
}