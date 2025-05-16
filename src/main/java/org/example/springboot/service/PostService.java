package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.example.springboot.dto.PostDto;
import org.example.springboot.repository.PostRepository;
import org.example.springboot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        // 최적화된 쿼리 사용
        Post post = postRepository.findByIdWithUser(postId)
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

    /**
     * N+1 문제 발생하는 게시물 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PostDto.ListResponse> getAllPostsWithNPlus1Problem() {
        log.debug("Starting getAllPostsWithNPlus1Problem method");
        
        // 1. 먼저 모든 게시글을 조회
        List<Post> posts = postRepository.findAllPosts();
        log.debug("Fetched {} posts from repository", posts.size());
        
        // 2. Stream을 사용하여 각 게시글별로 사용자 정보를 가져와 DTO로 변환
        List<PostDto.ListResponse> result = new ArrayList<>();
        
        for (Post post : posts) {
            log.debug("Creating DTO for post ID: {}", post.getPostId());
            
            // 이 시점에서 사용자 정보에 접근하면 지연 로딩으로 인해 추가 쿼리 발생
            log.debug("About to access User data for post ID: {}", post.getPostId());
            User user = post.getUser(); // 여기서 지연 로딩 발생 가능성
            log.debug("User ID: {}", user.getUserId());
            
            // 실제 유저 데이터 접근 - 이 시점에서 N+1 쿼리 발생
            log.debug("About to access User nickname - this will trigger lazy loading if not loaded yet");
            String nickname = user.getNickname();
            log.debug("User nickname: {}", nickname);
            
            PostDto.ListResponse dto = PostDto.ListResponse.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .status(post.getStatus())
                    .userId(user.getUserId())
                    .nickname(nickname)
                    .createdAt(post.getCreatedAt())
                    .build();
                    
            result.add(dto);
        }
        
        log.debug("Completed DTO conversion for all posts");
        return result;
    }

    /**
     * N+1 문제 해결한 게시물 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PostDto.ListResponse> getAllPostsWithoutNPlus1Problem() {
        log.debug("Starting getAllPostsWithoutNPlus1Problem method");
        
        // Fetch Join을 사용해 게시글과 사용자 정보를 한 번에 조회
        List<Post> posts = postRepository.findAllPostsWithUser();
        log.debug("Fetched {} posts with users using fetch join", posts.size());
        
        List<PostDto.ListResponse> result = new ArrayList<>();
        
        for (Post post : posts) {
            log.debug("Creating DTO for post ID: {}", post.getPostId());
            
            // 이미 User 정보가 로딩되어 있으므로 추가 쿼리 발생하지 않음
            log.debug("Accessing User data for post ID: {}", post.getPostId());
            User user = post.getUser();
            log.debug("User ID: {}", user.getUserId());
            
            // 이미 로딩된 유저 데이터 접근 - 추가 쿼리 발생하지 않음
            log.debug("Accessing User nickname - this should NOT trigger additional queries");
            String nickname = user.getNickname();
            log.debug("User nickname: {}", nickname);
            
            PostDto.ListResponse dto = PostDto.ListResponse.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .status(post.getStatus())
                    .userId(user.getUserId())
                    .nickname(nickname)
                    .createdAt(post.getCreatedAt())
                    .build();
                    
            result.add(dto);
        }
        
        log.debug("Completed DTO conversion for all posts");
        return result;
    }

    /**
     * N+1 문제 해결한 게시물 목록 조회 - Entity Graph 방식
     */
    @Transactional(readOnly = true)
    public List<PostDto.ListResponse> getAllPostsWithEntityGraph() {
        log.debug("Starting getAllPostsWithEntityGraph method");
        
        // Entity Graph를 사용해 게시글과 사용자 정보를 한 번에 조회
        List<Post> posts = postRepository.findAll();
        log.debug("Fetched {} posts with users using entity graph", posts.size());
        
        List<PostDto.ListResponse> result = new ArrayList<>();
        
        for (Post post : posts) {
            log.debug("Creating DTO for post ID: {}", post.getPostId());
            
            // 이미 User 정보가 로딩되어 있으므로 추가 쿼리 발생하지 않음
            User user = post.getUser();
            
            PostDto.ListResponse dto = PostDto.ListResponse.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .status(post.getStatus())
                    .userId(user.getUserId())
                    .nickname(user.getNickname()) // Entity Graph로 이미 로딩되어 있음
                    .createdAt(post.getCreatedAt())
                    .build();
                    
            result.add(dto);
        }
        
        log.debug("Completed DTO conversion for all posts using entity graph");
        return result;
    }
}