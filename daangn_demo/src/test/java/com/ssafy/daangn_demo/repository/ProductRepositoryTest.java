package com.ssafy.daangn_demo.repository;

import com.ssafy.daangn_demo.entity.ProductEntity;
import com.ssafy.daangn_demo.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 상품등록() {
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
        productRepository.save(product1);

        //when
        ProductEntity result = productRepository.findById(product1.getId()).get();

        //then
        assertThat(result).isEqualTo(product1);
    }

    @Test
    void 상품목록조회() {
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
        productRepository.save(product1);

        ProductEntity product2 = new ProductEntity();
        product2.setWriter(user);
        product2.setTitle("제목2");
        product2.setDescription("내용2");
        product2.setPrice(10000);
        product2.setStatus("on_sale");
        productRepository.save(product2);

        ProductEntity product3 = new ProductEntity();
        product3.setWriter(user);
        product3.setTitle("제목3");
        product3.setDescription("내용3");
        product3.setPrice(10000);
        product3.setStatus("on_sale");
        productRepository.save(product3);

        //when
        List<ProductEntity> list = productRepository.findAllByWriterId(user.getId());

        //then
        assertThat(list.size()).isEqualTo(3);
    }
}