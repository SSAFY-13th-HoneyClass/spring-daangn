package com.example.daangn.domain.post.dto;

import com.example.daangn.domain.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponseDto {

    private Long puid;
    private Long userId;
    private String userNickname;
    private String subject;
    private String title;
    private String content;
    private String postLocation;
    private Boolean postVote;
    private String postTag;
    private Boolean hot;
    private LocalDateTime created;
    private Integer views;
    private Integer bookmarks;


    public static PostResponseDto fromEntity(Post post) {
        return PostResponseDto.builder()
                .puid(post.getPuid())
                .userId(post.getUser().getUuid())
                .userNickname(post.getUser().getNickname())
                .subject(post.getSubject())
                .title(post.getTitle())
                .content(post.getContent())
                .postLocation(post.getPostLocation())
                .postVote(post.getPostVote())
                .postTag(post.getPostTag())
                .hot(post.getHot())
                .created(post.getCreated())
                .views(post.getViews())
                .bookmarks(post.getBookmarks())
                .build();
    }
}