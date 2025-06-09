package com.ssafy.daangn_demo.dto.request;

import lombok.Getter;

@Getter
public class ProductRequest {
    private final Long writerId;
    private final Long categoryId;
    private final Long areaId;
    private final String title;
    private final String description;
    private final int price;

    public ProductRequest(Long writerId, Long categoryId, Long areaId, String title, String description, int price) {
        this.writerId = writerId;
        this.categoryId = categoryId;
        this.areaId = areaId;
        this.title = title;
        this.description = description;
        this.price = price;
    }
}
