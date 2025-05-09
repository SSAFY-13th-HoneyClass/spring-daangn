package com.ssafy.daangn_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "images")
@Getter
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    private String imageName;
    private String imageUrl;
}
