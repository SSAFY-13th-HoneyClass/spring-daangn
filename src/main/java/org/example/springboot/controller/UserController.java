package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.UserDto;
import org.example.springboot.exception.ErrorResponse;
import org.example.springboot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "당근마켓 사용자 관리 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (중복 이메일/닉네임)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<UserDto.UserResponse> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDto.SignUpRequest.class))
            )
            @RequestBody UserDto.SignUpRequest request) {
        log.info("Sign up request for email: {}", request.getEmail());
        
        UserDto.UserResponse response = userService.signUp(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "로그인 실패 (잘못된 이메일/비밀번호)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<UserDto.LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserDto.LoginRequest.class))
            )
            @RequestBody UserDto.LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        
        UserDto.LoginResponse response = userService.login(request);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 사용자 조회", description = "등록된 모든 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.UserListResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<UserDto.UserListResponse>> getAllUsers() {
        log.info("Retrieving all users");
        
        List<UserDto.UserListResponse> responses = userService.getAllUsers();
        
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "특정 사용자 조회", description = "ID로 특정 사용자의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.UserResponse> getUserById(
            @Parameter(description = "조회할 사용자의 ID", required = true, example = "1")
            @PathVariable("id") Long userId) {
        log.info("Retrieving user with id: {}", userId);
        
        UserDto.UserResponse response = userService.getUserProfile(userId);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이메일로 사용자 조회", description = "이메일로 특정 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto.UserResponse> getUserByEmail(
            @Parameter(description = "조회할 사용자의 이메일", required = true, example = "user@example.com")
            @PathVariable("email") String email) {
        log.info("Retrieving user with email: {}", email);
        
        UserDto.UserResponse response = userService.getUserByEmail(email);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 삭제", description = "ID로 특정 사용자를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "삭제할 사용자의 ID", required = true, example = "1")
            @PathVariable("id") Long userId) {
        log.info("Deleting user with id: {}", userId);
        
        userService.deleteUser(userId);
        
        return ResponseEntity.noContent().build();
    }
} 