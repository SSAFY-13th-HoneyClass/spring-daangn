package com.ssafy.spring_boot.product.dto;


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
}
