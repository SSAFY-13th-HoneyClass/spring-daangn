package com.example.daangn.domain.product.dto;

import com.example.daangn.domain.location.entity.Location;
import com.example.daangn.domain.product.entity.Product;
import com.example.daangn.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class ProductRequestDto {

    private Long locationId;
    private Long userId;
    private String title;
    private String category;
    private String content;
    private String dealType;
    private Integer price;
    private Boolean isSell;


    public static Product toEntity(ProductRequestDto dto, Location location, User user) {
        return Product.builder()
                .location(location)
                .user(user)
                .title(dto.getTitle())
                .category(dto.getCategory())
                .content(dto.getContent())
                .dealType(dto.getDealType() != null ? dto.getDealType() : "판매하기")
                .price(dto.getPrice() != null ? dto.getPrice() : 0)
                .created(LocalDateTime.now())
                .views(0)
                .isSell(dto.getIsSell() != null ? dto.getIsSell() : false)
                .build();
    }
}