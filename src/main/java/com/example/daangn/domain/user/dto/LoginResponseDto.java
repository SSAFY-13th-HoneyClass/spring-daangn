package com.example.daangn.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    @JsonProperty("access_token") // json에 표시될 key값 지정
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;

    private UserResponseDto user;

    private String message;

    // Refresh Token은 JSON 응답에는 포함하지 않고, 쿠키로만 전송
    @JsonIgnore
    private String refreshToken;

    public static LoginResponseDto success(String accessToken, String refreshToken,
                                           long expiresIn, UserResponseDto user) {
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(user)
                .message("로그인 성공")
                .build();
    }
}