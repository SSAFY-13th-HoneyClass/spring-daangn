package com.ssafy.daangn.dto;

import com.ssafy.daangn.domain.User;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserResponseDto {

    private Long no; // 사용자 PK
    private String name;         // 이름
    private Double temperature;      // 당근 온도
    private String nickname;     // 닉네임
    private Double latitude;     // 위도
    private Double longitude;    // 경도

    public UserResponseDto(User user) {
        this.no = user.getNo();
        this.name = user.getName();
        this.temperature = user.getTemperature();
        this.nickname = user.getNickname();
        this.latitude = user.getLatitude();
    }
}
