package com.ssafy.spring_boot.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginInfoDTO {
    private Integer id;          // 사용자 ID
    private String nickname;     // 닉네임
    private String profileUrl;   // 프로필 이미지 URL
    private Double temperature;  // 메너온도
    private String regionName;   // 지역 이름
}
