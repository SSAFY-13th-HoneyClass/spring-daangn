package org.example.springboot.repository;

import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findByStatus(String status);

    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllOrderByCreatedAtDesc();

    // N+1 문제 해결을 위한 Fetch Join 쿼리
    @Query("SELECT p FROM Post p JOIN FETCH p.user ORDER BY p.createdAt DESC")
    List<Post> findAllWithUserOrderByCreatedAtDesc();

    // N+1 문제 확인용 메서드 추가
    @Query("SELECT p FROM Post p")
    List<Post> findAllPosts();

    // N+1 문제 해결용 메서드 추가
    @Query("SELECT p FROM Post p JOIN FETCH p.user")
    List<Post> findAllPostsWithUser();

    // 단일 게시물 조회 시 N+1 문제 해결
    @Query("SELECT p FROM Post p JOIN FETCH p.user WHERE p.postId = :postId")
    Optional<Post> findByIdWithUser(@Param("postId") Long postId);
}