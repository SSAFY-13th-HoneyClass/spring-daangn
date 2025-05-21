package com.ssafy.daangn.directmessage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ssafy.daangn.directmessage.dto.request.DirectMessageRequestDto;
import com.ssafy.daangn.directmessage.dto.response.DirectMessageResponseDto;
import com.ssafy.daangn.directmessage.entity.DirectMessage;
import com.ssafy.daangn.directmessage.repository.DirectMessageRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class DirectMessageServiceTest {

    @Mock
    private DirectMessageRepository dmRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private DirectMessageService dmService;

    private Member sender;
    private Member receiver;

    @BeforeEach
    void setup() {
        sender = Member.builder().memberId(1L).build();
        receiver = Member.builder().memberId(2L).build();
    }

    @Test
    void sendMessage_shouldReturnDto() {
        DirectMessageRequestDto dto = new DirectMessageRequestDto();
        dto.setSenderId(1L);
        dto.setReceiverId(2L);
        dto.setContent("Hi!");

        DirectMessage dm = DirectMessage.builder()
                .messageId(100L)
                .sender(sender)
                .receiver(receiver)
                .content("Hi!")
                .isDeleted(false)
                .isRead(false)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(dmRepository.save(any(DirectMessage.class))).thenReturn(dm);

        DirectMessageResponseDto response = dmService.sendMessage(dto);

        assertThat(response.getContent()).isEqualTo("Hi!");
        assertThat(response.getSenderId()).isEqualTo(1L);
    }

    @Test
    void getMessages_shouldReturnList() {
        when(memberRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(dmRepository.findByReceiver(receiver)).thenReturn(
                List.of(DirectMessage.builder()
                        .messageId(1L)
                        .sender(sender)
                        .receiver(receiver)
                        .content("Hello")
                        .isDeleted(false)
                        .isRead(false)
                        .build())
        );

        List<DirectMessageResponseDto> result = dmService.getMessages(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Hello");
    }
}
