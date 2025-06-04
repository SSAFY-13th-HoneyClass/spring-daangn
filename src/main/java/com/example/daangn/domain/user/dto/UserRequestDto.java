package com.example.daangn.domain.user.dto;

import com.example.daangn.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class UserRequestDto {

    private String id;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String profileImg;
    private BigDecimal manner;
    private String role;


    public static User toEntity(UserRequestDto dto) {
        return User.builder()
                .id(dto.getId())
                .password(dto.getPassword())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .profileImg(dto.getProfileImg())
                .manner(dto.getManner() != null ? dto.getManner() : new BigDecimal("36.5"))
                .role(dto.getRole() != null ? dto.getRole() : "USER")
                .build();
    }
}