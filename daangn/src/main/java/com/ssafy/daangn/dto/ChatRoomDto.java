package com.ssafy.daangn.dto;


import com.ssafy.daangn.domain.BaseEntity;
import com.ssafy.daangn.domain.Sale;
import com.ssafy.daangn.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {

    private Long no;
    private Long saleNo;
    private Long sellerNo;
    private Long buyerNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
