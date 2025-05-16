package com.ssafy.daangn_demo.service;

import com.ssafy.daangn_demo.entity.ProductEntity;
import com.ssafy.daangn_demo.entity.UserEntity;
import com.ssafy.daangn_demo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    void USERID로_회원이_작성한_판매물품_가져오기() {
        //given
        UserEntity user = new UserEntity();
        user.setEmail("abcd@gmail.com");
        user.setPhoneNumber("010-1234-5678");
        user.setMannerTemperature(36.7);
        userRepository.save(user);

        ProductEntity product1 = new ProductEntity();
        product1.setWriter(user);
        product1.setTitle("제목1");
        product1.setDescription("내용1");
        product1.setPrice(10000);
        product1.setStatus("on_sale");
        productService.create(product1);

        ProductEntity product2 = new ProductEntity();
        product2.setWriter(user);
        product2.setTitle("제목2");
        product2.setDescription("내용2");
        product2.setPrice(10000);
        product2.setStatus("on_sale");
        productService.create(product2);

        ProductEntity product3 = new ProductEntity();
        product3.setWriter(user);
        product3.setTitle("제목3");
        product3.setDescription("내용3");
        product3.setPrice(10000);
        product3.setStatus("on_sale");
        productService.create(product3);

        //when
        List<ProductEntity> products = productService.getByWriter(user.getId());

        //then
        assertThat(products.size()).isEqualTo(3);
    }

    @Test
    void 판매물품_전체목록의_작성자_조회하기(){
        //given

        UserEntity user1 = new UserEntity();
        user1.setEmail("abcd1@gmail.com");
        user1.setPhoneNumber("010-1234-5678");
        user1.setMannerTemperature(36.7);
        userRepository.save(user1);

        UserEntity user2 = new UserEntity();
        user2.setEmail("abcd2@gmail.com");
        user2.setPhoneNumber("010-1234-5678");
        user2.setMannerTemperature(36.8);
        userRepository.save(user2);

        UserEntity user3 = new UserEntity();
        user3.setEmail("abcd3@gmail.com");
        user3.setPhoneNumber("010-1234-5678");
        user3.setMannerTemperature(36.9);
        userRepository.save(user3);

        ProductEntity product1 = new ProductEntity();
        product1.setWriter(user1);
        product1.setTitle("제목1");
        product1.setDescription("내용1");
        product1.setPrice(10000);
        product1.setStatus("on_sale");
        productService.create(product1);

        ProductEntity product2 = new ProductEntity();
        product2.setWriter(user2);
        product2.setTitle("제목2");
        product2.setDescription("내용2");
        product2.setPrice(10000);
        product2.setStatus("on_sale");
        productService.create(product2);

        ProductEntity product3 = new ProductEntity();
        product3.setWriter(user3);
        product3.setTitle("제목3");
        product3.setDescription("내용3");
        product3.setPrice(10000);
        product3.setStatus("on_sale");
        productService.create(product3);

        em.flush();
        em.clear();

        //when
        List<ProductEntity> products = productService.getAll();

        //then
        System.out.println("==============start===============");
        for (ProductEntity product: products) {
            System.out.println(product.getWriter().getEmail());
        }
        System.out.println("==============end===============");
    }

    @Test
    void 판매물품_전체목록의_작성자_조회_NPlus1_방지() {
        // given
        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");
        user1.setPhoneNumber("010-1111-1111");
        user1.setMannerTemperature(36.5);
        userRepository.save(user1);

        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@example.com");
        user2.setPhoneNumber("010-2222-2222");
        user2.setMannerTemperature(36.6);
        userRepository.save(user2);

        ProductEntity p1 = new ProductEntity();
        p1.setTitle("상품1");
        p1.setWriter(user1);
        p1.setStatus("on_sale");
        p1.setPrice(10000);
        productService.create(p1);

        ProductEntity p2 = new ProductEntity();
        p2.setTitle("상품2");
        p2.setWriter(user2);
        p2.setStatus("on_sale");
        p2.setPrice(20000);
        productService.create(p2);

        em.flush();
        em.clear(); // 영속성 컨텍스트 초기화 → 프록시 강제 로딩 테스트 가능

        // when
        List<ProductEntity> products = productService.getAll(); // @EntityGraph 적용된 메서드

        // then
        for (ProductEntity p : products) {
            System.out.println(p.getWriter().getEmail()); // 추가 쿼리 없이 출력되어야 함
        }
    }
}