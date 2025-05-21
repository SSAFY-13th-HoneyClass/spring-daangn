package com.example.daangn.repository;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository  extends JpaRepository<Post, Long> {
    List<Post> findAllByUserUserId(Long userId);
    List<Post> findAllByStatus(PostStatus status);
    List<Post> findByTitleContaining(String title);
}
