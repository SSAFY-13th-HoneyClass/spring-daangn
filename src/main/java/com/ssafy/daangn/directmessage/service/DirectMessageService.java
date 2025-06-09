package com.ssafy.daangn.directmessage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.directmessage.dto.request.DirectMessageRequestDto;
import com.ssafy.daangn.directmessage.dto.response.DirectMessageResponseDto;
import com.ssafy.daangn.directmessage.entity.DirectMessage;
import com.ssafy.daangn.directmessage.repository.DirectMessageRepository;
import com.ssafy.daangn.directmessageroom.entity.DirectMessageRoom;
import com.ssafy.daangn.directmessageroom.repository.DirectMessageRoomRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectMessageService {

    private final DirectMessageRepository dmRepository;
    private final DirectMessageRoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public DirectMessageResponseDto sendMessage(Long boardId, DirectMessageRequestDto dto) {
        Member sender = memberRepository.findById(dto.getSenderId()).orElseThrow();
        Member receiver = memberRepository.findById(dto.getReceiverId()).orElseThrow();
        Board board = boardRepository.findById(boardId).orElseThrow();

        DirectMessageRoom room = roomRepository
                .findByBoardAndSenderAndReceiver(board, sender, receiver)
                .orElseGet(() -> roomRepository.save(
                        DirectMessageRoom.builder()
                                .board(board)
                                .sender(sender)
                                .receiver(receiver)
                                .build()
                ));

        DirectMessage dm = DirectMessage.builder()
                .room(room)
                .sender(sender)
                .receiver(receiver)
                .content(dto.getContent())
                .build();

        return DirectMessageResponseDto.from(dmRepository.save(dm));
    }

    public List<DirectMessageResponseDto> getMessagesInRoom(Long roomId) {
        DirectMessageRoom room = roomRepository.findById(roomId).orElseThrow();
        return dmRepository.findByRoomOrderByCreatedAtAsc(room).stream()
                .map(DirectMessageResponseDto::from)
                .collect(Collectors.toList());
    }
}