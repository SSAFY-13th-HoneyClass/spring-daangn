package com.ssafy.daangn.global.dto;

import lombok.Getter;

@Getter
public class ApiResponseDto<T> {
    private boolean success;
    private T data;
    private String message;

    private ApiResponseDto(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(true, data, null);
    }

    public static <T> ApiResponseDto<T> fail(String message) {
        return new ApiResponseDto<>(false, null, message);
    }
}
