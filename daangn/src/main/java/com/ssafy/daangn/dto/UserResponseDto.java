package com.ssafy.daangn.dto;

import com.ssafy.daangn.domain.User;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private Long no; // 사용자 PK
    private String name;         // 이름
    private Double temperature;      // 당근 온도
    private String nickname;     // 닉네임
    private Double latitude;     // 위도
    private Double longitude;    // 경도

    // 기존 생성자는 private으로 변경
    private UserResponseDto(User user) {
        this.no = user.getNo();
        this.name = user.getName();
        this.temperature = user.getTemperature();
        this.nickname = user.getNickname();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
    }

    // ✅ 정적 팩토리 메서드
    public static UserResponseDto from(User user) {
        return new UserResponseDto(user);
    }

    // ✅ 회원가입 성공 응답용
    public static UserResponseDto registerSuccess(User user) {
        return new UserResponseDto(user);
    }

    // ✅ 로그인 성공 응답용
    public static UserResponseDto loginSuccess(User user) {
        return new UserResponseDto(user);
    }
}