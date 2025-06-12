package com.ssafy.daangn.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.auth.dto.request.LoginRequestDto;
import com.ssafy.daangn.auth.dto.response.LoginResponseDto;
import com.ssafy.daangn.auth.service.AuthService;
import com.ssafy.daangn.global.dto.ApiResponseDto;
import com.ssafy.daangn.member.dto.request.MemberRequestDto;
import com.ssafy.daangn.member.dto.response.MemberResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<MemberResponseDto>> signup(@RequestBody MemberRequestDto dto) {
        MemberResponseDto createdMember = authService.signup(dto);
        return ResponseEntity.ok(ApiResponseDto.success(createdMember));
    }

    @Operation(summary = "로그인", description = "로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponseDto.success(authService.login(requestDto)));
    }

    @Operation(summary = "access token 재발급", description = "refresh token으로 access token을 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> refresh(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        System.out.println("refreshToken: " + refreshToken);
        LoginResponseDto newTokens = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(ApiResponseDto.success(newTokens));
    }

    @Operation(summary = "로그아웃", description = "RefreshToken 삭제 등 클라이언트 로그아웃 처리")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<String>> logout() {
        // stateless JWT 환경에서는 클라이언트가 토큰을 삭제하도록 유도
        return ResponseEntity.ok(ApiResponseDto.success("로그아웃되었습니다. 클라이언트 측 토큰 삭제 필요"));
    }

}
