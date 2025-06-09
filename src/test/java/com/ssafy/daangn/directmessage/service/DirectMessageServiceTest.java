package com.ssafy.daangn.directmessage.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.directmessage.dto.request.DirectMessageRequestDto;
import com.ssafy.daangn.directmessage.dto.response.DirectMessageResponseDto;
import com.ssafy.daangn.directmessageroom.repository.DirectMessageRoomRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

@SpringBootTest
@Transactional
public class DirectMessageServiceTest {

    @Autowired
    private DirectMessageService dmService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private DirectMessageRoomRepository roomRepository;

    @Test
    @DisplayName("DM 전송 및 조회 테스트")
    void sendAndGetMessages() {
        Member sender = memberRepository.save(Member.builder().email("s@test.com").membername("sender").build());
        Member receiver = memberRepository.save(Member.builder().email("r@test.com").membername("receiver").build());
        Board board = boardRepository.save(Board.builder().title("b").content("c").member(sender).build());

        DirectMessageRequestDto dto = new DirectMessageRequestDto();
        dto.setSenderId(sender.getMemberId());
        dto.setReceiverId(receiver.getMemberId());
        dto.setContent("hi");

        DirectMessageResponseDto sent = dmService.sendMessage(board.getBoardId(), dto);
        List<DirectMessageResponseDto> messages = dmService.getMessagesInRoom(sent.getRoomId());

        assertThat(messages).isNotEmpty();
        assertThat(messages.get(0).getContent()).isEqualTo("hi");
    }
}