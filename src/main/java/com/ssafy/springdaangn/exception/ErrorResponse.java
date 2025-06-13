package com.ssafy.springdaangn.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private String errorCode;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private int status;

    // 정적 팩토리 메서드 - 기본 에러 응답
    public static ErrorResponse of(String errorCode, String message, String path, int status) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 정적 팩토리 메서드 - 간단한 에러 응답
    public static ErrorResponse of(String errorCode, String message) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 정적 팩토리 메서드 - 유효성 검사 실패
    public static ErrorResponse validationFailed(String message, String path) {
        return ErrorResponse.builder()
                .errorCode("VALIDATION_FAILED")
                .message(message)
                .path(path)
                .status(400)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 정적 팩토리 메서드 - 리소스 없음
    public static ErrorResponse notFound(String resource, String path) {
        return ErrorResponse.builder()
                .errorCode("RESOURCE_NOT_FOUND")
                .message(resource + "을(를) 찾을 수 없습니다.")
                .path(path)
                .status(404)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 정적 팩토리 메서드 - 중복 리소스
    public static ErrorResponse conflict(String message, String path) {
        return ErrorResponse.builder()
                .errorCode("RESOURCE_CONFLICT")
                .message(message)
                .path(path)
                .status(409)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 정적 팩토리 메서드 - 서버 내부 오류
    public static ErrorResponse internalServerError(String path) {
        return ErrorResponse.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("서버 내부 오류가 발생했습니다.")
                .path(path)
                .status(500)
                .timestamp(LocalDateTime.now())
                .build();
    }
}