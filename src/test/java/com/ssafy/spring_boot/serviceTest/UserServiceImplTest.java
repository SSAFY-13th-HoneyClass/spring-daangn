package com.ssafy.spring_boot.serviceTest;

import com.ssafy.spring_boot.category.domain.Category;
import com.ssafy.spring_boot.chat.domain.ChatRoom;
import com.ssafy.spring_boot.chat.dto.ChatRoomDTO;
import com.ssafy.spring_boot.chat.repository.ChatRoomRepository;
import com.ssafy.spring_boot.favorite.domain.Favorite;
import com.ssafy.spring_boot.favorite.repository.FavoriteRepository;
import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.product.dto.ProductDTO;
import com.ssafy.spring_boot.product.repository.ProductRepository;
import com.ssafy.spring_boot.region.domain.Region;
import com.ssafy.spring_boot.user.domain.User;
import com.ssafy.spring_boot.user.dto.LoginInfoDTO;
import com.ssafy.spring_boot.user.dto.UserDTO;
import com.ssafy.spring_boot.user.repository.UserRepository;
import com.ssafy.spring_boot.user.service.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    public class UserServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private ProductRepository productRepository;

        @Mock
        private ChatRoomRepository chatRoomRepository;

        @Mock
        private FavoriteRepository favoriteRepository;

        @InjectMocks
        private UserServiceImpl userService;

        @Test
        @DisplayName("로그인 성공 테스트")
        void loginSuccessTest() {
            // given
            Region region = Region.builder().id(1).name("서울").build();
            User user = User.builder()
                    .id(1)
                    .nickname("테스트유저")
                    .email("test@example.com")
                    .password("password123")
                    .temperature(36.5)
                    .region(region)
                    .profileUrl("profile.jpg")
                    .build();

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            // when
            LoginInfoDTO result = userService.login("test@example.com", "password123");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getNickname()).isEqualTo("테스트유저");
            assertThat(result.getTemperature()).isEqualTo(36.5);
            assertThat(result.getRegionName()).isEqualTo("서울");

            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시 예외 발생 테스트")
        void loginFailEmailNotFoundTest() {
            // given
            when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

            // when & then
            assertThrows(EntityNotFoundException.class, () -> {
                userService.login("wrong@example.com", "password123");
            });
        }
        @Test
        @DisplayName("사용자가 등록한 상품 목록 조회 테스트")
        void getUserProductsTest() {
            // given
            Region region = Region.builder().id(1).name("서울").build();
            Category category = Category.builder().id(1).type("전자기기").build();
            User seller = User.builder()
                    .id(1)
                    .nickname("판매자")
                    .region(region)
                    .build();

            Product product1 = Product.builder()
                    .id(1L)
                    .title("아이폰")
                    .thumbnail("thumbnail1.jpg")
                    .description("아이폰 설명")
                    .price(1000000)
                    .createdAt(LocalDateTime.now())
                    .isReserved(false)
                    .isCompleted(false)
                    .isNegotiable(true)
                    .chatCount(0L)
                    .viewCount(0L)
                    .favoriteCount(0L)
                    .seller(seller)
                    .category(category)
                    .region(region)
                    .build();

            Product product2 = Product.builder()
                    .id(2L)
                    .title("맥북")
                    .thumbnail("thumbnail2.jpg")
                    .description("맥북 설명")
                    .price(2000000)
                    .createdAt(LocalDateTime.now())
                    .isReserved(false)
                    .isCompleted(false)
                    .isNegotiable(true)
                    .chatCount(0L)
                    .viewCount(0L)
                    .favoriteCount(0L)
                    .seller(seller)
                    .category(category)
                    .region(region)
                    .build();

            List<Product> products = Arrays.asList(product1, product2);

            when(productRepository.findAllBySeller_Id(1L)).thenReturn(products);

            // when
            List<ProductDTO> result = userService.getUserProducts(1L);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("아이폰");
            assertThat(result.get(1).getTitle()).isEqualTo("맥북");

            verify(productRepository).findAllBySeller_Id(1L);
        }

        @Test
        @DisplayName("사용자 관심 상품 ID 목록 조회 테스트")
        void getFavoriteProductIdsTest() {
            // given
            User user = User.builder().id(1).build();
            Product product1 = Product.builder().id(1L).build();
            Product product2 = Product.builder().id(2L).build();

            Favorite favorite1 = Favorite.builder()
                    .id(1L)
                    .user(user)
                    .product(product1)
                    .build();

            Favorite favorite2 = Favorite.builder()
                    .id(2L)
                    .user(user)
                    .product(product2)
                    .build();

            List<Favorite> favorites = Arrays.asList(favorite1, favorite2);

            when(favoriteRepository.findAllByUserId(1L)).thenReturn(favorites);

            // when
            List<Long> result = userService.getFavoriteProductIds(1L);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(1L, 2L);

            verify(favoriteRepository).findAllByUserId(1L);
        }
    }