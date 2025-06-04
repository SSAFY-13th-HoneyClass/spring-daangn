package com.example.daangn.controller;

import com.example.daangn.domain.post.dto.PostRequestDto;
import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.post.repository.PostRepository;
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
 * PostController 통합 테스트
 * 게시글 관련 API의 전체 동작을 검증합니다.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class PostControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;

    /**
     * 각 테스트 실행 전 MockMvc 및 테스트 데이터 설정
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 테스트용 사용자 생성
        testUser = User.builder()
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
        testUser = userRepository.save(testUser);
    }

    /**
     * 게시글 생성 API 테스트 (성공)
     */
    @Test
    @DisplayName("게시글 생성 성공 테스트")
    void createPost_Success() throws Exception {
        // given
        PostRequestDto requestDto = PostRequestDto.builder()
                .userId(testUser.getUuid())
                .subject("자유")
                .title("안녕하세요")
                .content("첫 번째 게시글입니다.")
                .postLocation("강남역")
                .postVote(false)
                .postTag("#인사")
                .hot(false)
                .build();

        // when & then
        mockMvc.perform(post("/api/posts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("안녕하세요"))
                .andExpect(jsonPath("$.subject").value("자유"))
                .andExpect(jsonPath("$.content").value("첫 번째 게시글입니다."));
    }

    /**
     * 게시글 생성 API 테스트 (존재하지 않는 사용자)
     */
    @Test
    @DisplayName("존재하지 않는 사용자로 게시글 생성 실패 테스트")
    void createPost_UserNotFound() throws Exception {
        // given
        PostRequestDto requestDto = PostRequestDto.builder()
                .userId(999L)
                .subject("자유")
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        // when & then
        mockMvc.perform(post("/api/posts/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 모든 게시글 조회 API 테스트
     */
    @Test
    @DisplayName("모든 게시글 조회 테스트")
    void getAllPosts_Success() throws Exception {
        // given
        Post post1 = Post.builder()
                .user(testUser)
                .subject("자유")
                .title("첫 번째 게시글")
                .content("첫 번째 내용")
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();

        Post post2 = Post.builder()
                .user(testUser)
                .subject("질문")
                .title("두 번째 게시글")
                .content("두 번째 내용")
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();

        postRepository.save(post1);
        postRepository.save(post2);

        // when & then
        mockMvc.perform(get("/api/posts/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("첫 번째 게시글"))
                .andExpect(jsonPath("$[1].title").value("두 번째 게시글"));
    }

    /**
     * 특정 게시글 조회 API 테스트 (성공)
     */
    @Test
    @DisplayName("특정 게시글 조회 성공 테스트")
    void getPostById_Success() throws Exception {
        // given
        Post post = Post.builder()
                .user(testUser)
                .subject("공지사항")
                .title("중요한 공지")
                .content("중요한 공지사항입니다.")
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();
        Post savedPost = postRepository.save(post);

        // when & then
        mockMvc.perform(get("/api/posts/{id}/", savedPost.getPuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("중요한 공지"))
                .andExpect(jsonPath("$.subject").value("공지사항"));
    }

    /**
     * 특정 게시글 조회 API 테스트 (실패)
     */
    @Test
    @DisplayName("존재하지 않는 게시글 조회 실패 테스트")
    void getPostById_NotFound() throws Exception {
        // given
        Long nonExistentId = 999L;

        // when & then
        mockMvc.perform(get("/api/posts/{id}/", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * 게시글 삭제 API 테스트 (성공)
     */
    @Test
    @DisplayName("게시글 삭제 성공 테스트")
    void deletePost_Success() throws Exception {
        // given
        Post post = Post.builder()
                .user(testUser)
                .subject("임시")
                .title("삭제될 게시글")
                .content("삭제 테스트")
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();
        Post savedPost = postRepository.save(post);

        // when & then
        mockMvc.perform(delete("/api/posts/{id}/", savedPost.getPuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 게시글 삭제 API 테스트 (실패)
     */
    @Test
    @DisplayName("존재하지 않는 게시글 삭제 실패 테스트")
    void deletePost_NotFound() throws Exception {
        // given
        Long nonExistentId = 999L;

        // when & then
        mockMvc.perform(delete("/api/posts/{id}/", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}