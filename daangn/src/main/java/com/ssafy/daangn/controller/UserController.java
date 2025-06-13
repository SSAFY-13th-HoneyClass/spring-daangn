package com.ssafy.daangn.controller;

import com.ssafy.daangn.domain.RefreshToken;
import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.dto.*;
import com.ssafy.daangn.exception.DuplicateNicknameException;
import com.ssafy.daangn.exception.DuplicateUserIdException;
import com.ssafy.daangn.exception.InvalidPasswordException;
import com.ssafy.daangn.exception.UserNotFoundException;
import com.ssafy.daangn.security.TokenProvider;
import com.ssafy.daangn.service.RefreshTokenService;
import com.ssafy.daangn.service.UserService;
import com.ssafy.daangn.util.IpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;


    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "아이디 또는 닉네임 중복"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody User user) {
        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 기존 로직...
        if (userService.existsById(user.getId())) {
            throw new DuplicateUserIdException("이미 존재하는 아이디입니다: " + user.getId());
        }

        if (userService.existsByNickname(user.getNickname())) {
            throw new DuplicateNicknameException("이미 존재하는 닉네임입니다: " + user.getNickname());
        }

        User savedUser = userService.save(user);
        return ResponseEntity.ok(UserResponseDto.registerSuccess(savedUser));
    }

    @Operation(summary = "사용자 로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletRequest request) {

        Optional<User> userOpt = userService.findById(loginRequest.getId());

        if (userOpt.isEmpty()) {
            throw new InvalidPasswordException("아이디가 존재하지 않습니다.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 틀렸습니다.");
        }

        // 로그인 성공 로그
        String clientIp = IpUtils.getClientIpAddress(request);
        log.info("로그인 성공 - 사용자: {}, IP: {}", user.getId(), IpUtils.maskIpAddress(clientIp));

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getNo().toString(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // JWT 액세스 토큰 생성
        String accessToken = tokenProvider.createAccessToken(user.getNo(), authentication);

        // 리프레시 토큰 생성 (IP 주소와 함께)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getNo(), request);

        LoginResponse response = LoginResponse.builder()
                .userNo(user.getNo())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(accessTokenValidityInSeconds)
                .build();

        return ResponseEntity.ok(response);
    }



    @Operation(summary = "토큰 갱신", description = "리프레시 토큰과 IP 주소를 검증하여 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request,
                                                             HttpServletRequest httpRequest) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration) // 만료 확인
                .map(token -> {
                    // 의심스러운 활동 감지
                    refreshTokenService.checkSuspiciousActivity(token, httpRequest);

                    // IP 주소 검증
                    return refreshTokenService.verifyIpAddress(token, httpRequest);
                })
                .map(RefreshToken::getUserNo)
                .map(userNo -> {
                    // 사용자 정보로 새로운 Authentication 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userNo.toString(),
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

                    // 새로운 액세스 토큰 생성
                    String newAccessToken = tokenProvider.createAccessToken(userNo, authentication);

                    // 새로운 리프레시 토큰 생성 (보안상 권장)
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userNo, httpRequest);

                    String clientIp = IpUtils.getClientIpAddress(httpRequest);
                    log.info("토큰 갱신 성공 - 사용자: {}, IP: {}", userNo, IpUtils.maskIpAddress(clientIp));

                    return ResponseEntity.ok(TokenRefreshResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken.getToken())
                            .tokenType("Bearer")
                            .expiresIn(accessTokenValidityInSeconds)
                            .build());
                })
                .orElseThrow(() -> new RuntimeException("리프레시 토큰이 데이터베이스에 없습니다!"));
    }


    @Operation(summary = "로그아웃", description = "로그아웃하고 리프레시 토큰을 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam Long userNo, HttpServletRequest request) {
        refreshTokenService.deleteByUserNo(userNo);

        String clientIp = IpUtils.getClientIpAddress(request);
        log.info("로그아웃 완료 - 사용자: {}, IP: {}", userNo, IpUtils.maskIpAddress(clientIp));

        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }


    @GetMapping("/{no}")
    public ResponseEntity<UserResponseDto> getUser(
            @Parameter(description = "사용자 번호") @PathVariable Long no) {

        Optional<User> userOpt = userService.findByNo(no);
        if (userOpt.isEmpty()) {
            // ✅ 예외를 던지면 GlobalExceptionHandler가 처리
            throw new UserNotFoundException("사용자를 찾을 수 없습니다. ID: " + no);
        }

        // ✅ 정적 팩토리 메서드 사용
        return ResponseEntity.ok(UserResponseDto.from(userOpt.get()));
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{no}")
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "사용자 번호") @PathVariable Long no,
            @RequestBody UserUpdateRequest request) {

        // ✅ try-catch 제거 - service에서 예외 던지면 GlobalExceptionHandler가 처리
        UserResponseDto updated = userService.update(no, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "아이디 중복 체크", description = "아이디 중복 여부를 확인합니다.")
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkIdDuplicate(
            @Parameter(description = "체크할 아이디") @RequestParam String id) {

        boolean exists = userService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "닉네임 중복 체크", description = "닉네임 중복 여부를 확인합니다.")
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(
            @Parameter(description = "체크할 닉네임") @RequestParam String nickname) {

        boolean exists = userService.existsByNickname(nickname);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "비밀번호 찾기", description = "아이디와 이메일로 사용자를 찾습니다.")
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(
            @Parameter(description = "아이디") @RequestParam String id,
            @Parameter(description = "이메일") @RequestParam String email) {

        Optional<User> userOpt = userService.findByIdAndEmail(id, email);
        if (userOpt.isEmpty()) {
            // ✅ 예외를 던지면 GlobalExceptionHandler가 처리
            throw new UserNotFoundException("해당 아이디와 이메일로 사용자를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok("사용자를 찾았습니다. 비밀번호 재설정을 진행해주세요.");
    }
}