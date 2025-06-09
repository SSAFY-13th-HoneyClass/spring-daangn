package com.ssafy.daangn.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;

    // ✅ 정적 팩토리 메서드로 ErrorResponse 생성
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(
                errorCode.getCode(),
                message,
                LocalDateTime.now()
        );
    }

    // ✅ 기본 메시지를 사용하는 정적 팩토리 메서드
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now()
        );
    }
}