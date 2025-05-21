package com.ssafy.daangn.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    private String name;         // 이름
    private String password;     // 비밀번호
    private String email;        // 이메일
    private String phone;        // 전화번호
    private String address;      // 기본 주소
    private Double temperature;      // 당근 온도
    private String addressDetail; // 상세 주소
    private String nickname;     // 닉네임
    private Double latitude;     // 위도
    private Double longitude;    // 경도
}
