package com.example.daangn.domain.post.entity;

import com.example.daangn.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "PostEmpathys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEmpathy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long peuid;

    private Integer empathy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
