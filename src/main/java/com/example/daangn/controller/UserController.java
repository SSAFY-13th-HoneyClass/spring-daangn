package com.example.daangn.controller;

import com.example.daangn.domain.user.dto.UserRequestDto;
import com.example.daangn.domain.user.dto.UserResponseDto;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 새로운 사용자 생성 (회원가입)
     * POST /api/users/
     */
    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto requestDto) {
        // DTO를 엔티티로 변환
        User user = UserRequestDto.toEntity(requestDto);
        user.setCreated(LocalDateTime.now());
        user.setLastest(LocalDateTime.now());

        // 사용자 생성 (중복 체크 포함)
        User savedUser = userService.join(user);

        // 중복된 ID인 경우 처리
        if (savedUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 존재하는 사용자 ID입니다.");
        }

        // 생성된 사용자 정보를 DTO로 변환하여 반환
        UserResponseDto responseDto = UserResponseDto.fromEntity(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 모든 사용자 조회
     * GET /api/users/
     */
    @GetMapping("/")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.findAll();

        // 엔티티 리스트를 DTO 리스트로 변환
        List<UserResponseDto> responseDtos = users.stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 특정 사용자 조회
     * GET /api/users/{id}/
     */
    @GetMapping("/{id}/")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findByUID(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 사용자를 찾을 수 없습니다.");
        }

        UserResponseDto responseDto = UserResponseDto.fromEntity(user.get());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 특정 사용자 삭제
     * DELETE /api/users/{id}/
     */
    @DeleteMapping("/{id}/")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findByUID(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 사용자를 찾을 수 없습니다.");
        }

        userService.delete(id);
        return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
    }
}