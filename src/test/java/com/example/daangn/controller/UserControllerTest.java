package com.example.daangn.controller;

import com.example.daangn.domain.user.dto.UserRequestDto;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 통합 테스트
 * 사용자 관련 API의 전체 동작을 검증합니다.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    /**
     * 각 테스트 실행 전 MockMvc 설정
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * 사용자 생성 API 테스트 (성공)
     */
    @Test
    @DisplayName("사용자 생성 성공 테스트")
    void createUser_Success() throws Exception {
        // given
        UserRequestDto requestDto = UserRequestDto.builder()
                .id("testuser")
                .password("password123")
                .name("테스트 사용자")
                .nickname("테스터")
                .phone("01012345678")
                .build();

        // when & then
        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("testuser"))
                .andExpect(jsonPath("$.name").value("테스트 사용자"))
                .andExpect(jsonPath("$.nickname").value("테스터"));
    }

    /**
     * 사용자 생성 API 테스트 (중복 ID)
     */
    @Test
    @DisplayName("중복 ID로 사용자 생성 실패 테스트")
    void createUser_DuplicateId() throws Exception {
        // given
        User existingUser = User.builder()
                .id("duplicate")
                .password("password")
                .name("기존 사용자")
                .nickname("기존")
                .phone("01011111111")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .created(LocalDateTime.now())
                .lastest(LocalDateTime.now())
                .build();
        userRepository.save(existingUser);

        UserRequestDto requestDto = UserRequestDto.builder()
                .id("duplicate")
                .password("newpassword")
                .name("새 사용자")
                .nickname("새로운")
                .phone("01022222222")
                .build();

        // when & then
        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());
    }

    /**
     * 모든 사용자 조회 API 테스트
     */
    @Test
    @DisplayName("모든 사용자 조회 테스트")
    void getAllUsers_Success() throws Exception {
        // given
        User user1 = User.builder()
                .id("user1")
                .password("password")
                .name("사용자1")
                .nickname("유저1")
                .phone("01011111111")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .created(LocalDateTime.now())
                .lastest(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .id("user2")
                .password("password")
                .name("사용자2")
                .nickname("유저2")
                .phone("01022222222")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .created(LocalDateTime.now())
                .lastest(LocalDateTime.now())
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // when & then
        mockMvc.perform(get("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("user1"))
                .andExpect(jsonPath("$[1].id").value("user2"));
    }

    /**
     * 특정 사용자 조회 API 테스트 (성공)
     */
    @Test
    @DisplayName("특정 사용자 조회 성공 테스트")
    void getUserById_Success() throws Exception {
        // given
        User user = User.builder()
                .id("testuser")
                .password("password")
                .name("테스트 사용자")
                .nickname("테스터")
                .phone("01012345678")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .created(LocalDateTime.now())
                .lastest(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        // when & then
        mockMvc.perform(get("/api/users/{id}/", savedUser.getUuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("testuser"))
                .andExpect(jsonPath("$.name").value("테스트 사용자"));
    }

    /**
     * 특정 사용자 조회 API 테스트 (실패)
     */
    @Test
    @DisplayName("존재하지 않는 사용자 조회 실패 테스트")
    void getUserById_NotFound() throws Exception {
        // given
        Long nonExistentId = 999L;

        // when & then
        mockMvc.perform(get("/api/users/{id}/", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * 사용자 삭제 API 테스트 (성공)
     */
    @Test
    @DisplayName("사용자 삭제 성공 테스트")
    void deleteUser_Success() throws Exception {
        // given
        User user = User.builder()
                .id("deleteuser")
                .password("password")
                .name("삭제될 사용자")
                .nickname("삭제")
                .phone("01012345678")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .created(LocalDateTime.now())
                .lastest(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        // when & then
        mockMvc.perform(delete("/api/users/{id}/", savedUser.getUuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 사용자 삭제 API 테스트 (실패)
     */
    @Test
    @DisplayName("존재하지 않는 사용자 삭제 실패 테스트")
    void deleteUser_NotFound() throws Exception {
        // given
        Long nonExistentId = 999L;

        // when & then
        mockMvc.perform(delete("/api/users/{id}/", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}