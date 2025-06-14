package com.ssafy.daangn_demo.controller;

import com.ssafy.daangn_demo.dto.JoinDTO;
import com.ssafy.daangn_demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<Objects> join(@RequestBody JoinDTO joinDTO) {
        userService.joinProcess(joinDTO);
        return ResponseEntity.ok().build();
    }
}
