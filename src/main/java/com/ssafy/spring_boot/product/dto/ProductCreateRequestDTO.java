package com.ssafy.spring_boot.product.dto;

import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.category.domain.Category;
import com.ssafy.spring_boot.region.domain.Region;
import com.ssafy.spring_boot.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "상품 등록/수정 요청 DTO")
public class ProductCreateRequestDTO {

    @Schema(description = "상품 제목", example = "아이폰 15 프로", required = true)
    private String title;

    @Schema(description = "썸네일 이미지 URL", example = "iphone15.jpg", required = true)
    private String thumbnail;

    @Schema(description = "상품 설명", example = "거의 새 제품입니다. 케이스와 함께 판매해요")
    private String description;

    @Schema(description = "가격 (원)", example = "1200000")
    private Integer price;

    @Schema(description = "가격 협상 가능 여부", example = "true")
    private Boolean isNegotiable;

    @Schema(description = "판매자 ID", example = "1", required = true)
    private Integer sellerId;

    @Schema(description = "카테고리 ID", example = "1", required = true)
    private Integer categoryId;

    @Schema(description = "지역 ID", example = "1", required = true)
    private Integer regionId;

    // DTO -> Entity 변환 메소드 (정적 팩토리 메서드)
    public static Product toEntity(ProductCreateRequestDTO dto, User seller, Category category, Region region) {
        return Product.builder()
                .title(dto.getTitle())
                .thumbnail(dto.getThumbnail())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .isNegotiable(dto.getIsNegotiable() != null ? dto.getIsNegotiable() : false)
                .seller(seller)
                .category(category)
                .region(region)
                .build();
    }
}