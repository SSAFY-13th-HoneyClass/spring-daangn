package com.ssafy.daangn.directmessageroom.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.directmessageroom.dto.response.DirectMessageRoomResponseDto;
import com.ssafy.daangn.directmessageroom.entity.DirectMessageRoom;
import com.ssafy.daangn.directmessageroom.repository.DirectMessageRoomRepository;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectMessageRoomService {

    private final DirectMessageRoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    // 1:1 방 단일 조회 또는 생성
    @Transactional
    public DirectMessageRoomResponseDto getOrCreateRoom(Long boardId, Long senderId, Long receiverId) {
        Optional<DirectMessageRoom> optionalRoom =
            roomRepository.findRoomByBoardAndMembers(boardId, senderId, receiverId);

        if (optionalRoom.isPresent()) {
            return DirectMessageRoomResponseDto.from(optionalRoom.get());
        }

        // 방이 없으면 새로 생성
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("보내는 사람 없음"));
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("받는 사람 없음"));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        DirectMessageRoom room = DirectMessageRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .board(board)
                .build();

        return DirectMessageRoomResponseDto.from(roomRepository.save(room));
    }

    // 특정 게시글에 대한 내가 참여 중인 DM 방들
    public List<DirectMessageRoomResponseDto> getRoomsForBoard(Long boardId, Long memberId) {
        return roomRepository.findByBoardIdAndMemberId(boardId, memberId)
                .stream()
                .map(DirectMessageRoomResponseDto::from)
                .collect(Collectors.toList());
    }

    // 내가 참여한 전체 DM 방 조회
    public List<DirectMessageRoomResponseDto> getRoomsByMember(Long memberId) {
        return roomRepository.findAllByMemberId(memberId)
                .stream()
                .map(DirectMessageRoomResponseDto::from)
                .collect(Collectors.toList());
    }
}
