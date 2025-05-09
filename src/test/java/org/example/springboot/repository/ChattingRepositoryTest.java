package org.example.springboot.repository;

import org.example.springboot.domain.Chatting;
import org.example.springboot.domain.ChattingRoom;
import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ChattingRepositoryTest {

    @Autowired
    private ChattingRepository chattingRepository;

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User seller;
    private User buyer;
    private Post post;
    private ChattingRoom chattingRoom;

    @BeforeEach
    public void setup() {
        // 테스트 사용자 생성 (판매자)
        seller = new User();
        seller.setEmail("seller@example.com");
        seller.setPassword("password");
        seller.setPhone("010-1111-1111");
        seller.setName("판매자");
        seller.setProfile("안녕하세요 판매자입니다.");
        seller.setNickname("판매자닉네임");
        seller.setProfileImgPath("/images/seller.jpg");
        seller.setRole("USER");
        userRepository.save(seller);

        // 테스트 사용자 생성 (구매자)
        buyer = new User();
        buyer.setEmail("buyer@example.com");
        buyer.setPassword("password");
        buyer.setPhone("010-2222-2222");
        buyer.setName("구매자");
        buyer.setProfile("안녕하세요 구매자입니다.");
        buyer.setNickname("구매자닉네임");
        buyer.setProfileImgPath("/images/buyer.jpg");
        buyer.setRole("USER");
        userRepository.save(buyer);

        // 테스트 게시물 생성
        post = new Post();
        post.setUser(seller);
        post.setTitle("판매 게시물");
        post.setContent("판매 게시물 내용입니다.");
        post.setStatus("판매중");
        postRepository.save(post);

        // 테스트 채팅방 생성
        chattingRoom = new ChattingRoom();
        chattingRoom.setUser(buyer);
        chattingRoom.setPostId(post.getPostId());
        chattingRoomRepository.save(chattingRoom);
    }

    @Test
    @DisplayName("채팅 메시지 등록 및 조회 테스트")
    public void createAndFindChattings() {
        // 구매자의 첫 메시지
        Chatting chatting1 = new Chatting();
        chatting1.setChattingRoom(chattingRoom);
        chatting1.setUser(buyer);
        chatting1.setContent("안녕하세요, 이 상품에 관심이 있습니다.");
        chattingRepository.save(chatting1);

        // 판매자의 답변
        Chatting chatting2 = new Chatting();
        chatting2.setChattingRoom(chattingRoom);
        chatting2.setUser(seller);
        chatting2.setContent("네, 안녕하세요. 어떤 점이 궁금하신가요?");
        chattingRepository.save(chatting2);

        // 구매자의 질문
        Chatting chatting3 = new Chatting();
        chatting3.setChattingRoom(chattingRoom);
        chatting3.setUser(buyer);
        chatting3.setContent("혹시 직거래 가능한가요?");
        chattingRepository.save(chatting3);

        // 판매자의 답변
        Chatting chatting4 = new Chatting();
        chatting4.setChattingRoom(chattingRoom);
        chatting4.setUser(seller);
        chatting4.setContent("네, 가능합니다. 어느 지역에 계신가요?");
        chattingRepository.save(chatting4);

        // 전체 채팅 메시지 조회 테스트
        List<Chatting> allChattings = chattingRepository.findAll();
        assertThat(allChattings).hasSize(4);

        // 채팅방별 메시지 조회 테스트
        List<Chatting> roomChattings = chattingRepository.findByChattingRoom(chattingRoom);
        assertThat(roomChattings).hasSize(4);

        // 채팅방 ID로 메시지 조회 및 정렬 테스트
        List<Chatting> sortedChattings = chattingRepository.findByChattingRoomIdOrderByCreatedAtAsc(chattingRoom.getChattingRoomId());
        assertThat(sortedChattings).hasSize(4);

        // 메시지 순서 확인
        assertThat(sortedChattings.get(0).getContent()).isEqualTo("안녕하세요, 이 상품에 관심이 있습니다.");
        assertThat(sortedChattings.get(1).getContent()).isEqualTo("네, 안녕하세요. 어떤 점이 궁금하신가요?");
        assertThat(sortedChattings.get(2).getContent()).isEqualTo("혹시 직거래 가능한가요?");
        assertThat(sortedChattings.get(3).getContent()).isEqualTo("네, 가능합니다. 어느 지역에 계신가요?");

        // 발신자 확인
        assertThat(sortedChattings.get(0).getUser().getEmail()).isEqualTo("buyer@example.com");
        assertThat(sortedChattings.get(1).getUser().getEmail()).isEqualTo("seller@example.com");
        assertThat(sortedChattings.get(2).getUser().getEmail()).isEqualTo("buyer@example.com");
        assertThat(sortedChattings.get(3).getUser().getEmail()).isEqualTo("seller@example.com");
    }
}