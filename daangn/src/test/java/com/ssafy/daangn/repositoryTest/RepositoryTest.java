package com.ssafy.daangn.repositoryTest;

import com.ssafy.daangn.domain.*;
import com.ssafy.daangn.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SaleRepository saleRepository;
    @Autowired private SaleLikeRepository saleLikeRepository;
    @Autowired private SaleStatusRepository saleStatusRepository;
    @Autowired private SaleImageRepository saleImageRepository;
    @Autowired private SearchRepository searchRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;

//    private User savedUser;
//    private Sale savedSale;

//    @BeforeEach
    @Test
    void saveTest() {
        if (userRepository.count() == 0) {
            // 1. 유저 저장
            User savedUser = userRepository.save(User.builder()
                    .name("테스트 유저")
                    .id("test123")
                    .password("password")
                    .email("test@example.com")
                    .phone("010-0000-0000")
                    .address("서울시 어딘가")
                    .addressDetail("302호")
                    .nickname("testnick")
                    .temperature(36.5)
                    .latitude(37.12345)
                    .longitude(127.12345)
                    .build());

            // 2. 카테고리 & 상태 저장
            Category category = categoryRepository.save(Category.builder()
                    .name("ELECTRONICS")
                    .description("전자기기")
                    .build());

            SaleStatus status = saleStatusRepository.save(SaleStatus.builder()
                    .name("ON_SALE")
                    .description("판매중")
                    .build());

            // 3. 판매글 저장
            Sale savedSale = saleRepository.save(Sale.builder()
                    .user(savedUser)
                    .category(category)
                    .status(status)
                    .title("맥북 프로 팝니다")
                    .content("2021년형 M1 맥북 프로 거의 새것입니다.")
                    .price(1500000L)
                    .discount(0.1)
                    .isPriceSuggestible(true)
                    .thumbnail("https://image.example.com/macbook.jpg")
                    .viewCount(0)
                    .likeCount(0)
                    .chatCount(0)
                    .address("서울시 강남구")
                    .addressDetail("101동 1001호")
                    .latitude(37.51)
                    .longitude(127.03)
                    .build());

            // 4. 이미지 저장
            saleImageRepository.save(SaleImage.builder()
                    .sale(savedSale)
                    .imageUrl("https://image.example.com/book2.jpg")
                    .build());

            // 5. 좋아요 저장
            saleLikeRepository.save(SaleLike.builder()
                    .user(savedUser)
                    .sale(savedSale)
                    .build());

            // 6. 검색어 저장
            searchRepository.save(Search.builder()
                    .user(savedUser)
                    .keyword("맥북")
                    .build());

            // 7. 채팅방 저장
            ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                    .sale(savedSale)
                    .seller(savedUser)
                    .buyer(savedUser)  // 여기선 같은 유저를 구매자/판매자로 사용
                    .build());


            System.out.println("🟢 초기 세팅 완료. 채팅방 ID: " + chatRoom.getNo());

            ChatMessage message = ChatMessage.builder()
                    .message("안녕하세요! 채팅 메시지 테스트입니다.")
                    .isRead(false)
                    .writer(savedUser)
                    .chatRoom(chatRoom)
                    .build();

            ChatMessage saved = chatMessageRepository.save(message);

            System.out.println("💬 저장된 채팅 메시지 ID: " + saved.getNo());


            //        } else {
//            savedUser = userRepository.findById(1L).orElseThrow();
//            savedSale = saleRepository.findById(1L).orElseThrow();
        }
    }

//    @Test
//    @Order(1)
//    void testSaleLike_duplicate_shouldFail() {
//        // 중복 좋아요 시도 → 예외 기대
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            saleLikeRepository.save(SaleLike.builder()
//                    .user(savedUser)
//                    .sale(savedSale)
//                    .build());
//        });
//    }
}


