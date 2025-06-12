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
    private User.Role role;

    public User toEntity() {
        return User.builder()
                .id(id)
                .password(password)
                .name(name)
                .nickname(nickname)
                .phone(phone)
                .profileImg(profileImg)
                .manner(manner)
                .role(role != null ? role : User.Role.USER)
                .build();
    }
}