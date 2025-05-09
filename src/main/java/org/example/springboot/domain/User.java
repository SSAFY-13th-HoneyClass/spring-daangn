package org.example.springboot.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile", nullable = false)
    private String profile;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "profile_img_path", nullable = false)
    private String profileImgPath;

    @Column(name = "role", nullable = false)
    private String role;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 관계 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostLike> postLikes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Chatting> chattings;
}