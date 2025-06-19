package com.ssafy.daangn.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private Long userNo;
    private String nickname;
    private String accessToken;
    private String refreshToken; // 추가
    private String tokenType;
    private Long expiresIn; // 액세스 토큰 만료 시간 (초)
}