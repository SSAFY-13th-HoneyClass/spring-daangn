package com.ssafy.daangn.dto;

import com.ssafy.daangn.domain.ChatRoom;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatRoomResponseDto {
    private Long no;
    private Long saleNo;
    private String saleTitle;
    private Long sellerNo;
    private String sellerNickname;
    private Long buyerNo;
    private String buyerNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatRoomResponseDto(ChatRoom chatRoom) {
        this.no = chatRoom.getNo();
        this.saleNo = chatRoom.getSale().getNo();
        this.saleTitle = chatRoom.getSale().getTitle();
        this.sellerNickname = chatRoom.getSeller().getNickname();
        this.buyerNickname = chatRoom.getBuyer().getNickname();

    }
}