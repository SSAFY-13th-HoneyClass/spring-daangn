package com.ssafy.spring_boot.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryDTO {
    private Integer id;      // 카테고리 ID
    private String type;     // 카테고리 이름
}
