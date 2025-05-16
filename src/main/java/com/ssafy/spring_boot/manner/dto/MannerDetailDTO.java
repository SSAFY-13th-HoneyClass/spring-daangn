// src/main/java/com/ssafy/spring_boot/manner/dto/MannerDetailDTO.java
package com.ssafy.spring_boot.manner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MannerDetailDTO {
    private Integer id;
    private String content;
}