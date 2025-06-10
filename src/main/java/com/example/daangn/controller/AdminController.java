package com.example.daangn.controller;

import com.example.daangn.domain.user.dto.UserResponseDto;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 관리자만 접근할 수 있는 API
 * /admin으로 접근
 * */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * 모든 사용자 조회(관리자 권한)
     * GET /admin/users/
     */
    @GetMapping("/")
    @Operation(summary = "모든 사용자 조회", description = "등록된 모든 사용자 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.findAll();

        // 엔티티 리스트를 DTO 리스트로 변환
        List<UserResponseDto> responseDtos = users.stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }
}
