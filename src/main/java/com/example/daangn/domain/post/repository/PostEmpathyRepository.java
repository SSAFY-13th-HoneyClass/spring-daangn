package com.example.daangn.domain.post.repository;

import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.post.entity.PostEmpathy;
import com.example.daangn.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostEmpathyRepository extends JpaRepository<PostEmpathy, Long> {
    List<PostEmpathy> findByPost(Post post);
    Optional<PostEmpathy> findByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);
}
