package com.ssafy.daangn.directmessage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.directmessage.entity.DirectMessageRoom;
import com.ssafy.daangn.member.entity.Member;

public interface DirectMessageRoomRepository extends JpaRepository<DirectMessageRoom, Long> {

    Optional<DirectMessageRoom> findByBoardAndSenderAndReceiver(Board board, Member sender, Member receiver);

    List<DirectMessageRoom> findByBoardAndSenderOrReceiver(Board board, Member sender, Member receiver);

    List<DirectMessageRoom> findBySenderOrReceiver(Member sender, Member receiver);

    Optional<DirectMessageRoom> findByBoardAndSenderAndReceiverOrBoardAndSenderAndReceiver(
        Board board1, Member sender1, Member receiver1,
        Board board2, Member sender2, Member receiver2
    );

    @Query("""
        SELECT r FROM DirectMessageRoom r
        WHERE r.board.boardId = :boardId
        AND ((r.sender.memberId = :senderId AND r.receiver.memberId = :receiverId)
             OR (r.sender.memberId = :receiverId AND r.receiver.memberId = :senderId))
    """)
    Optional<DirectMessageRoom> findRoomByBoardAndMembers(@Param("boardId") Long boardId,
                                                          @Param("senderId") Long senderId,
                                                          @Param("receiverId") Long receiverId);

    @Query("""
        SELECT r FROM DirectMessageRoom r
        WHERE r.board.boardId = :boardId
        AND (r.sender.memberId = :memberId OR r.receiver.memberId = :memberId)
    """)
    List<DirectMessageRoom> findByBoardIdAndMemberId(@Param("boardId") Long boardId,
                                                     @Param("memberId") Long memberId);

    @Query("""
        SELECT r FROM DirectMessageRoom r
        WHERE r.sender.memberId = :memberId OR r.receiver.memberId = :memberId
    """)
    List<DirectMessageRoom> findAllByMemberId(@Param("memberId") Long memberId);
}
