package com.ssafy.daangn.directmessage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.daangn.directmessage.entity.DirectMessage;
import com.ssafy.daangn.member.entity.Member;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    List<DirectMessage> findByReceiver(Member receiver);
    List<DirectMessage> findBySenderAndReceiver(Member sender, Member receiver);
}
