package com.example.daangn.controller;

import com.example.daangn.domain.user.dto.LoginRequestDto;
import com.example.daangn.domain.user.dto.LoginResponseDto;
import com.example.daangn.domain.user.dto.UserRequestDto;
import com.example.daangn.domain.user.dto.UserResponseDto;
import com.example.daangn.domain.user.service.UserService;
import com.example.daangn.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "입력값 오류 또는 중복된 정보",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> signup(
            @Valid @RequestBody @Schema(description = "회원가입 요청 데이터") UserRequestDto requestDto) {
        try {
            UserResponseDto savedUser = userService.join(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("회원가입 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 ID와 비밀번호로 로그인합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpServletResponse response) {
        try {
            LoginResponseDto loginResponse = authService.login(loginRequest);

            // Refresh Token을 HttpOnly 쿠키로 설정
            setRefreshTokenCookie(response, loginResponse.getRefreshToken());

            return ResponseEntity.ok(loginResponse);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("로그인 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그인 중 오류가 발생했습니다");
        }
    }

    /**
     * Access Token 재발급
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용해 새로운 Access Token을 발급받습니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 Refresh Token",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String refreshToken = getRefreshTokenFromCookie(request);

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh Token이 없습니다");
            }

            LoginResponseDto refreshResponse = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(refreshResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("토큰 재발급 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("토큰 재발급 중 오류가 발생했습니다");
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 처리를 합니다")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    public ResponseEntity<Map<String, Object>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 현재 인증된 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String userId = authentication.getName();
                authService.logout(userId);
            }

            // Refresh Token 쿠키 삭제
            clearRefreshTokenCookie(response);

            // SecurityContext 클리어
            SecurityContextHolder.clearContext();

            result.put("success", true);
            result.put("message", "로그아웃되었습니다");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("로그아웃 오류", e);
            result.put("success", false);
            result.put("message", "로그아웃 처리 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * Refresh Token을 HttpOnly 쿠키로 설정
     */
    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS에서만 전송 (개발환경에서는 false로 설정)
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshTokenCookie);
    }

    /**
     * 쿠키에서 Refresh Token 추출
     */
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> "refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Refresh Token 쿠키 삭제
     */
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}