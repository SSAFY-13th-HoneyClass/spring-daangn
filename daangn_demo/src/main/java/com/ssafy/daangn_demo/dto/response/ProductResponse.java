package com.ssafy.daangn_demo.dto.response;

import com.ssafy.daangn_demo.entity.ProductEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {
    private String writerName;
    private String categoryName;
    private String title;
    private String description;
    private int price;
    private String status;

    public static ProductResponse from(ProductEntity productEntity) {
        return ProductResponse.builder()
                .writerName(productEntity.getWriter().getUsername())
                .categoryName(productEntity.getCategory().getCategoryName())
                .title(productEntity.getTitle())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .status(productEntity.getStatus())
                .build();
    }
}
