package com.ssafy.spring_boot.user.controller;

import com.ssafy.spring_boot.chat.dto.ChatRoomDTO;
import com.ssafy.spring_boot.product.dto.ProductDTO;
import com.ssafy.spring_boot.security.provider.TokenProvider;
import com.ssafy.spring_boot.security.util.SecurityUtil;
import com.ssafy.spring_boot.user.dto.*;
import com.ssafy.spring_boot.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 관리", description = "회원가입, 로그인, 사용자 정보 관련 API\n\n" +
        "🚀 테스트 순서:\n" +
        "1. POST /api/users/signup (회원가입) → test@test.com, 1234, 테스터, regionId: 1\n" +
        "2. POST /api/users/login (로그인) → test@test.com, 1234\n" +
        "3. Authorization 헤더에 Bearer {accessToken} 추가 후 /me API 테스트\n" +
        "4. 🆕 POST /api/users/refresh (토큰 갱신) → refreshToken으로 새 토큰 발급!\n\n" +
        "📖 API 구분:\n" +
        "- GET /api/users/{id}: 공개 - 누구나 다른 사용자 정보 조회 가능\n" +
        "- GET /api/users/me: 개인 - JWT 토큰으로 본인 정보만 조회 가능\n" +
        "- POST /api/users/refresh: 🔥 TokenProvider 고급 기능 - 토큰 갱신!")
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;  // TokenProvider 추가!

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 이메일 중복"),
            @ApiResponse(responseCode = "404", description = "지역을 찾을 수 없음")
    })
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO signupRequest) {
        log.debug("회원가입 요청: {}", signupRequest.getEmail());

        SignupResponseDTO response = userService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "JWT 로그인", description = "JWT 토큰 기반 로그인을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        log.debug("JWT 로그인 요청: {}", loginRequest.getEmail());

        LoginResponseDTO response = userService.jwtLogin(loginRequest);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "🚀 토큰 갱신 (TokenProvider 기능)",
            description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다. TokenProvider만의 고급 기능입니다!",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 Refresh Token"),
            @ApiResponse(responseCode = "401", description = "만료된 Refresh Token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponseDTO> refreshToken(
            @Valid @RequestBody TokenRefreshRequestDTO request) {

        String refreshToken = request.getRefreshToken();
        log.debug("토큰 갱신 요청");

        // 🎯 TokenProvider만 가능한 기능: Refresh Token 전용 검증
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        // 🔍 TokenProvider의 고급 기능: 토큰에서 상세 정보 추출
        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        String email = tokenProvider.getEmailFromToken(refreshToken);
        String authorities = tokenProvider.getAuthoritiesFromToken(refreshToken);

        // 📊 TokenProvider의 분석 기능: 토큰 상태 확인
        long remainingTime = tokenProvider.getRemainingTime(refreshToken);
        String tokenInfo = tokenProvider.getTokenInfo(refreshToken);

        log.info("토큰 갱신 - {}", tokenInfo);
        log.info("Refresh Token 남은 시간: {}분", remainingTime / (1000 * 60));

        // 🔄 새로운 토큰 쌍 생성 (TokenProvider의 핵심 기능)
        String newAccessToken = tokenProvider.createAccessToken(userId, email, authorities);
        String newRefreshToken = tokenProvider.createRefreshToken(userId, email, authorities);

        // ⚠️ Refresh Token 만료 임박 경고 (TokenProvider만 가능)
        if (remainingTime < 24 * 60 * 60 * 1000) { // 24시간 미만
            log.warn("Refresh Token 만료 임박 - 사용자: {}, 남은시간: {}시간",
                    email, remainingTime / (1000 * 60 * 60));
        }

        return ResponseEntity.ok(TokenRefreshResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenValidityInMilliseconds() / 1000)
                .build());
    }

    // ========================= 공개 API (누구나 접근 가능) =========================

    @Operation(summary = "사용자 상세 정보 조회 (공개)", description = "사용자 ID로 상세 정보를 조회합니다. 누구나 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserDetail(
            @Parameter(description = "조회할 사용자의 ID", required = true)
            @PathVariable Long id) {
        log.debug("사용자 상세 정보 조회 (공개): 사용자 ID {}", id);

        UserDTO user = userService.getUserDetail(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "사용자가 등록한 상품 목록 조회 (공개)", description = "특정 사용자가 등록한 모든 상품을 조회합니다. 누구나 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductDTO>> getUserProducts(
            @Parameter(description = "조회할 사용자의 ID", required = true)
            @PathVariable Long id) {
        log.debug("사용자 상품 목록 조회 (공개): 사용자 ID {}", id);

        List<ProductDTO> products = userService.getUserProducts(id);
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "사용자 채팅방 목록 조회 (공개)", description = "특정 사용자가 참여 중인 채팅방 목록을 조회합니다. 누구나 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}/chatrooms")
    public ResponseEntity<List<ChatRoomDTO>> getUserChatRooms(
            @Parameter(description = "조회할 사용자의 ID", required = true)
            @PathVariable Long id) {
        log.debug("사용자 채팅방 목록 조회 (공개): 사용자 ID {}", id);

        List<ChatRoomDTO> chatRooms = userService.getUserChatRooms(id);
        return ResponseEntity.ok(chatRooms);
    }

    @Operation(summary = "사용자 관심 상품 ID 목록 조회 (공개)", description = "특정 사용자가 관심 표시한 상품 ID 목록을 조회합니다. 누구나 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}/favorites")
    public ResponseEntity<List<Long>> getFavoriteProductIds(
            @Parameter(description = "조회할 사용자의 ID", required = true)
            @PathVariable Long id) {
        log.debug("사용자 관심 상품 목록 조회 (공개): 사용자 ID {}", id);

        List<Long> favoriteIds = userService.getFavoriteProductIds(id);
        return ResponseEntity.ok(favoriteIds);
    }

    // ========================= 개인 API (JWT 토큰 필요) =========================

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다. JWT 토큰에서 사용자 ID를 추출하여 본인 정보만 조회합니다.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo() {
        Long currentUserId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        log.debug("내 정보 조회: 사용자 ID {}", currentUserId);

        UserDTO user = userService.getUserDetail(currentUserId);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "내가 등록한 상품 목록 조회",
            description = "현재 로그인한 사용자가 등록한 모든 상품을 조회합니다. JWT 토큰에서 사용자 ID를 추출합니다.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me/products")
    public ResponseEntity<List<ProductDTO>> getMyProducts() {
        Long currentUserId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        log.debug("내 상품 목록 조회: 사용자 ID {}", currentUserId);

        List<ProductDTO> products = userService.getUserProducts(currentUserId);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "내 채팅방 목록 조회",
            description = "현재 로그인한 사용자가 참여 중인 채팅방 목록을 조회합니다. JWT 토큰에서 사용자 ID를 추출합니다.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me/chatrooms")
    public ResponseEntity<List<ChatRoomDTO>> getMyChatRooms() {
        Long currentUserId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        log.debug("내 채팅방 목록 조회: 사용자 ID {}", currentUserId);

        List<ChatRoomDTO> chatRooms = userService.getUserChatRooms(currentUserId);
        return ResponseEntity.ok(chatRooms);
    }

    @Operation(
            summary = "내 관심 상품 ID 목록 조회",
            description = "현재 로그인한 사용자가 관심 표시한 상품 ID 목록을 조회합니다. JWT 토큰에서 사용자 ID를 추출합니다.",
            security = @SecurityRequirement(name = "Bearer")
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me/favorites")
    public ResponseEntity<List<Long>> getMyFavoriteProductIds() {
        Long currentUserId = SecurityUtil.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("인증되지 않은 사용자입니다."));

        log.debug("내 관심 상품 목록 조회: 사용자 ID {}", currentUserId);

        List<Long> favoriteIds = userService.getFavoriteProductIds(currentUserId);
        return ResponseEntity.ok(favoriteIds);
    }

    // ========================= 호환성 API =========================

    @Operation(summary = "기존 방식 로그인 (호환성)", description = "기존 방식의 로그인입니다. (JWT 토큰 없음)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/legacy-login")
    public ResponseEntity<LoginInfoDTO> legacyLogin(
            @Parameter(description = "이메일", required = true) @RequestParam String email,
            @Parameter(description = "비밀번호", required = true) @RequestParam String password) {
        log.debug("기존 방식 로그인 요청: {}", email);

        LoginInfoDTO response = userService.login(email, password);

        return ResponseEntity.ok(response);
    }
}