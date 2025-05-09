package org.example.springboot.repository;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ChattingRoomRepositoryTest {

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User seller;
    private User buyer1;
    private User buyer2;
    private Post post1;
    private Post post2;

    @BeforeEach
    public void setup() {
        // 테스트 사용자 생성 (판매자) - Builder 사용
        seller = User.builder()
                .email("seller@example.com")
                .password("password")
                .phone("010-1111-1111")
                .name("판매자")
                .profile("안녕하세요 판매자입니다.")
                .nickname("판매자닉네임")
                .profileImgPath("/images/seller.jpg")
                .role("USER")
                .build();
        userRepository.save(seller);

        // 테스트 사용자 생성 (구매자1) - Builder 사용
        buyer1 = User.builder()
                .email("buyer1@example.com")
                .password("password")
                .phone("010-2222-2222")
                .name("구매자1")
                .profile("안녕하세요 구매자1입니다.")
                .nickname("구매자1닉네임")
                .profileImgPath("/images/buyer1.jpg")
                .role("USER")
                .build();
        userRepository.save(buyer1);

        // 테스트 사용자 생성 (구매자2) - Builder 사용
        buyer2 = User.builder()
                .email("buyer2@example.com")
                .password("password")
                .phone("010-3333-3333")
                .name("구매자2")
                .profile("안녕하세요 구매자2입니다.")
                .nickname("구매자2닉네임")
                .profileImgPath("/images/buyer2.jpg")
                .role("USER")
                .build();
        userRepository.save(buyer2);

        // 테스트 게시물 생성 - Builder 사용
        post1 = Post.builder()
                .user(seller)
                .title("첫 번째 판매 게시물")
                .content("첫 번째 판매 게시물 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post1);

        post2 = Post.builder()
                .user(seller)
                .title("두 번째 판매 게시물")
                .content("두 번째 판매 게시물 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post2);
    }

    @Test
    @DisplayName("채팅방 등록 및 조회 테스트")
    public void createAndFindChattingRooms() {
        // 채팅방 생성 - buyer1이 post1에 대해 문의 - Builder 사용
        ChattingRoom chatRoom1 = ChattingRoom.builder()
                .user(buyer1)
                .postId(post1.getPostId())
                .build();
        chattingRoomRepository.save(chatRoom1);

        // 채팅방 생성 - buyer2가 post1에 대해 문의 - Builder 사용
        ChattingRoom chatRoom2 = ChattingRoom.builder()
                .user(buyer2)
                .postId(post1.getPostId())
                .build();
        chattingRoomRepository.save(chatRoom2);

        // 채팅방 생성 - buyer1이 post2에 대해 문의 - Builder 사용
        ChattingRoom chatRoom3 = ChattingRoom.builder()
                .user(buyer1)
                .postId(post2.getPostId())
                .build();
        chattingRoomRepository.save(chatRoom3);

        // 전체 채팅방 조회 테스트
        List<ChattingRoom> allRooms = chattingRoomRepository.findAll();
        assertThat(allRooms).hasSize(3);

        // ID로 채팅방 조회 테스트
        ChattingRoom foundRoom = chattingRoomRepository.findById(chatRoom1.getChattingRoomId()).orElse(null);
        assertThat(foundRoom).isNotNull();
        assertThat(foundRoom.getUser().getEmail()).isEqualTo("buyer1@example.com");

        // 사용자별 채팅방 조회 테스트
        List<ChattingRoom> buyer1Rooms = chattingRoomRepository.findByUser(buyer1);
        assertThat(buyer1Rooms).hasSize(2);

        List<ChattingRoom> buyer2Rooms = chattingRoomRepository.findByUser(buyer2);
        assertThat(buyer2Rooms).hasSize(1);

        // 게시물별 채팅방 조회 테스트
        List<ChattingRoom> post1Rooms = chattingRoomRepository.findByPostId(post1.getPostId());
        assertThat(post1Rooms).hasSize(2);

        List<ChattingRoom> post2Rooms = chattingRoomRepository.findByPostId(post2.getPostId());
        assertThat(post2Rooms).hasSize(1);

        // 사용자+게시물로 채팅방 조회 테스트
        Optional<ChattingRoom> buyer1Post1Room = chattingRoomRepository.findByUserAndPostId(buyer1, post1.getPostId());
        assertThat(buyer1Post1Room).isPresent();
        assertThat(buyer1Post1Room.get().getChattingRoomId()).isEqualTo(chatRoom1.getChattingRoomId());

        // 판매자의 게시물에 대한 모든 채팅방 조회 테스트
        List<ChattingRoom> sellerRooms = chattingRoomRepository.findByPostOwner(seller);
        assertThat(sellerRooms).hasSize(3);
    }
}