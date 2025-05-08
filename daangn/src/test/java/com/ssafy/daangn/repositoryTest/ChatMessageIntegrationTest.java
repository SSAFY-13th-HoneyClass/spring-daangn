package com.ssafy.daangn.repositoryTest;

import com.ssafy.daangn.domain.*;
import com.ssafy.daangn.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatMessageIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired private SaleRepository saleRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SaleStatusRepository saleStatusRepository;
    @Autowired private SaleImageRepository saleImageRepository;
    @Autowired private SaleLikeRepository saleLikeRepository;
    @Autowired private SearchRepository searchRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;

    private User seller;
    private User buyer;
    private ChatRoom chatRoom;

    @BeforeAll
    void setUp() {
        // 1. 판매자, 구매자 저장
        seller = userRepository.save(User.builder()
                .name("판매자")
                .id("seller123")
                .password("pw1")
                .email("seller@example.com")
                .phone("010-1111-1111")
                .address("서울")
                .addressDetail("101호")
                .nickname("셀러")
                .temperature(36.5)
                .latitude(37.1)
                .longitude(127.1)
                .build());

        buyer = userRepository.save(User.builder()
                .name("구매자")
                .id("buyer123")
                .password("pw2")
                .email("buyer@example.com")
                .phone("010-2222-2222")
                .address("부산")
                .addressDetail("202호")
                .nickname("바이어")
                .temperature(36.5)
                .latitude(36.5)
                .longitude(128.0)
                .build());

        Category category = categoryRepository.save(new Category("ELECTRONICS", "전자기기"));
        SaleStatus status = saleStatusRepository.save(new SaleStatus("ON_SALE", "판매중"));

        Sale sale = saleRepository.save(Sale.builder()
                .user(seller)
                .category(category)
                .status(status)
                .title("아이패드 팝니다")
                .content("거의 새거에요")
                .price(500000L)
                .thumbnail("https://example.com/ipad.jpg")
                .isPriceSuggestible(true)
                .address("서울 강남")
                .addressDetail("303호")
                .latitude(37.55)
                .longitude(127.01)
                .build());

        chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .sale(sale)
                .seller(seller)
                .buyer(buyer)
                .build());

        // 채팅 메시지 추가 (보낸 순서 보장 위함)
        chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(chatRoom)
                .writer(seller)
                .message("안녕하세요. 아직 판매중인가요?")
                .isRead(false)
                .build());

        chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(chatRoom)
                .writer(buyer)
                .message("네 아직 판매 중입니다.")
                .isRead(false)
                .build());

        chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(chatRoom)
                .writer(seller)
                .message("직거래 가능할까요?")
                .isRead(false)
                .build());
    }

    @Test
    @DisplayName("사용자를 ID로 조회하면 이름과 닉네임이 일치해야 한다")
    void 사용자를_아이디로_조회하면_저장된_정보와_일치한다() {
        // GIVEN
        Long userNo = seller.getNo();

        // WHEN
        User found = userRepository.findByNo(userNo).orElseThrow();

        // THEN
        assertEquals("판매자", found.getName());
        assertEquals("셀러", found.getNickname());
    }

    @Test
    @DisplayName("채팅방의 메시지를 보낸 순서대로 조회할 수 있다")
    void 채팅방의_메시지를_보낸_순서대로_조회할_수_있다() {
        // GIVEN
        Long chatRoomNo = chatRoom.getNo();

        // WHEN
        List<ChatMessage> messages = chatMessageRepository
                .findTop100ByChatRoomNoOrderByCreatedAtAsc(chatRoomNo);

        // THEN
        assertEquals(3, messages.size());
        assertEquals("안녕하세요. 아직 판매중인가요?", messages.get(0).getMessage());
        assertEquals("네 아직 판매 중입니다.", messages.get(1).getMessage());
        assertEquals("직거래 가능할까요?", messages.get(2).getMessage());
    }

}
