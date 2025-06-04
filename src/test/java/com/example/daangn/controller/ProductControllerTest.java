package com.example.daangn.controller;

import com.example.daangn.domain.location.entity.Location;
import com.example.daangn.domain.location.repository.LocationRepository;
import com.example.daangn.domain.product.dto.ProductRequestDto;
import com.example.daangn.domain.product.entity.Product;
import com.example.daangn.domain.product.repository.ProductRepository;
import com.example.daangn.domain.user.entity.User;
import com.example.daangn.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductController 통합 테스트
 * 상품 관련 API의 전체 동작을 검증합니다.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testUser;
    private Location testLocation;

    /**
     * 각 테스트 실행 전 MockMvc 및 테스트 데이터 설정
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 테스트용 사용자 생성
        testUser = User.builder()
                .id("testuser")
                .password("password")
                .name("테스트 사용자")
                .nickname("테스터")
                .phone("01012345678")
                .manner(new BigDecimal("36.5"))
                .role("USER")
                .created(LocalDateTime.now())
                .lastest(LocalDateTime.now())
                .build();
        testUser = userRepository.save(testUser);

        // 테스트용 위치 생성
        testLocation = Location.builder()
                .si("서울시")
                .gugun("강남구")
                .location("역삼동")
                .lat(new BigDecimal("37.5665"))
                .lng(new BigDecimal("126.9780"))
                .build();
        testLocation = locationRepository.save(testLocation);
    }

    /**
     * 상품 생성 API 테스트 (성공)
     */
    @Test
    @DisplayName("상품 생성 성공 테스트")
    void createProduct_Success() throws Exception {
        // given
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .locationId(testLocation.getLuid())
                .userId(testUser.getUuid())
                .title("아이폰 15 팝니다")
                .category("디지털기기")
                .content("깨끗한 상태의 아이폰 15입니다.")
                .dealType("판매하기")
                .price(1000000)
                .isSell(false)
                .build();

        // when & then
        mockMvc.perform(post("/api/products/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("아이폰 15 팝니다"))
                .andExpect(jsonPath("$.category").value("디지털기기"))
                .andExpect(jsonPath("$.price").value(1000000));
    }

    /**
     * 상품 생성 API 테스트 (존재하지 않는 사용자)
     */
    @Test
    @DisplayName("존재하지 않는 사용자로 상품 생성 실패 테스트")
    void createProduct_UserNotFound() throws Exception {
        // given
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .locationId(testLocation.getLuid())
                .userId(999L)
                .title("테스트 상품")
                .category("기타")
                .content("테스트 내용")
                .build();

        // when & then
        mockMvc.perform(post("/api/products/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 상품 생성 API 테스트 (존재하지 않는 위치)
     */
    @Test
    @DisplayName("존재하지 않는 위치로 상품 생성 실패 테스트")
    void createProduct_LocationNotFound() throws Exception {
        // given
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .locationId(999L)
                .userId(testUser.getUuid())
                .title("테스트 상품")
                .category("기타")
                .content("테스트 내용")
                .build();

        // when & then
        mockMvc.perform(post("/api/products/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 모든 상품 조회 API 테스트
     */
    @Test
    @DisplayName("모든 상품 조회 테스트")
    void getAllProducts_Success() throws Exception {
        // given
        Product product1 = Product.builder()
                .location(testLocation)
                .user(testUser)
                .title("상품1")
                .category("카테고리1")
                .content("내용1")
                .dealType("판매하기")
                .price(10000)
                .created(LocalDateTime.now())
                .views(0)
                .isSell(false)
                .build();

        Product product2 = Product.builder()
                .location(testLocation)
                .user(testUser)
                .title("상품2")
                .category("카테고리2")
                .content("내용2")
                .dealType("판매하기")
                .price(20000)
                .created(LocalDateTime.now())
                .views(0)
                .isSell(false)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        // when & then
        mockMvc.perform(get("/api/products/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("상품1"))
                .andExpect(jsonPath("$[1].title").value("상품2"));
    }

    /**
     * 특정 상품 조회 API 테스트 (성공)
     */
    @Test
    @DisplayName("특정 상품 조회 성공 테스트")
    void getProductById_Success() throws Exception {
        // given
        Product product = Product.builder()
                .location(testLocation)
                .user(testUser)
                .title("테스트 상품")
                .category("테스트 카테고리")
                .content("테스트 내용")
                .dealType("판매하기")
                .price(50000)
                .created(LocalDateTime.now())
                .views(0)
                .isSell(false)
                .build();
        Product savedProduct = productRepository.save(product);

        // when & then
        mockMvc.perform(get("/api/products/{id}/", savedProduct.getPuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 상품"))
                .andExpect(jsonPath("$.category").value("테스트 카테고리"));
    }

    /**
     * 특정 상품 조회 API 테스트 (실패)
     */
    @Test
    @DisplayName("존재하지 않는 상품 조회 실패 테스트")
    void getProductById_NotFound() throws Exception {
        // given
        Long nonExistentId = 999L;

        // when & then
        mockMvc.perform(get("/api/products/{id}/", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * 상품 삭제 API 테스트 (성공)
     */
    @Test
    @DisplayName("상품 삭제 성공 테스트")
    void deleteProduct_Success() throws Exception {
        // given
        Product product = Product.builder()
                .location(testLocation)
                .user(testUser)
                .title("삭제될 상품")
                .category("테스트")
                .content("삭제 테스트")
                .dealType("판매하기")
                .price(30000)
                .created(LocalDateTime.now())
                .views(0)
                .isSell(false)
                .build();
        Product savedProduct = productRepository.save(product);

        // when & then
        mockMvc.perform(delete("/api/products/{id}/", savedProduct.getPuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 상품 삭제 API 테스트 (실패)
     */
    @Test
    @DisplayName("존재하지 않는 상품 삭제 실패 테스트")
    void deleteProduct_NotFound() throws Exception {
        // given
        Long nonExistentId = 999L;

        // when & then
        mockMvc.perform(delete("/api/products/{id}/", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}