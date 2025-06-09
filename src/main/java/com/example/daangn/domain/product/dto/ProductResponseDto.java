package com.example.daangn.domain.product.dto;

import com.example.daangn.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class ProductResponseDto {

    private Long puid;
    private Long locationId;
    private String locationName;
    private Long userId;
    private String userNickname;
    private String title;
    private String category;
    private String content;
    private String dealType;
    private Integer price;
    private LocalDateTime created;
    private Integer views;
    private Boolean isSell;


    public static ProductResponseDto fromEntity(Product product) {
        return ProductResponseDto.builder()
                .puid(product.getPuid())
                .locationId(product.getLocation().getLuid())
                .locationName(product.getLocation().getSi() + " " +
                        product.getLocation().getGugun() + " " +
                        product.getLocation().getLocation())
                .userId(product.getUser().getUuid())
                .userNickname(product.getUser().getNickname())
                .title(product.getTitle())
                .category(product.getCategory())
                .content(product.getContent())
                .dealType(product.getDealType())
                .price(product.getPrice())
                .created(product.getCreated())
                .views(product.getViews())
                .isSell(product.getIsSell())
                .build();
    }
}