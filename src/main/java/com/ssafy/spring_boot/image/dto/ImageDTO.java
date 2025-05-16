package com.ssafy.spring_boot.image.dto;

import com.ssafy.spring_boot.image.domain.Image;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageDTO {
    private Long id;
    private Long productId;
    private String imageUrl;
    private Integer order;

    // Entity -> DTO 변환 메소드
    public static ImageDTO from(Image image) {
        return ImageDTO.builder()
                .id(image.getId())
                .productId(image.getProduct().getId())
                .imageUrl(image.getImageUrl())
                .order(image.getOrder())
                .build();
    }
}