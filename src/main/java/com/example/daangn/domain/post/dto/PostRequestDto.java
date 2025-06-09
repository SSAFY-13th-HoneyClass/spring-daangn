package com.example.daangn.domain.post.dto;

import com.example.daangn.domain.post.entity.Post;
import com.example.daangn.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostRequestDto {

    private Long userId;
    private String subject;
    private String title;
    private String content;
    private String postLocation;
    private Boolean postVote;
    private String postTag;
    private Boolean hot;

    public static Post toEntity(PostRequestDto dto, User user) {
        return Post.builder()
                .user(user)
                .subject(dto.getSubject())
                .title(dto.getTitle())
                .content(dto.getContent())
                .postLocation(dto.getPostLocation())
                .postVote(dto.getPostVote())
                .postTag(dto.getPostTag())
                .hot(dto.getHot() != null ? dto.getHot() : false)
                .created(LocalDateTime.now())
                .views(0)
                .bookmarks(0)
                .build();
    }
}