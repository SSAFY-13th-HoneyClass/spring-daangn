package com.ssafy.springdaangn.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TokenResponseDto {
    private String grantType;
    private String accessToken;
//    private String refreshToken;
    private Long tokenExpiresIn;
//    private Long refreshTokenExpiresIn;
    private Long userId;
    private String nickname;
}
