package org.example.springboot.repository;

import org.example.springboot.domain.Comment;
import org.example.springboot.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId AND c.parentComment IS NULL")
    List<Comment> findRootCommentsByPostId(@Param("postId") Long postId);

    List<Comment> findByParentComment(Comment parentComment);
}