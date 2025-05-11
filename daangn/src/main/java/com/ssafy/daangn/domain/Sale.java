package com.ssafy.daangn.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 거래 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user; // 판매자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_name", nullable = false)
    private Category category; // 카테고리 (문자열 PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_name", nullable = false)
    private SaleStatus status; // 거래 상태 (문자열 PK)

    private String title; // 게시글 제목

    @Column(length = 2000)
    private String content; // 내용

    private Long price; // 가격

    private Double discount; // 할인율

    @Column(name = "is_price_suggestible")
    private Boolean isPriceSuggestible; // 제안 가능 여부

    private String thumbnail; // 썸네일 URL

    private Integer viewCount;

    private Integer likeCount;

    private Integer chatCount;

    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    private Double latitude;

    private Double longitude;
}
