package com.ssafy.daangn.repositoryTest;

import com.ssafy.daangn.domain.Category;
import com.ssafy.daangn.domain.Sale;
import com.ssafy.daangn.domain.SaleStatus;
import com.ssafy.daangn.domain.User;
import com.ssafy.daangn.repository.CategoryRepository;
import com.ssafy.daangn.repository.SaleRepository;
import com.ssafy.daangn.repository.SaleStatusRepository;
import com.ssafy.daangn.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 이거 중요!
public class Nplus1Test {

    @Autowired UserRepository userRepository;
    @Autowired SaleRepository saleRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired SaleStatusRepository saleStatusRepository;


    @BeforeAll
    public void setUpBeforeClass() throws Exception {
        for (int i = 1; i <= 10; i++) {

            User user = userRepository.save(User.builder()
                    .name("이름" + i)
                    .id("user" + i)
                    .password("password")
                    .email("test" + i + "@example.com")
                    .phone("010-0000-0000")
                    .address("서울시 어딘가")
                    .addressDetail("302호")
                    .nickname("test" + i + "nick")
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
            Sale sale = saleRepository.save(Sale.builder()
                    .user(user)
                    .category(category)
                    .status(status)
                    .title("제목" + i)
                    .content("2021년형 M1 맥북 프로 거의 새것입니다.")
                    .price(1000L * i)
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

            saleRepository.save(sale);
        }
    }


    @Test
    @Transactional
    public void problem() {
        List<Sale> sales = saleRepository.findAll();
        System.out.println("sales : " + sales);
        for (Sale sale : sales) {
            System.out.println(sale.getUser().getNickname()); // ← 여기서 추가 쿼리 N번 발생
        }
    }

    @Test
    @Transactional
    public void solution() {
        List<Sale> sales = saleRepository.findAllWithUserCategoryStatus();
        for (Sale sale : sales) {
            System.out.println(sale.getUser().getNickname());
        }
    }



}
