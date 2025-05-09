package com.ssafy.spring_boot.product.domain;


import com.ssafy.spring_boot.category.domain.Category;
import com.ssafy.spring_boot.region.domain.Region;
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
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @ToString.Exclude
    private User seller; // 판매자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category; // 카테고리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    @ToString.Exclude
    private Region region; // 지역

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String thumbnail;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer price;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "dump_time")
    private Integer dumpTime;

    @Builder.Default
    @Column(name = "is_reserved", nullable = false)
    private Boolean isReserved = false;

    @Builder.Default
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Builder.Default
    @Column(name = "is_negotiable", nullable = false)
    private Boolean isNegotiable = false;

    @Builder.Default
    @Column(name = "chat_count", nullable = false)
    private Long chatCount = 0L;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Builder.Default
    @Column(name = "favorite_count", nullable = false)
    private Long favoriteCount = 0L;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}