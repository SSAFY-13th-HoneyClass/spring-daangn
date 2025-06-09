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

@DataJpaTest
class BoardPhotoRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardPhotoRepository boardPhotoRepository;

    @Test
    @DisplayName("게시글 ID로 생성 시각 순으로 사진을 조회한다")
    void findByBoard_BoardIdOrderByCreatedAtAsc() {
        // given
        Board board = boardRepository.save(Board.builder()
                .title("테스트 게시글")
                .content("테스트 내용")
                .isDeleted(false)
                .build());

        BoardPhoto photo1 = BoardPhoto.builder()
                .board(board)
                .url("https://img.com/first.jpg")
                .build();

        BoardPhoto photo2 = BoardPhoto.builder()
                .board(board)
                .url("https://img.com/second.jpg")
                .build();

        boardPhotoRepository.save(photo1);
        boardPhotoRepository.save(photo2);

        // when
        List<BoardPhoto> result = boardPhotoRepository.findByBoard_BoardIdOrderByCreatedAtAsc(board.getBoardId());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUrl()).isEqualTo("https://img.com/first.jpg");
        assertThat(result.get(1).getUrl()).isEqualTo("https://img.com/second.jpg");
    }
}