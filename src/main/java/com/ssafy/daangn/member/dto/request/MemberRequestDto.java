package com.ssafy.daangn.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRequestDto {

    @Schema(description = "회원 이름", example = "홍길동")
    private String membername;

    @Schema(description = "이메일", example = "test@test.com")
    private String email;

    @Schema(description = "비밀번호", example = "Passw0rd!")
    private String password;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileUrl;
}
