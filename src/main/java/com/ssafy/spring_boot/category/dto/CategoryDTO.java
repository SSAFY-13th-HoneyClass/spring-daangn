package com.ssafy.spring_boot.category.dto;

import com.ssafy.spring_boot.category.domain.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryDTO {
    private Integer id;
    private String type;

    // Entity -> DTO 변환 메소드
    public static CategoryDTO from(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .type(category.getType())
                .build();
    }
}