package com.example.daangn.controller;

import com.example.daangn.domain.user.dto.LoginRequestDto;
import com.example.daangn.domain.user.dto.UserRequestDto;
import com.example.daangn.domain.user.dto.UserResponseDto;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    /**
     * 새로운 사용자 생성 (회원가입)
     * POST /auth/signup
     */
    @PostMapping("/signup")
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다 (회원가입)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "사용자 생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자 ID",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> createUser(
            @RequestBody @Schema(description = "사용자 생성 요청 데이터") UserRequestDto requestDto) {
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
     * 로그인
     * POST /auth/login
     * */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 ID와 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        // Spring Security를 사용해 등록된 회원인지 여부를 파악 후 인증 토큰 발부
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getId(), loginRequestDto.getPassword());

        // 인증 토큰을 통해 인증된 사용자 정보를 가져옴
        // 내부적으로 DaoAuthenticationProvider -> CustomUserDetailsService 호출됨
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 사용자 정보를 SecurityContextHolder에 저장해둠
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // jwt 토큰 사용 시 여기에 토큰 생성 로직 추가(06/11에 해야지)

        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    /**
     * 로그아웃 API
     *
     * POST /auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // SecurityContext 클리어
            SecurityContextHolder.clearContext();

            //추후 쿠키,토큰,헤더 처리 필요(6/11 하자)

            response.put("success", true);
            response.put("message", "로그아웃되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "로그아웃 처리 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 사용자 삭제
     * DELETE /auth/users/{id}/
     */
    @DeleteMapping("/users/{id}/")
    @Operation(summary = "사용자 삭제", description = "사용자 ID로 특정 사용자를 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 삭제 성공",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<?> deleteUser(
            @PathVariable @Parameter(description = "사용자 ID") Long id) {
        Optional<User> user = userService.findByUID(id);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 사용자를 찾을 수 없습니다.");
        }

        userService.delete(id);
        return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
    }
}
