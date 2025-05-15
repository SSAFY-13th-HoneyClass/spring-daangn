package com.ssafy.daangn.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SaleStatusDto {

    private String name; // 예: "ON_SALE", "RESERVED", "COMPLETED"
    private String description; // 예: "판매 중", "예약 중", "거래 완료"
}
