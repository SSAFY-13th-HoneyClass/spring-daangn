// UserController.java
package com.ssafy.daangn.controller;

import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.dto.UserResponseDto;
import com.ssafy.daangn.dto.UserUpdateRequest;
import com.ssafy.daangn.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "아이디 또는 닉네임 중복")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // 아이디 중복 체크
            if (userService.existsById(user.getId())) {
                return ResponseEntity.status(409).body("이미 존재하는 아이디입니다.");
            }

            // 닉네임 중복 체크
            if (userService.existsByNickname(user.getNickname())) {
                return ResponseEntity.status(409).body("이미 존재하는 닉네임입니다.");
            }

            User savedUser = userService.save(user);
            return ResponseEntity.ok(new UserResponseDto(savedUser));
        } catch (Exception e) {
            log.error("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("회원가입에 실패했습니다.");
        }
    }

    @Operation(summary = "사용자 로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "사용자 아이디") @RequestParam String id,
            @Parameter(description = "비밀번호") @RequestParam String password) {

        Optional<User> userOpt = userService.findByIdAndPassword(id, password);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(new UserResponseDto(userOpt.get()));
        } else {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자 번호로 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{no}")
    public ResponseEntity<?> getUser(
            @Parameter(description = "사용자 번호") @PathVariable Long no) {

        Optional<User> userOpt = userService.findByNo(no);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(new UserResponseDto(userOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/{no}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "사용자 번호") @PathVariable Long no,
            @RequestBody UserUpdateRequest request) {

        try {
            UserResponseDto updated = userService.update(no, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("사용자 정보 수정 실패: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<?> findPassword(
            @Parameter(description = "아이디") @RequestParam String id,
            @Parameter(description = "이메일") @RequestParam String email) {

        Optional<User> userOpt = userService.findByIdAndEmail(id, email);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok("사용자를 찾았습니다. 비밀번호 재설정을 진행해주세요.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}