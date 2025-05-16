package com.ssafy.spring_boot.comment.repository;

import com.ssafy.spring_boot.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findAllByProduct_Id(Long productId);
    List<Comment> findAllByProductIdOrderByCreateAtAsc(Long productId);
}
