package com.ssafy.spring_boot.chat.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomDTO {
    private Long id;

    private Long productId;
    private String productTitle;
    private String productThumbnail;

    private Integer buyerId;
    private String buyerNickname;

    private Integer sellerId;
    private String sellerNickname;
}
