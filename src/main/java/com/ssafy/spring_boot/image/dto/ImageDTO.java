package com.ssafy.spring_boot.image.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageDTO {
    private Long id;          // 이미지 ID
    private Long productId;   // 연결된 상품 ID
    private String imageUrl;  // 이미지 URL
    private Integer order;    // 이미지 순서
}
