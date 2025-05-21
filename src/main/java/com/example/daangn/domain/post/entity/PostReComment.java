package com.example.daangn.domain.post.entity;

import com.example.daangn.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PostReComments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prcuid;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private Integer likes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "recomment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCommentLike> commentLikes = new ArrayList<>();
}
