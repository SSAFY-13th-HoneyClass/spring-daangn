package com.example.securitystudy.controller;

import com.example.securitystudy.domain.User;
import com.example.securitystudy.dto.LoginRequest;
import com.example.securitystudy.dto.SignupRequest;
import com.example.securitystudy.jwt.TokenProvider;
import com.example.securitystudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "이미 존재하는 사용자입니다.";
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null, List.of(() -> user.getRole())
        );
        String accessToken = tokenProvider.createAccessToken(user.getId(), authentication);
        String refreshToken = tokenProvider.createRefreshToken(user.getId());

        // JWT 토큰 생성
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        return response;
    }

    @PostMapping("/reissue")
    public Map<String, String> reissue(@RequestHeader("Authorization") String bearerToken) {

        // "Bearer " 제거
        String refreshToken = bearerToken.replace("Bearer ", "");

        // RT 유효성 검사
        if (!tokenProvider.validateAccessToken(refreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 유효성 검사
        String userId = tokenProvider.getTokenUserId(refreshToken);
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 새로운 AT 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId(), null, List.of(() -> user.getRole())
        );
        String newAccessToken = tokenProvider.createAccessToken(user.getId(), authentication);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        return response;
    }
}
