package com.example.daangn.repository;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostReComment;
import com.example.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostReCommentRepository extends JpaRepository<PostReComment, Long> {
    List<PostReComment> findByPost(Post post);
    List<PostReComment> findByUser(User user);
    long countByPost(Post post);
}
