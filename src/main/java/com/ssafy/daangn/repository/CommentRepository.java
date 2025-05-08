package com.ssafy.daangn.repository;

import com.ssafy.daangn.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}