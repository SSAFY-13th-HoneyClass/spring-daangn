package com.ssafy.spring_boot.chat.dto;

import com.ssafy.spring_boot.chat.domain.ChatRoom;
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

    // Entity -> DTO 변환 메소드
    public static ChatRoomDTO from(ChatRoom chatRoom) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .productId(chatRoom.getProduct().getId())
                .productTitle(chatRoom.getProduct().getTitle())
                .productThumbnail(chatRoom.getProduct().getThumbnail())
                .buyerId(chatRoom.getBuyer().getId())
                .buyerNickname(chatRoom.getBuyer().getNickname())
                .sellerId(chatRoom.getSeller().getId())
                .sellerNickname(chatRoom.getSeller().getNickname())
                .build();
    }
}