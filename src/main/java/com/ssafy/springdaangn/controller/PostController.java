package com.ssafy.springdaangn.controller;

import com.ssafy.springdaangn.domain.Post;
import com.ssafy.springdaangn.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PostController {

    private final PostService postService;

    // 1. 새로운 게시글 생성
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestParam Long userId,
            @RequestBody Post post) {
        Post createdPost = postService.createPost(userId, post);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    // 2. 특정 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable Long postId) {
        Post post = postService.getPost(postId);
        return ResponseEntity.ok(post);
    }

    // 3. 특정 사용자의 게시글 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }

    // 4. 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long postId,
            @RequestBody Post post) {
        Post updatedPost = postService.updatePost(postId, post);
        return ResponseEntity.ok(updatedPost);
    }

    // 5. 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // 6. 게시글 조회수 증가
    @PatchMapping("/{postId}/views")
    public ResponseEntity<Void> incrementViews(@PathVariable Long postId) {
        postService.incrementViews(postId);
        return ResponseEntity.ok().build();
    }
}