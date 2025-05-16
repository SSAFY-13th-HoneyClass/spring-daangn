package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.example.springboot.dto.PostDto;
import org.example.springboot.repository.PostRepository;
import org.example.springboot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 1. 게시물 생성
     */
    @Transactional
    public Long createPost(PostDto.CreateRequest requestDto, Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        Post post = Post.builder()
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .status("판매중")
                .build();

        Post savedPost = postRepository.save(post);
        return savedPost.getPostId();
    }

    /**
     * 2. 게시물 상세 조회
     */
    @Transactional(readOnly = true)
    public PostDto.DetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        User user = post.getUser();

        return PostDto.DetailResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .status(post.getStatus())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImgPath(user.getProfileImgPath())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /**
     * 3. 게시물 상태 업데이트
     */
    @Transactional
    public Long updatePostStatus(Long postId, String status, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다."));

        // 게시물 작성자 확인
        if (!post.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시물 수정 권한이 없습니다.");
        }

        // 상태 유효성 검사
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("유효하지 않은 상태값입니다.");
        }

        // 게시물 상태 업데이트
        post.updateStatus(status);

        return post.getPostId();
    }

    // 게시물 상태 유효성 검사 메서드
    private boolean isValidStatus(String status) {
        return status.equals("판매중") || status.equals("예약중") || status.equals("판매완료");
    }
}