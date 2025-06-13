package com.ssafy.springdaangn.dto;

import com.ssafy.springdaangn.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDto {
    private Long postId;
    private String title;
    private String description;
    private Integer price;
    private String status;
    private String categoryId;
    private Integer views;
    private Integer likeCount;
    private Integer chatRoomCount;
    private UserDto seller;
    private AddressDto location;

    // 정적 팩토리 메서드 - Entity -> DTO
    public static PostDto from(Post post) {
        return PostDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .description(post.getDescription())
                .price(post.getPrice())
                .status(post.getStatus())
                .categoryId(post.getCategoryId())
                .views(post.getViews())
                .likeCount(post.getLikeCount())
                .chatRoomCount(post.getChatRoomCount())
                .seller(post.getSeller() != null ? UserDto.from(post.getSeller()) : null)
                .location(post.getLocation() != null ? AddressDto.from(post.getLocation()) : null)
                .build();
    }

    // 정적 팩토리 메서드 - 간단한 정보만
    public static PostDto fromSimple(Post post) {
        return PostDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .price(post.getPrice())
                .status(post.getStatus())
                .views(post.getViews())
                .likeCount(post.getLikeCount())
                .build();
    }

    // 생성용 요청 DTO
    @Getter
    @Builder
    public static class CreateRequest {
        private String title;
        private String description;
        private Integer price;
        private String status;
        private String categoryId;

        public static CreateRequest of(String title, String description, Integer price,
                                       String status, String categoryId) {
            return CreateRequest.builder()
                    .title(title)
                    .description(description)
                    .price(price)
                    .status(status)
                    .categoryId(categoryId)
                    .build();
        }

        public Post toEntity() {
            return Post.builder()
                    .title(this.title)
                    .description(this.description)
                    .price(this.price)
                    .status(this.status)
                    .categoryId(this.categoryId)
                    .views(0)
                    .likeCount(0)
                    .chatRoomCount(0)
                    .build();
        }
    }

    // 수정용 요청 DTO
    @Getter
    @Builder
    public static class UpdateRequest {
        private String title;
        private String description;
        private Integer price;
        private String status;

        public static UpdateRequest of(String title, String description,
                                       Integer price, String status) {
            return UpdateRequest.builder()
                    .title(title)
                    .description(description)
                    .price(price)
                    .status(status)
                    .build();
        }
    }

    // 목록용 간단한 DTO
    @Getter
    @Builder
    public static class Summary {
        private Long postId;
        private String title;
        private Integer price;
        private String status;
        private Integer views;
        private Integer likeCount;
        private String sellerNickname;

        public static Summary from(Post post) {
            return Summary.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .price(post.getPrice())
                    .status(post.getStatus())
                    .views(post.getViews())
                    .likeCount(post.getLikeCount())
                    .sellerNickname(post.getSeller().getNickname())
                    .build();
        }
    }
}