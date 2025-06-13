package com.example.daangn.domain.post.entity;

import com.example.daangn.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long puid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String subject;

    private String title;

    private String content;

    @Column(name = "post_loaction")
    private String postLocation;

    @Column(name = "post_vote")
    private Boolean postVote;

    @Column(name = "post_tag")
    private String postTag;

    private Boolean hot;

    @CreationTimestamp
    private LocalDateTime created;

    private Integer views;

    private Integer bookmarks;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEmpathy> empathies = new ArrayList<>();
}
