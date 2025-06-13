package com.ssafy.springdaangn.controller;

import com.ssafy.springdaangn.service.LikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "좋아요 관리", description = "게시글 좋아요/좋아요 취소 API")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<String> likePost(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        // Global Exception Handler가 AlreadyLikedException을 자동 처리
        likeService.likePost(userId, postId);
        return ResponseEntity.ok("게시글에 좋아요를 눌렀습니다.");
    }

    @DeleteMapping
    public ResponseEntity<String> unlikePost(
            @RequestParam Long userId,
            @RequestParam Long postId) {
        // Global Exception Handler가 LikeNotFoundException을 자동 처리
        likeService.unlikePost(userId, postId);
        return ResponseEntity.ok("좋아요를 취소했습니다.");
    }
}