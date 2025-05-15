package com.ssafy.daangn.dto;

import com.ssafy.daangn.domain.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailResponseDto {
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
    private List<SaleImage> imageUrlList;

    public SaleDetailResponseDto(Sale sale, List<SaleImage> saleImages) {
        this.no = sale.getNo();
        this.user = sale.getUser();
        this.category = sale.getCategory();
        this.status = sale.getStatus();
        this.title = sale.getTitle();
        this.content = sale.getContent();
        this.price = sale.getPrice();
        this.discount = sale.getDiscount();
        this.isPriceSuggestible = sale.getIsPriceSuggestible();
        this.thumbnail = sale.getThumbnail();
        this.viewCount = sale.getViewCount();
        this.likeCount = sale.getLikeCount();
        this.chatCount = sale.getChatCount();
        this.address = sale.getAddress();
        this.addressDetail = sale.getAddressDetail();
        this.latitude = sale.getLatitude();
        this.longitude = sale.getLongitude();
        this.imageUrlList = saleImages;

    }
}
