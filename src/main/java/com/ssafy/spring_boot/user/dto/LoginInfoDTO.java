package com.ssafy.spring_boot.user.dto;

import com.ssafy.spring_boot.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginInfoDTO {
    private Integer id;
    private String nickname;
    private String profileUrl;
    private Double temperature;
    private String regionName;

    // Entity -> DTO 변환 메소드
    public static LoginInfoDTO from(User user) {
        return LoginInfoDTO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .profileUrl(user.getProfileUrl())
                .temperature(user.getTemperature())
                .regionName(user.getRegion().getName())
                .build();
    }
}