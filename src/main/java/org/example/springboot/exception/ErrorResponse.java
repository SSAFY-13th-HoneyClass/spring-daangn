package org.example.springboot.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "에러 응답 DTO")
public class ErrorResponse {
    
    @Schema(description = "HTTP 상태 코드", example = "404")
    private int status;
    
    @Schema(description = "에러 코드", example = "ITEM_NOT_FOUND")
    private String code;
    
    @Schema(description = "에러 메시지", example = "Item not found with id: 999")
    private String message;
    
    @Schema(description = "에러 발생 시간", example = "2024-01-01T10:00:00")
    private LocalDateTime timestamp;
    
    // 정적 팩토리 메서드
    public static ErrorResponse of(int status, String code, String message) {
        return ErrorResponse.builder()
                .status(status)
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
} 