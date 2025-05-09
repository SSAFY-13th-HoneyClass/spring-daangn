package com.ssafy.spring_boot.comment.domain;

import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product; // 중고 상품 FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user; // 사용자 FK

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "child_count")
    private Integer childCount = 0;

    @Column
    private Integer level = 0;

    @Column(name = "hierarchy_path", length = 255)
    private String hierarchyPath;
}