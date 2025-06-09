package com.ssafy.daangn_demo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "product-001", "상품을 찾을 수 없습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user-001", "사용자를 찾을 수 없습니다"),;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
