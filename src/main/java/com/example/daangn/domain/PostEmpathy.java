package com.example.daangn.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "PostEmpathys")
@Data
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
