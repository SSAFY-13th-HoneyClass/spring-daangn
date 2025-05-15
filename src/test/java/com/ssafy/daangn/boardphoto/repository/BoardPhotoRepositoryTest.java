package com.ssafy.daangn.boardphoto.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ssafy.daangn.board.entity.Board;
import com.ssafy.daangn.board.repository.BoardRepository;
import com.ssafy.daangn.boardphoto.entity.BoardPhoto;
import com.ssafy.daangn.member.entity.Member;
import com.ssafy.daangn.member.repository.MemberRepository;

@DataJpaTest
class BoardPhotoRepositoryTest {

    @Autowired
    private BoardPhotoRepository boardPhotoRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("게시글 ID로 사진을 정렬된 순서로 조회")
    void findByBoardIdOrderByPhotoOrder() {
        Member member = memberRepository.save(Member.builder()
                .membername("user")
                .email("test@example.com")
                .password("1234")
                .build());

        Board board = boardRepository.save(Board.builder()
                .member(member)
                .title("Test")
                .content("Content")
                .build());

        boardPhotoRepository.save(BoardPhoto.builder().board(board).url("url1").photoOrder(2).build());
        boardPhotoRepository.save(BoardPhoto.builder().board(board).url("url2").photoOrder(1).build());

        List<BoardPhoto> photos = boardPhotoRepository.findByBoard_BoardIdOrderByPhotoOrder(board.getBoardId());

        assertThat(photos).hasSize(2);
        assertThat(photos.get(0).getPhotoOrder()).isEqualTo(1);
    }
}
