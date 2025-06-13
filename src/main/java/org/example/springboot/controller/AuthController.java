package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.AuthDto;
import org.example.springboot.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Authentication", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthDto.SignUpResponse> signUp(@RequestBody AuthDto.SignUpRequest request) {
        AuthDto.SignUpResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@RequestBody AuthDto.LoginRequest request, HttpServletResponse response) {
        // AuthService에서 refreshToken을 별도로 받아오는 방식으로 변경 필요
        AuthDto.LoginResponse loginResponse = authService.login(request);
        
        // AuthService에서 refreshToken을 별도로 가져오기 위해 로그인 결과를 다시 받아옴
        String refreshToken = authService.getRefreshTokenForUser(loginResponse.getUserId());
        
        // RefreshToken을 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // 개발환경에서는 false, 프로덕션에서는 true
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);
        
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "토큰 재발급", description = "쿠키의 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다. 새로운 리프레시 토큰은 쿠키로 자동 설정됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰이 쿠키에 없거나 유효하지 않음"),
            @ApiResponse(responseCode = "404", description = "리프레시 토큰을 찾을 수 없음")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthDto.RefreshTokenResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 RefreshToken 추출
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // 기존 refreshToken에서 userId 추출
        Long userId = authService.getUserIdFromRefreshToken(refreshToken);
        
        // RefreshToken으로 새로운 토큰 발급
        AuthDto.RefreshTokenRequest refreshRequest = AuthDto.RefreshTokenRequest.of(refreshToken);
        AuthDto.RefreshTokenResponse refreshResponse = authService.refreshToken(refreshRequest);
        
        // 토큰 재발급 후, 해당 사용자의 새로운 refreshToken을 DB에서 조회하여 쿠키에 설정
        String newRefreshToken = authService.getRefreshTokenForUser(userId);
        
        // 새로운 RefreshToken을 쿠키로 설정
        Cookie newRefreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        newRefreshTokenCookie.setHttpOnly(true);
        newRefreshTokenCookie.setSecure(false); // 개발환경에서는 false, 프로덕션에서는 true
        newRefreshTokenCookie.setPath("/");
        newRefreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(newRefreshTokenCookie);
        
        return ResponseEntity.ok(refreshResponse);
    }

    @Operation(summary = "로그아웃", description = "현재 사용자를 로그아웃하고 리프레시 토큰을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication, HttpServletResponse response) {
        Long userId = Long.parseLong(authentication.getName());
        authService.logout(userId);
        
        // RefreshToken 쿠키 삭제 (MaxAge를 0으로 설정)
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 삭제
        response.addCookie(refreshTokenCookie);
        
        return ResponseEntity.ok().build();
    }
} 