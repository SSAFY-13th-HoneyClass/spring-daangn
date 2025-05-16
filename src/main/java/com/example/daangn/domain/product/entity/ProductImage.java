package com.example.daangn.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ProductImages")
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
    @JoinColumn(name = "puid", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String productImg;
}
