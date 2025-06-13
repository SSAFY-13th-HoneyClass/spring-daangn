package com.ssafy.springdaangn.dto;

import com.ssafy.springdaangn.domain.Like;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeDto {
    private com.ssafy.springdaangn.dto.UserDto user;
    private PostDto.Summary post;

    // 정적 팩토리 메서드 - Entity -> DTO
    public static LikeDto from(Like like) {
        return LikeDto.builder()
                .user(like.getUser() != null ? com.ssafy.springdaangn.dto.UserDto.from(like.getUser()) : null)
                .post(like.getPost() != null ? PostDto.Summary.from(like.getPost()) : null)
                .build();
    }

    // 요청용 DTO
    @Getter
    @Builder
    public static class Request {
        private Long userId;
        private Long postId;

        public static Request of(Long userId, Long postId) {
            return Request.builder()
                    .userId(userId)
                    .postId(postId)
                    .build();
        }
    }

    // 응답용 DTO
    @Getter
    @Builder
    public static class Response {
        private String message;
        private boolean success;

        public static Response success(String message) {
            return Response.builder()
                    .message(message)
                    .success(true)
                    .build();
        }

        public static Response failure(String message) {
            return Response.builder()
                    .message(message)
                    .success(false)
                    .build();
        }
    }
}