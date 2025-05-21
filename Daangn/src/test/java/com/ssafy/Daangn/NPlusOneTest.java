package com.ssafy.Daangn;

import com.ssafy.Daangn.Domain.Board;
import com.ssafy.Daangn.Domain.Location;
import com.ssafy.Daangn.Domain.User;
import com.ssafy.Daangn.Repository.BoardRepository;
import com.ssafy.Daangn.Repository.LocationRepository;
import com.ssafy.Daangn.Repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@SpringBootTest
@Transactional
public class NPlusOneTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setup() {

        Location location = new Location();
        location.setLocationName("서울");
        locationRepository.save(location);

        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setUserId("user" + i);
            user.setPassword("password" + i);
            user.setName("사용자" + i);
            user.setNickname("닉네임" + i);
            user.setDegree(36.5 + (i * 0.1));
            user.setLocation(location);
            userRepository.save(user);

            // 각 사용자마다 3개의 게시글 생성
            for (int j = 1; j <= 3; j++) {
                Board board = new Board();
                board.setSeller(user);
                board.setTitle("게시글 " + i + "-" + j);
                board.setPrice(10000 * i + 1000 * j);
                board.setRegistTime(new Timestamp(System.currentTimeMillis()));
                board.setCategory("카테고리" + j);
                board.setLocation(location);
                boardRepository.save(board);
            }
        }
    }

    @Test
    @Rollback
    void testNPlusOneProblem() {
        System.out.println("\n==========N+1 문제 테스트 시작===========\n");
        int count = 0;

        entityManager.clear();
        entityManager.flush();

        // 1. 일반 findAll() 실행 - N+1 문제 발생
        System.out.println("===== 일반 findAll() 사용 (N+1 문제 발생) =====");


        // 조회 쿼리 실행
        List<Board> boards = boardRepository.findAll();
        System.out.println("Board 조회 완료. 개수: " + boards.size());
        System.out.println("쿼리 수 : " + count);

        // Seller 접근 (지연 로딩 발생)
        System.out.println("\n----- Seller 정보 접근 시작 -----");
        for (Board board : boards) {
            count++;
            System.out.println("게시글 ID: " + board.getBoardId() + ", 판매자: " + board.getSeller().getName());
        }
        System.out.println("----- Seller 정보 접근 완료 -----\n");


        System.out.println("=====================================================================================");

        entityManager.clear();
        entityManager.flush();

        count = 0;

        // 2. EntityGraph 사용 - N+1 문제 해결
        System.out.println("\n===== EntityGraph 사용 (N+1 문제 해결) =====");


        // EntityGraph를 사용한 조회 쿼리 실행
        List<Board> optimizedBoards = boardRepository.findAllWithSellerAndLocation();
        System.out.println("Board와 Seller 조회 완료. 개수: " + optimizedBoards.size());
        System.out.println("쿼리 수 : " + count);

        // Seller 접근 (지연 로딩 발생하지 않음)
        System.out.println("\n----- Seller 정보 접근 시작 -----");
        for (Board board : optimizedBoards) {
            count++;
            System.out.println("게시글 ID: " + board.getBoardId() + ", 판매자: " + board.getSeller().getName());
        }
        System.out.println("----- Seller 정보 접근 완료 -----\n");
        System.out.println("쿼리 수 : " + count);


    }
}