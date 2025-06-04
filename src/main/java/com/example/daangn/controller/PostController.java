package com.example.daangn.controller;

import com.example.daangn.domain.post.dto.PostRequestDto;
import com.example.daangn.domain.post.dto.PostResponseDto;
import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.post.service.PostService;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 게시글 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    /**
     * 새로운 게시글 생성
     * POST /api/posts/
     */
    @PostMapping("/")
    public ResponseEntity<?> createPost(@RequestBody PostRequestDto requestDto) {
        // 사용자 존재 여부 확인
        Optional<User> user = userService.findByUID(requestDto.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("존재하지 않는 사용자입니다.");
        }

        // DTO를 엔티티로 변환
        Post post = PostRequestDto.toEntity(requestDto, user.get());

        // 게시글 저장
        Post savedPost = postService.save(post);

        // 생성된 게시글 정보를 DTO로 변환하여 반환
        PostResponseDto responseDto = PostResponseDto.fromEntity(savedPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 모든 게시글 조회
     * GET /api/posts/
     */
    @GetMapping("/")
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        List<Post> posts = postService.findAll();

        // 엔티티 리스트를 DTO 리스트로 변환
        List<PostResponseDto> responseDtos = posts.stream()
                .map(PostResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 특정 게시글 조회
     * GET /api/posts/{id}/
     */
    @GetMapping("/{id}/")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 게시글을 찾을 수 없습니다.");
        }

        PostResponseDto responseDto = PostResponseDto.fromEntity(post.get());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 특정 게시글 삭제
     * DELETE /api/posts/{id}/
     */
    @DeleteMapping("/{id}/")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        Optional<Post> post = postService.findById(id);

        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 게시글을 찾을 수 없습니다.");
        }

        postService.deleteById(id);
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
    }
}