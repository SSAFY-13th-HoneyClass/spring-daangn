package com.ssafy.spring_boot.user.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserDTO {
    private Integer id;              // 사용자 ID
    private String email;           // 이메일
    private String nickname;        // 닉네임
    private String phone;           // 전화번호
    private String profileUrl;      // 프로필 이미지 URL
    private Double temperature;     // 메너온도
    private Integer regionId;       // 지역 ID
    private String regionName;      // 지역 이름
    private LocalDateTime createAt; // 생성일시
    private LocalDateTime updateAt; // 수정일시
}
