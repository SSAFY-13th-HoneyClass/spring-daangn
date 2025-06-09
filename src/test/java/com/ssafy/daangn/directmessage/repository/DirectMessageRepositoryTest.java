package com.ssafy.daangn.directmessage.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.directmessage.entity.DirectMessage;
import com.ssafy.daangn.directmessageroom.entity.DirectMessageRoom;
import com.ssafy.daangn.directmessageroom.repository.DirectMessageRoomRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

@DataJpaTest
public class DirectMessageRepositoryTest {

    @Autowired
    private DirectMessageRepository dmRepository;
    @Autowired
    private DirectMessageRoomRepository roomRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("DM 저장 및 채팅방 기준 조회")
    void saveAndFindByRoom() {
        Member sender = memberRepository.save(Member.builder().email("sender@example.com").membername("Sender").build());
        Member receiver = memberRepository.save(Member.builder().email("receiver@example.com").membername("Receiver").build());
        Board board = boardRepository.save(Board.builder().title("Test Board").content("Test").member(sender).build());
        DirectMessageRoom room = roomRepository.save(DirectMessageRoom.builder().board(board).sender(sender).receiver(receiver).build());

        DirectMessage dm = DirectMessage.builder().room(room).sender(sender).receiver(receiver).content("Hello").build();
        dmRepository.save(dm);

        List<DirectMessage> messages = dmRepository.findByRoomOrderByCreatedAtAsc(room);
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("Hello");
    }
}