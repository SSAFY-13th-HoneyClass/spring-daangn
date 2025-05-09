package org.example.springboot.repository;

import org.example.springboot.domain.Post;
import org.example.springboot.domain.PostLike;
import org.example.springboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    List<PostLike> findByPost(Post post);
    List<PostLike> findByUser(User user);
    Optional<PostLike> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.postId = :postId")
    Long countByPostId(@Param("postId") Long postId);
}