package com.example.daangn.exception;

import lombok.Getter;

/**
 * API 통신에 대한 '에러 코드'를 Enum 형태로 관리
 * 전역으로 발생하는 에러코드와 업무별 에러코드를 관리
 */
@Getter
public enum ErrorCode {

    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */

    // 잘못된 서버 요청
    BAD_REQUEST_ERROR(400, "G001", "잘못된 요청입니다"),

    // @RequestBody 데이터 미 존재
    REQUEST_BODY_MISSING_ERROR(400, "G002", "필수 요청 데이터가 누락되었습니다"),

    // Request Parameter 로 데이터가 전달되지 않을 경우
    MISSING_REQUEST_PARAMETER_ERROR(400, "G003", "필수 요청 파라미터가 누락되었습니다"),

    // 입력/출력 값이 유효하지 않음
    NOT_VALID_ERROR(400, "G004", "유효하지 않은 입력 값입니다"),

    // 서버로 요청한 리소스가 존재하지 않음
    NOT_FOUND_ERROR(404, "G005", "요청한 리소스를 찾을 수 없습니다"),

    // NULL Point Exception 발생
    NULL_POINT_ERROR(500, "G006", "내부 서버 오류가 발생했습니다"),

    // 서버가 처리 할 방법을 모르는 경우 발생
    INTERNAL_SERVER_ERROR(500, "G999", "내부 서버 오류가 발생했습니다"),

    /**
     * ******************************* Custom Error CodeList ***************************************
     */

    // 사용자 관련 에러
    USER_NOT_FOUND(404, "U001", "존재하지 않는 사용자입니다"),
    USER_ALREADY_EXISTS(409, "U002", "이미 존재하는 사용자 ID입니다"),

    // 게시글 관련 에러
    POST_NOT_FOUND(404, "P001", "존재하지 않는 게시글입니다"),

    // 상품 관련 에러
    PRODUCT_NOT_FOUND(404, "PR001", "존재하지 않는 상품입니다"),

    // 위치 관련 에러
    LOCATION_NOT_FOUND(404, "L001", "존재하지 않는 위치입니다")

    ; // End

    /**
     * ******************************* Error Code Constructor ***************************************
     */

    // 에러 코드의 '코드 상태'을 반환
    private final int status;

    // 에러 코드의 '코드간 구분 값'을 반환
    private final String divisionCode;

    // 에러 코드의 '코드 메시지'을 반환
    private final String message;

    // 생성자 구성
    ErrorCode(final int status, final String divisionCode, final String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}