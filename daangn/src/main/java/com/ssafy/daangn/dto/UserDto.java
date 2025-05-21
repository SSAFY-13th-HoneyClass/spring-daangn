package com.ssafy.daangn.dto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 사용자 PK

    @Column(nullable = false)
    private String name;         // 이름

    @Column(nullable = false, unique = true)
    private String id;           // 아이디

    @Column(nullable = false)
    private String password;     // 비밀번호

    private String email;        // 이메일

    private String phone;        // 전화번호

    private String address;      // 기본 주소

    private Double temperature;      // 당근 온도

    @Column(name = "address_detail")
    private String addressDetail; // 상세 주소

    private String nickname;     // 닉네임

    private Double latitude;     // 위도

    private Double longitude;    // 경도
}

