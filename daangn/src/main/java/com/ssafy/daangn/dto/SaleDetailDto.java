package com.ssafy.daangn.dto;

import com.ssafy.daangn.domain.Category;
import com.ssafy.daangn.domain.SaleStatus;
import com.ssafy.daangn.domain.User;
import jakarta.persistence.*;

import java.util.List;

public class SaleDetailDto {
    private Long no; // 거래 PK
    private User user; // 판매자
    private Category category; // 카테고리 (문자열 PK)
    private SaleStatus status; // 거래 상태 (문자열 PK)
    private String title; // 게시글 제목
    private String content; // 내용
    private Long price; // 가격
    private Double discount; // 할인율
    private Boolean isPriceSuggestible; // 제안 가능 여부
    private String thumbnail; // 썸네일 URL
    private Integer viewCount;
    private Integer likeCount;
    private Integer chatCount;
    private String address;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
    private List<String> imageUrlList;
}
