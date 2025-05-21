package com.ssafy.daangn.directmessage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.directmessage.dto.request.DirectMessageRequestDto;
import com.ssafy.daangn.directmessage.dto.response.DirectMessageResponseDto;
import com.ssafy.daangn.directmessage.entity.DirectMessage;
import com.ssafy.daangn.directmessage.repository.DirectMessageRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectMessageService {

    private final DirectMessageRepository dmRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public DirectMessageResponseDto sendMessage(DirectMessageRequestDto dto) {
        Member sender = memberRepository.findById(dto.getSenderId()).orElseThrow();
        Member receiver = memberRepository.findById(dto.getReceiverId()).orElseThrow();

        DirectMessage dm = DirectMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(dto.getContent())
                .build();

        return DirectMessageResponseDto.from(dmRepository.save(dm));
    }

    public List<DirectMessageResponseDto> getMessages(Long receiverId) {
        Member receiver = memberRepository.findById(receiverId).orElseThrow();
        return dmRepository.findByReceiver(receiver)
                .stream()
                .map(DirectMessageResponseDto::from)
                .collect(Collectors.toList());
    }
}
