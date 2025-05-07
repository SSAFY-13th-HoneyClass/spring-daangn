package com.ssafy.daangn.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "sale_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleStatus {

    @Id
    private String name; // 예: "ON_SALE", "RESERVED", "COMPLETED"

//    private String description; // 예: "판매 중", "예약 중", "거래 완료"
}
