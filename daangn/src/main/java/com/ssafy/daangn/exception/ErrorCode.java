package com.ssafy.daangn.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ✅ 공통 오류
    INVALID_INPUT_VALUE("C001", "잘못된 입력값입니다."),
    ENTITY_NOT_FOUND("C002", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR("C003", "서버 내부 오류가 발생했습니다."),

    // ✅ 사용자 관련 오류
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER_ID("U002", "이미 존재하는 아이디입니다."),
    DUPLICATE_NICKNAME("U003", "이미 존재하는 닉네임입니다."),
    INVALID_PASSWORD("U004", "아이디 또는 비밀번호가 일치하지 않습니다."),

    // ✅ 판매글 관련 오류
    SALE_NOT_FOUND("S001", "판매글을 찾을 수 없습니다."),
    SALE_CREATE_FAILED("S002", "판매글 등록에 실패했습니다."),
    SALE_UPDATE_FAILED("S003", "판매글 수정에 실패했습니다."),
    SALE_DELETE_FAILED("S004", "판매글 삭제에 실패했습니다."),

    // ✅ 채팅 관련 오류
    CHATROOM_NOT_FOUND("CR001", "채팅방을 찾을 수 없습니다."),
    CHATROOM_CREATE_FAILED("CR002", "채팅방 생성에 실패했습니다."),
    CHATROOM_DELETE_FAILED("CR003", "채팅방 삭제에 실패했습니다."),

    // ✅ 파일 관련 오류
    FILE_UPLOAD_FAILED("F001", "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED("F002", "파일 삭제에 실패했습니다."),
    EMPTY_FILE("F003", "파일이 비어있습니다.");

    private final String code;
    private final String message;
}