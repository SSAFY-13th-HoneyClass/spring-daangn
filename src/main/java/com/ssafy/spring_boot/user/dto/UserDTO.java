package com.ssafy.spring_boot.user.dto;

import com.ssafy.spring_boot.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserDTO {
    private Integer id;
    private String email;
    private String nickname;
    private String phone;
    private String profileUrl;
    private Double temperature;
    private Integer regionId;
    private String regionName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // Entity -> DTO 변환 메소드
    public static UserDTO from(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .profileUrl(user.getProfileUrl())
                .temperature(user.getTemperature())
                .regionId(user.getRegion().getId())
                .regionName(user.getRegion().getName())
                .createAt(user.getCreateAt())
                .updateAt(user.getUpdateAt())
                .build();
    }
}