package org.example.springboot.repository;

import org.example.springboot.domain.ChattingRoom;
import org.example.springboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {
    List<ChattingRoom> findByUser(User user);
    List<ChattingRoom> findByPostId(Long postId);
    Optional<ChattingRoom> findByUserAndPostId(User user, Long postId);

    @Query("SELECT cr FROM ChattingRoom cr WHERE cr.postId IN (SELECT p.postId FROM Post p WHERE p.user = :user)")
    List<ChattingRoom> findByPostOwner(@Param("user") User user);
}