package com.ssafy.daangn.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SaleLikeDto{

    private Long no;
    private Long userNo;
    private Long saleNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
