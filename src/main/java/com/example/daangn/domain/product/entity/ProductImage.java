package com.example.daangn.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "product_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long piuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puid")
    private Product product;

    @Column(name = "product_img")
    private String productImg;
}
