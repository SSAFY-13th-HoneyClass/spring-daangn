package com.ssafy.daangn.controller;

import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.dto.LoginRequest;
import com.ssafy.daangn.dto.LoginResponse;
import com.ssafy.daangn.dto.UserResponseDto;
import com.ssafy.daangn.dto.UserUpdateRequest;
import com.ssafy.daangn.exception.DuplicateNicknameException;
import com.ssafy.daangn.exception.DuplicateUserIdException;
import com.ssafy.daangn.exception.InvalidPasswordException;
import com.ssafy.daangn.exception.UserNotFoundException;
import com.ssafy.daangn.security.TokenProvider;
import com.ssafy.daangn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
//    @PostMapping("/register")
//    public ResponseEntity<UserResponseDto> register(@RequestBody User user) {
//        // ✅ 예외를 던지면 GlobalExceptionHandler가 처리
//        if (userService.existsById(user.getId())) {
//            throw new DuplicateUserIdException("이미 존재하는 아이디입니다: " + user.getId());
//        }
//
//        if (userService.existsByNickname(user.getNickname())) {
//            throw new DuplicateNicknameException("이미 존재하는 닉네임입니다: " + user.getNickname());
//        }
//
//        User savedUser = userService.save(user);
//        // ✅ 정적 팩토리 메서드 사용
//        return ResponseEntity.ok(UserResponseDto.registerSuccess(savedUser));
//    }

    @Operation(summary = "사용자 로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        Optional<User> userOpt = userService.findById(loginRequest.getId());

        if (userOpt.isEmpty()) {
            throw new InvalidPasswordException("아이디가 존재하지 않습니다.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 틀렸습니다.");
        }

//        Optional<User> userOpt = userService.findByIdAndPassword(
//                loginRequest.getId(),
//                loginRequest.getPassword()
//        );
//
//        if (userOpt.isEmpty()) {
//            throw new InvalidPasswordException("아이디 또는 비밀번호가 일치하지 않습니다.");
//        }

//        User user = userOpt.get();

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getNo().toString(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // JWT 토큰 생성
        String accessToken = tokenProvider.createAccessToken(user.getNo(), authentication);

        LoginResponse response = LoginResponse.builder()
                .userNo(user.getNo())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .build();

        return ResponseEntity.ok(response);
    }

//    @PostMapping("/login")
//    public ResponseEntity<UserResponseDto> login(
//            @Parameter(description = "사용자 아이디") @RequestParam String id,
//            @Parameter(description = "비밀번호") @RequestParam String password) {
//
//        Optional<User> userOpt = userService.findByIdAndPassword(id, password);
//        if (userOpt.isEmpty()) {
//            // ✅ 예외를 던지면 GlobalExceptionHandler가 처리
//            throw new InvalidPasswordException("아이디 또는 비밀번호가 일치하지 않습니다.");
//        }
//
//        // ✅ 정적 팩토리 메서드 사용
//        return ResponseEntity.ok(UserResponseDto.loginSuccess(userOpt.get()));
//    }
//
//    @Operation(summary = "사용자 정보 조회", description = "사용자 번호로 정보를 조회합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "조회 성공"),
//            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
//    })



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