package org.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.springboot.domain.User;

import java.time.LocalDateTime;

public class UserDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "회원가입 요청 DTO")
    public static class SignUpRequest {
        @Schema(description = "이메일", example = "user@example.com", required = true)
        private String email;
        
        @Schema(description = "비밀번호", example = "password123", required = true)
        private String password;
        
        @Schema(description = "이름", example = "홍길동", required = true)
        private String name;
        
        @Schema(description = "닉네임", example = "길동이", required = true)
        private String nickname;
        
        @Schema(description = "전화번호", example = "010-1234-5678", required = true)
        private String phone;
        
        @Schema(description = "프로필 설명", example = "안녕하세요!", required = true)
        private String profile;
        
        @Schema(description = "프로필 이미지 경로", example = "/images/default.jpg")
        private String profileImgPath;

        // 정적 팩토리 메서드
        public static SignUpRequest of(String email, String password, String name, 
                                     String nickname, String phone, String profile) {
            return SignUpRequest.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .nickname(nickname)
                    .phone(phone)
                    .profile(profile)
                    .profileImgPath("/images/default.jpg")
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "로그인 요청 DTO")
    public static class LoginRequest {
        @Schema(description = "이메일", example = "user@example.com", required = true)
        private String email;
        
        @Schema(description = "비밀번호", example = "password123", required = true)
        private String password;

        // 정적 팩토리 메서드
        public static LoginRequest of(String email, String password) {
            return LoginRequest.builder()
                    .email(email)
                    .password(password)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "사용자 정보 응답 DTO")
    public static class UserResponse {
        @Schema(description = "사용자 ID", example = "1")
        private Long userId;
        
        @Schema(description = "이메일", example = "user@example.com")
        private String email;
        
        @Schema(description = "이름", example = "홍길동")
        private String name;
        
        @Schema(description = "닉네임", example = "길동이")
        private String nickname;
        
        @Schema(description = "전화번호", example = "010-1234-5678")
        private String phone;
        
        @Schema(description = "프로필 설명", example = "안녕하세요!")
        private String profile;
        
        @Schema(description = "프로필 이미지 경로", example = "/images/profile.jpg")
        private String profileImgPath;
        
        @Schema(description = "역할", example = "USER")
        private String role;
        
        @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
        private LocalDateTime createdAt;

        // 정적 팩토리 메서드
        public static UserResponse from(User user) {
            return UserResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .phone(user.getPhone())
                    .profile(user.getProfile())
                    .profileImgPath(user.getProfileImgPath())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "로그인 성공 응답 DTO")
    public static class LoginResponse {
        @Schema(description = "로그인 성공 메시지", example = "로그인 성공")
        private String message;
        
        @Schema(description = "사용자 정보")
        private UserResponse user;

        // 정적 팩토리 메서드
        public static LoginResponse of(String message, User user) {
            return LoginResponse.builder()
                    .message(message)
                    .user(UserResponse.from(user))
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "사용자 목록 조회 응답 DTO")
    public static class UserListResponse {
        @Schema(description = "사용자 ID", example = "1")
        private Long userId;
        
        @Schema(description = "이메일", example = "user@example.com")
        private String email;
        
        @Schema(description = "닉네임", example = "길동이")
        private String nickname;
        
        @Schema(description = "역할", example = "USER")
        private String role;
        
        @Schema(description = "생성일시", example = "2024-01-01T10:00:00")
        private LocalDateTime createdAt;

        // 정적 팩토리 메서드
        public static UserListResponse from(User user) {
            return UserListResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }
}