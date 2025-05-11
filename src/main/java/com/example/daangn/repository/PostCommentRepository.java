package com.example.daangn.repository;


import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostComment;
import com.example.daangn.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPost(Post post);
    List<PostComment> findByUser(User user);
    long countByPost(Post post);
}
