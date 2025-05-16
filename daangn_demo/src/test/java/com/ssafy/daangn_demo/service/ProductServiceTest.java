package com.ssafy.daangn_demo.service;

import com.ssafy.daangn_demo.entity.ProductEntity;
import com.ssafy.daangn_demo.entity.UserEntity;
import com.ssafy.daangn_demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

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
}