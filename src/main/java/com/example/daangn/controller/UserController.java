package com.example.daangn.controller;

import com.example.daangn.domain.user.dto.UserRequestDto;
import com.example.daangn.domain.user.dto.UserResponseDto;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 사용자 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;

    /**
     * 특정 사용자 조회
     * GET /users/{id}/
     */
    @GetMapping("/{id}/")
    @Operation(summary = "특정 사용자 조회", description = "사용자 ID로 특정 사용자 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> getUserById(
            @PathVariable @Parameter(description = "사용자 ID") Long id) {
        Optional<User> user = userService.findByUID(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 사용자를 찾을 수 없습니다.");
        }

        UserResponseDto responseDto = UserResponseDto.fromEntity(user.get());
        return ResponseEntity.ok(responseDto);
    }
}