package com.ssafy.daangn.serviceTest;

import com.ssafy.daangn.domain.*;
import com.ssafy.daangn.dto.ChatRoomDto;
import com.ssafy.daangn.repository.*;
import com.ssafy.daangn.service.ChatRoomService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatRoomServiceIntegrationTest {

    @Autowired private ChatRoomService chatRoomService;
    @Autowired private UserRepository userRepository;
    @Autowired private SaleRepository saleRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SaleStatusRepository saleStatusRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;

    private User seller;
    private User buyer;
    private Sale sale;
    private ChatRoomDto chatRoomDto;

    @BeforeEach
    void setUp() {
        seller = userRepository.save(User.builder()
                .id("seller" + System.nanoTime())  // 👈 여기를 동적으로
                .name("판매자")
                .nickname("셀러")
                .password("pw")
                .email("seller@example.com")
                .temperature(36.5)
                .build());

        buyer = userRepository.save(User.builder()
                .id("seller" + System.nanoTime())  // 👈 여기를 동적으로
                .name("구매자")
                .nickname("바이어")
                .password("pw")
                .email("buyer@example.com")
                .temperature(36.5)
                .build());

        Category category = categoryRepository.save(new Category("ELECTRONICS", "전자기기"));
        SaleStatus status = saleStatusRepository.save(new SaleStatus("ON_SALE", "판매중"));

        sale = saleRepository.save(Sale.builder()
                .user(seller)
                .category(category)
                .status(status)
                .title("노트북 팝니다")
                .price(1000000L)
                .thumbnail("url")
                .build());

        chatRoomDto = ChatRoomDto.builder()
                .saleNo(sale.getNo())
                .sellerNo(seller.getNo())
                .buyerNo(buyer.getNo())
                .build();
    }

    @Test
    @DisplayName("채팅방 저장 후 조회하면 값이 일치해야 한다")
    void testSaveChatRoom() {
        ChatRoom saved = chatRoomService.save(chatRoomDto);

        List<ChatRoom> found = chatRoomService.findBySaleNoAndBuyerNoOrderByUpdatedAtAsc(
                chatRoomDto.getSaleNo(), chatRoomDto.getBuyerNo());

        assertEquals(1, found.size());
        assertEquals(saved.getSeller().getNo(), seller.getNo());
    }

    @Test
    @DisplayName("채팅방 삭제 후 조회하면 존재하지 않아야 한다")
    void testDeleteChatRoom() {

        chatRoomService.delete(chatRoomDto);
        List<ChatRoom> found = chatRoomService.findBySaleNoAndBuyerNoOrderByUpdatedAtAsc(
                chatRoomDto.getSaleNo(), chatRoomDto.getBuyerNo());

        assertTrue(found.isEmpty());
    }
}
