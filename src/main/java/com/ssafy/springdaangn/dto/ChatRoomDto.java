package com.ssafy.springdaangn.dto;

import com.ssafy.springdaangn.domain.ChatRoom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomDto {
    private Long chatRoomId;
    private UserDto seller;
    private UserDto buyer;
    private PostDto.Summary post; // 게시글 요약 정보만

    // 정적 팩토리 메서드 - Entity -> DTO
    public static ChatRoomDto from(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .seller(chatRoom.getSeller() != null ? UserDto.from(chatRoom.getSeller()) : null)
                .buyer(chatRoom.getBuyer() != null ? UserDto.from(chatRoom.getBuyer()) : null)
                .post(chatRoom.getPost() != null ? PostDto.Summary.from(chatRoom.getPost()) : null)
                .build();
    }

    // 정적 팩토리 메서드 - 간단한 정보만
    public static ChatRoomDto fromSimple(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .build();
    }

    // 목록용 요약 DTO
    @Getter
    @Builder
    public static class Summary {
        private Long chatRoomId;
        private String sellerNickname;
        private String buyerNickname;
        private String postTitle;
        private Integer postPrice;

        public static Summary from(ChatRoom chatRoom) {
            return Summary.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .sellerNickname(chatRoom.getSeller().getNickname())
                    .buyerNickname(chatRoom.getBuyer().getNickname())
                    .postTitle(chatRoom.getPost().getTitle())
                    .postPrice(chatRoom.getPost().getPrice())
                    .build();
        }
    }

    // 생성용 요청 DTO
    @Getter
    @Builder
    public static class CreateRequest {
        private Long postId;
        private Long buyerId;

        public static CreateRequest of(Long postId, Long buyerId) {
            return CreateRequest.builder()
                    .postId(postId)
                    .buyerId(buyerId)
                    .build();
        }
    }
}