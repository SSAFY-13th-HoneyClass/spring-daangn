package com.ssafy.springdaangn.Exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 사용자 정의 예외 처리
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException e, HttpServletRequest request) {
        log.warn("사용자를 찾을 수 없음: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.notFound("사용자", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(
            PostNotFoundException e, HttpServletRequest request) {
        log.warn("게시글을 찾을 수 없음: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.notFound("게시글", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAddressNotFoundException(
            AddressNotFoundException e, HttpServletRequest request) {
        log.warn("주소를 찾을 수 없음: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.notFound("주소", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChatRoomNotFoundException(
            ChatRoomNotFoundException e, HttpServletRequest request) {
        log.warn("채팅방을 찾을 수 없음: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.notFound("채팅방", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ChatMessageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChatMessageNotFoundException(
            ChatMessageNotFoundException e, HttpServletRequest request) {
        log.warn("메시지를 찾을 수 없음: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.notFound("메시지", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLikeNotFoundException(
            LikeNotFoundException e, HttpServletRequest request) {
        log.warn("좋아요를 찾을 수 없음: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.notFound("좋아요", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyLikedException(
            AlreadyLikedException e, HttpServletRequest request) {
        log.warn("이미 좋아요를 누름: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.conflict(e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // 2. JPA 관련 예외 처리
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException e, HttpServletRequest request) {
        log.warn("엔티티를 찾을 수 없음: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.notFound("요청한 리소스", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 3. 유효성 검사 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("유효성 검사 실패: {}", e.getMessage());

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.validationFailed(errorMessage, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException e, HttpServletRequest request) {
        log.warn("바인딩 실패: {}", e.getMessage());

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = ErrorResponse.validationFailed(errorMessage, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException e, HttpServletRequest request) {
        log.warn("유효성 검사 예외: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.validationFailed(e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 4. HTTP 관련 예외 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("HTTP 메시지 읽기 실패: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "INVALID_REQUEST_BODY",
                "요청 본문을 읽을 수 없습니다.",
                request.getRequestURI(),
                400
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("메서드 인자 타입 불일치: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "INVALID_PARAMETER_TYPE",
                "잘못된 파라미터 타입입니다: " + e.getName(),
                request.getRequestURI(),
                400
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("지원하지 않는 HTTP 메서드: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "METHOD_NOT_ALLOWED",
                "지원하지 않는 HTTP 메서드입니다: " + e.getMethod(),
                request.getRequestURI(),
                405
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("핸들러를 찾을 수 없음: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "ENDPOINT_NOT_FOUND",
                "요청한 엔드포인트를 찾을 수 없습니다.",
                request.getRequestURI(),
                404
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 5. 비즈니스 로직 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        log.warn("비즈니스 로직 예외: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                e.getErrorCode(),
                e.getMessage(),
                request.getRequestURI(),
                400
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 6. IllegalStateException 처리 (이미 존재하는 LikeService에서 사용)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException e, HttpServletRequest request) {
        log.warn("잘못된 상태 예외: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.conflict(e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // 7. 모든 예외의 최종 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {
        log.error("예상치 못한 예외 발생", e);

        ErrorResponse errorResponse = ErrorResponse.internalServerError(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}