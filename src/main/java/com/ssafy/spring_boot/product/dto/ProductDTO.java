package com.ssafy.spring_boot.product.dto;

import com.ssafy.spring_boot.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductDTO {
    private Long id;
    private String title;
    private String thumbnail;
    private String description;
    private Integer price;
    private LocalDateTime createdAt;
    private Integer dumpTime;

    private Boolean isReserved;
    private Boolean isCompleted;
    private Boolean isNegotiable;

    private Long chatCount;
    private Long viewCount;
    private Long favoriteCount;

    private Integer sellerId;
    private String sellerNickname;

    private Integer categoryId;
    private String categoryName;

    private Integer regionId;
    private String regionName;

    // Entity -> DTO 변환 메소드 추가
    public static ProductDTO from(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .dumpTime(product.getDumpTime())
                .isReserved(product.getIsReserved())
                .isCompleted(product.getIsCompleted())
                .isNegotiable(product.getIsNegotiable())
                .chatCount(product.getChatCount())
                .viewCount(product.getViewCount())
                .favoriteCount(product.getFavoriteCount())
                .sellerId(product.getSeller().getId())
                .sellerNickname(product.getSeller().getNickname())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getType())
                .regionId(product.getRegion().getId())
                .regionName(product.getRegion().getName())
                .build();
    }
}