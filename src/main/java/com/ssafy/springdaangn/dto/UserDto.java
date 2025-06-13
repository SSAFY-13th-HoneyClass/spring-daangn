package com.ssafy.springdaangn.dto;

import com.ssafy.springdaangn.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private Long userId;
    private String id;
    private String nickname;

    // 정적 팩토리 메서드 - Entity -> DTO
    public static UserDto from(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .id(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    // 정적 팩토리 메서드 - 생성용 DTO
    public static UserDto of(String id, String password, String nickname) {
        return UserDto.builder()
                .id(id)
                .nickname(nickname)
                .build();
    }

    // DTO -> Entity 변환 메서드
    public User toEntity() {
        return User.builder()
                .userId(this.userId)
                .id(this.id)
                .nickname(this.nickname)
                .build();
    }

    // 요청용 DTO
    @Getter
    @Builder
    public static class CreateRequest {
        private String id;
        private String password;
        private String nickname;

        public static CreateRequest of(String id, String password, String nickname) {
            return CreateRequest.builder()
                    .id(id)
                    .password(password)
                    .nickname(nickname)
                    .build();
        }

        public User toEntity() {
            return User.builder()
                    .id(this.id)
                    .password(this.password)
                    .nickname(this.nickname)
                    .build();
        }
    }

    // 수정용 DTO
    @Getter
    @Builder
    public static class UpdateRequest {
        private String password;
        private String nickname;

        public static UpdateRequest of(String password, String nickname) {
            return UpdateRequest.builder()
                    .password(password)
                    .nickname(nickname)
                    .build();
        }
    }
}