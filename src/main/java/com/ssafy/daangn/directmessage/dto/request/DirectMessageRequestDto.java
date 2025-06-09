package com.ssafy.daangn.directmessage.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DirectMessageRequestDto {
    private Long senderId;
    private Long receiverId;
    private String content;
}