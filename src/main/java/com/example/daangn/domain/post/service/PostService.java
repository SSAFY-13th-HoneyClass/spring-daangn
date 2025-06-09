package com.example.daangn.domain.post.service;

import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

    /**
     * 모든 게시글 조회
     */
    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    /**
     * ID로 특정 게시글 조회
     */
    @Transactional(readOnly = true)
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    /**
     * 게시글 저장 (생성/수정)
     */
    public Post save(Post post) {
        return postRepository.save(post);
    }

    /**
     * 게시글 삭제
     */
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}