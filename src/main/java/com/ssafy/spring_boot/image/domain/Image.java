package com.ssafy.spring_boot.image.domain;


import com.ssafy.spring_boot.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product; // 중고 상품 FK

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "`order`", nullable = false)
    private Integer order;
}