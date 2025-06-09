package com.ssafy.daangn.dto;

import com.ssafy.daangn.domain.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailResponseDto {
    private Long no;
    private Long userNo; // ✅ 엔티티 대신 ID
    private String userNickname; // ✅ 필요한 정보만 추출
    private String category;
    private String status;
    private String title;
    private String content;
    private Long price;
    private Double discount;
    private Boolean isPriceSuggestible;
    private String thumbnail;
    private Integer viewCount;
    private Integer likeCount;
    private Integer chatCount;
    private String address;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
    private List<String> imageUrlList; // ✅ 이미지 URL만 전달

    public SaleDetailResponseDto(Sale sale, List<SaleImage> saleImages) {
        this.no = sale.getNo();
        this.userNo = sale.getUser().getNo(); // ✅ Lazy 접근 가능: readOnly = true + 즉시 접근
        this.userNickname = sale.getUser().getNickname(); // ✅ 필요한 데이터만
        this.category = sale.getCategory().getName(); // ✅ Category 대신 이름
        this.status = sale.getStatus().getName();     // ✅ SaleStatus 대신 이름
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

        this.imageUrlList = new ArrayList<>();
        for (SaleImage image : saleImages) {
            this.imageUrlList.add(image.getImageUrl()); // ✅ 엔티티가 아닌 값만 담기
        }
    }
}
