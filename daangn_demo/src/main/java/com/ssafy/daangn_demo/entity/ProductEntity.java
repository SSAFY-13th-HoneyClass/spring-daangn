package com.ssafy.daangn_demo.entity;

import com.ssafy.daangn_demo.dto.request.ProductRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private UserEntity writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private AreaEntity area;

    private String title;
    private String description;
    private int price;
    private String status;

    public static ProductEntity from(ProductRequest productRequest, UserEntity writer, CategoryEntity category, AreaEntity area) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.writer = writer;
        productEntity.category = category;
        productEntity.area = area;
        productEntity.title = productRequest.getTitle();
        productEntity.description = productRequest.getDescription();
        productEntity.price = productRequest.getPrice();
        return productEntity;
    }
}
