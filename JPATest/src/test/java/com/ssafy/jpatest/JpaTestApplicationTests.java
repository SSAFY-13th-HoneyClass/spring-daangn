package com.ssafy.jpatest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@SpringBootTest
@Transactional
class JpaTestApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    void test(){
        //give
        User user = new User();
        user.setUserId("ssafy1");
        user.setPassword("1234");
        user.setName("홍길동");
        user.setNickname("ssafy");
        userRepository.save(user);

        User user2 = new User();
        user2.setUserId("taeho");
        user2.setPassword("0000");
        user2.setName("이태호");
        user2.setNickname("Taeho");
        userRepository.save(user2);

        Board board = new Board();
        board.setBoardId(1L);
        board.setSeller(user);
        board.setTitle("제목1");
        board.setPrice(10000);
        board.setRegistTime(new Timestamp(System.currentTimeMillis()));
        boardRepository.save(board);

        Board board2 = new Board();
        board2.setBoardId(2L);
        board2.setSeller(user);
        board2.setTitle("제목2");
        board2.setPrice(20000);
        board2.setRegistTime(new Timestamp(System.currentTimeMillis()));
        boardRepository.save(board2);

        Board board3 = new Board();
        board3.setBoardId(3L);
        board3.setSeller(user2);
        board3.setTitle("제목3");
        board3.setPrice(30000);
        board3.setRegistTime(new Timestamp(System.currentTimeMillis()));
        boardRepository.save(board3);



        //when
        List<Board> boards = boardRepository.findAll();
        List<User> users = userRepository.findAll();

        List<Board> boardsBySeller = boardRepository.findBySeller(user);
        List<Board> boardsByPrice = boardRepository.findByPriceGreaterThanEqual(19999);


        //then
        System.out.println();
        System.out.println("=================== Board 전체 조회 =========================");
        boards.forEach(System.out::println);

        System.out.println();
        System.out.println("=================== User 전체 조회 =========================");
        users.forEach(System.out::println);


        System.out.println();
        System.out.println("=================== Seller가 홍길동인 게시글 전체 조회 =========================");
        boardsBySeller.forEach(System.out::println);

        System.out.println();
        System.out.println("=================== 가격이 19999 이상인 게시글 전체 조회 =========================");
        boardsByPrice.forEach(System.out::println);
    }


}
