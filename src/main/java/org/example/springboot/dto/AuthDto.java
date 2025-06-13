package org.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.springboot.domain.User;

import java.time.LocalDateTime;

public class AuthDto {

    @Schema(description = "회원가입 요청 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignUpRequest {
        @Schema(description = "이메일", example = "user@example.com")
        private String email;

        @Schema(description = "비밀번호", example = "password123")
        private String password;

        @Schema(description = "이름", example = "홍길동")
        private String name;

        @Schema(description = "닉네임", example = "길동이")
        private String nickname;

        @Schema(description = "전화번호", example = "010-1234-5678")
        private String phone;

        @Schema(description = "프로필", example = "안녕하세요!")
        private String profile;

        public static SignUpRequest of(String email, String password, String name, 
                                     String nickname, String phone, String profile) {
            return SignUpRequest.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .nickname(nickname)
                    .phone(phone)
                    .profile(profile)
                    .build();
        }

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .email(this.email)
                    .password(encodedPassword)
                    .name(this.name)
                    .nickname(this.nickname)
                    .phone(this.phone)
                    .profile(this.profile)
                    .profileImgPath("/images/default.jpg")
                    .role("ROLE_USER")
                    .build();
        }
    }

    @Schema(description = "로그인 요청 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        @Schema(description = "이메일", example = "user@example.com")
        private String email;

        @Schema(description = "비밀번호", example = "password123")
        private String password;

        public static LoginRequest of(String email, String password) {
            return LoginRequest.builder()
                    .email(email)
                    .password(password)
                    .build();
        }
    }

    @Schema(description = "로그인 응답 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        @Schema(description = "사용자 ID")
        private Long userId;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "액세스 토큰")
        private String accessToken;

        @Schema(description = "토큰 타입")
        private String tokenType;

        public static LoginResponse of(User user, String accessToken, String refreshToken) {
            return LoginResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .build();
        }
    }

    @Schema(description = "토큰 재발급 요청 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RefreshTokenRequest {
        @Schema(description = "리프레시 토큰")
        private String refreshToken;

        public static RefreshTokenRequest of(String refreshToken) {
            return RefreshTokenRequest.builder()
                    .refreshToken(refreshToken)
                    .build();
        }
    }

    @Schema(description = "토큰 재발급 응답 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RefreshTokenResponse {
        @Schema(description = "새로운 액세스 토큰")
        private String accessToken;

        @Schema(description = "토큰 타입")
        private String tokenType;

        public static RefreshTokenResponse of(String accessToken, String refreshToken) {
            return RefreshTokenResponse.builder()
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .build();
        }
    }

    @Schema(description = "회원가입 응답 DTO")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignUpResponse {
        @Schema(description = "사용자 ID")
        private Long userId;

        @Schema(description = "이메일")
        private String email;

        @Schema(description = "이름")
        private String name;

        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "가입일시")
        private LocalDateTime createdAt;

        public static SignUpResponse from(User user) {
            return SignUpResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }
} 