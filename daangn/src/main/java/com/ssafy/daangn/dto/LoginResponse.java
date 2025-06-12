package com.ssafy.daangn.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private Long userNo;
    private String nickname;
    private String accessToken;
}