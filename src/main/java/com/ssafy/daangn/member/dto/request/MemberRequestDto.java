package com.ssafy.daangn.member.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRequestDto {
    private String membername;
    private String email;
    private String password;
    private String profileUrl;
}
