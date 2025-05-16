package org.example.springboot.repository;

import org.example.springboot.domain.Comment;
import org.example.springboot.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId AND c.parentComment IS NULL")
    List<Comment> findRootCommentsByPostId(@Param("postId") Long postId);

    // N+1 문제 해결을 위한 Fetch Join 쿼리
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.postId = :postId AND c.parentComment IS NULL")
    List<Comment> findRootCommentsByPostIdWithUser(@Param("postId") Long postId);

    // EntityGraph를 사용한 방식
    @EntityGraph(attributePaths = {"user"})
    List<Comment> findByParentComment(Comment parentComment);
}