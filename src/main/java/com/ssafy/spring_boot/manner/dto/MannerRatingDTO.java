// src/main/java/com/ssafy/spring_boot/manner/dto/MannerRatingDTO.java
package com.ssafy.spring_boot.manner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MannerRatingDTO {
    private Long id;
    private Long ratedUserId;
    private String ratedUserNickname;
    private Long raterUserId;
    private String raterUserNickname;
    private Integer detailId;
    private String detailContent;
}