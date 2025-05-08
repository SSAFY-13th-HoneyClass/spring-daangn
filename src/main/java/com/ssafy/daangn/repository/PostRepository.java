package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}