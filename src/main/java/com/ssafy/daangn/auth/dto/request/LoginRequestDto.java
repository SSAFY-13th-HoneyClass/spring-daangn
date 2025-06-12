package com.ssafy.daangn.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginRequestDto {
    @Schema(description = "이메일", example = "test@test.com")
    private String email;

    @Schema(description = "비밀번호", example = "Passw0rd!")
    private String password;
}
