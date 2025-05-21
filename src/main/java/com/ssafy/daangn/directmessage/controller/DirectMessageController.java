package com.ssafy.daangn.directmessage.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.daangn.directmessage.dto.request.DirectMessageRequestDto;
import com.ssafy.daangn.directmessage.dto.response.DirectMessageResponseDto;
import com.ssafy.daangn.directmessage.service.DirectMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class DirectMessageController {

    private final DirectMessageService directMessageService;

    @PostMapping
    public ResponseEntity<DirectMessageResponseDto> sendMessage(@RequestBody DirectMessageRequestDto dto) {
        return ResponseEntity.ok(directMessageService.sendMessage(dto));
    }

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<DirectMessageResponseDto>> getMessages(@PathVariable Long receiverId) {
        return ResponseEntity.ok(directMessageService.getMessages(receiverId));
    }
}
