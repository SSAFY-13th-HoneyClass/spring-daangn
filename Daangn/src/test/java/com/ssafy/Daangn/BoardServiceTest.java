package com.ssafy.Daangn;

import com.ssafy.Daangn.Domain.Board;
import com.ssafy.Daangn.Domain.Location;
import com.ssafy.Daangn.Domain.User;
import com.ssafy.Daangn.Repository.BoardRepository;
import com.ssafy.Daangn.Repository.LocationRepository;
import com.ssafy.Daangn.Repository.UserRepository;
import com.ssafy.Daangn.Service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    private User testUser;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = new Location();
        testLocation.setLocationName("울산");
        locationRepository.save(testLocation);

        testUser = new User();
        testUser.setUserId("abcde");
        testUser.setPassword("1234");
        testUser.setName("taeho");
        testUser.setNickname("taeho");
        testUser.setLocation(testLocation);
        testUser.setDegree(37.0);
        userRepository.save(testUser);

        Board Board1 = CreateBoard("게시글1", 10000, false);  // 판매중
        Board Board2 = CreateBoard("게시글2", 20000, false);  // 판매중

        Board Board3 = CreateBoard("게시글3", 30000, true); // 판매완료
    }

    private Board CreateBoard( String title, int price, boolean isSell) {
        Board board = new Board();
        board.setTitle(title);
        board.setPrice(price);
        board.setIsSell(isSell);
        return boardRepository.save(board);
    }

    @Test
    void testSell(){
        System.out.println("\n======== 판매 상태 변경 테스트 시작 ========\n");

        System.out.println("1. 초기 판매중 게시글 조회");
        List<Board> sellingBoards = boardService.findBoardsByIsSell(false);

        for (Board board : sellingBoards) {
            System.out.println(board.getBoardId() + " | " + board.getTitle() + " | " + board.getPrice() + " | " + board.getIsSell());
        }


        System.out.println("\n======== 판매 상태 변경 테스트 시작 ========\n");
        boardService.updateSellStatus("게시글2",true);
        System.out.println("2. 2번 게시글 판매 완료로 변경");
        System.out.println("========== 판매 중 게시글 =============");
        List<Board> NextBoards = boardService.findBoardsByIsSell(false);

        for (Board board : NextBoards) {
            System.out.println(board.getTitle() + " | " + board.getPrice() + " | " + board.getIsSell());
        }


        System.out.println("========== 판매 완료 게시글 =============");
        List<Board> NextBoards2 = boardService.findBoardsByIsSell(true);

        for (Board board : NextBoards2) {
            System.out.println(board.getTitle() + " | " + board.getPrice() + " | " + board.getIsSell());
        }




    }
}
