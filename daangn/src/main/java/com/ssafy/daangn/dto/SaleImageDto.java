package com.ssafy.daangn.dto;


import com.ssafy.daangn.domain.Sale;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SaleImageDto {

    private Long no;
    private Long saleNo;
    private String imageUrl;
}
