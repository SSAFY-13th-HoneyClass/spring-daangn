package com.example.daangn.repository;

import com.example.daangn.domain.Post;
import com.example.daangn.domain.PostEmpathy;
import com.example.daangn.domain.User;
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
