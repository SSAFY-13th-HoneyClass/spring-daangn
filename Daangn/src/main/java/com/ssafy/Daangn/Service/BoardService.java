package com.ssafy.Daangn.Service;

import com.ssafy.Daangn.Domain.Board;
import com.ssafy.Daangn.Domain.Location;
import com.ssafy.Daangn.Repository.BoardRepository;
import com.ssafy.Daangn.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 전체 게시글 조회
    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }

    // 지역별 게시글 조회
    public List<Board> findBoardsByLocation(Location location) {
        return boardRepository.findByLocation(location);
    }

    // 카테고리별 게시글 조회
    public List<Board> findBoardsByCategory(String category) {
        return boardRepository.findByCategory(category);
    }

    // 제목 키워드로 게시글 검색
    public List<Board> findBoardsByTitle(String keyword) {
        return boardRepository.findByTitleContaining(keyword);
    }

    // 판매 상태별 게시글 조회
    public List<Board> findBoardsByIsSell(boolean isSell) {
        return boardRepository.findByIsSell(isSell);
    }

    // 게시글 저장
    public Board saveBoard(Board board) {
        // 현재 시간으로 등록 시간 설정
        if (board.getRegistTime() == null) {
            board.setRegistTime(new Timestamp(System.currentTimeMillis()));
        }

        // 초기 조회수, 좋아요 수 설정
        if (board.getView() == null) {
            board.setView(0);
        }

        if (board.getLikeCount() == null) {
            board.setLikeCount(0);
        }

        return boardRepository.save(board);
    }

    // 게시글 삭제
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }


    // 판매 상태 변경
    public void updateSellStatus(String title, boolean isSell) {
        Optional<Board> boardOptional = boardRepository.findBytitle(title);
        boardOptional.ifPresent(board -> {
            board.setIsSell(isSell);
            boardRepository.save(board);
        });
    }
}