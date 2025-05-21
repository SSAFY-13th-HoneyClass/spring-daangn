package com.ssafy.daangn.dto;

import com.ssafy.daangn.domain.Category;
import com.ssafy.daangn.domain.SaleImage;
import com.ssafy.daangn.domain.SaleStatus;
import com.ssafy.daangn.domain.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailRequestDto {
    private Long no; // 거래 PK
    private Long userNo; // 판매자
    private String category; // 카테고리 (문자열 PK)
    private String status; // 거래 상태 (문자열 PK)
    private String title; // 게시글 제목
    private String content; // 내용
    private Long price; // 가격
    private Double discount; // 할인율
    private Boolean isPriceSuggestible; // 제안 가능 여부
    private Integer viewCount;
    private Integer likeCount;
    private Integer chatCount;
    private String address;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
    private MultipartFile thumbnail; // 썸네일 이미지
    private List<MultipartFile> images; // 상세 이미지들
}
