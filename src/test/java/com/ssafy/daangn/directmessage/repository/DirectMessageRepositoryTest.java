package com.ssafy.daangn.directmessage.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ssafy.daangn.directmessage.entity.DirectMessage;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

@DataJpaTest
class DirectMessageRepositoryTest {

    @Autowired
    private DirectMessageRepository dmRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("받는 사람 기준으로 메시지 목록 조회")
    void findByReceiver() {
        Member sender = memberRepository.save(Member.builder()
                .membername("보낸이")
                .email("sender@x.com")
                .password("pw")
                .build());

        Member receiver = memberRepository.save(Member.builder()
                .membername("받는이")
                .email("receiver@x.com")
                .password("pw")
                .build());

        dmRepository.save(DirectMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content("hello")
                .build());

        List<DirectMessage> list = dmRepository.findByReceiver(receiver);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getContent()).isEqualTo("hello");
    }

    @Test
    @DisplayName("보낸 사람과 받는 사람 기준으로 메시지 조회")
    void findBySenderAndReceiver() {
        Member sender = memberRepository.save(Member.builder()
                .membername("보낸이")
                .email("sender2@x.com")
                .password("pw")
                .build());

        Member receiver = memberRepository.save(Member.builder()
                .membername("받는이")
                .email("receiver2@x.com")
                .password("pw")
                .build());

        dmRepository.save(DirectMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content("hi")
                .build());

        List<DirectMessage> list = dmRepository.findBySenderAndReceiver(sender, receiver);

        assertThat(list).isNotEmpty();
    }
}
