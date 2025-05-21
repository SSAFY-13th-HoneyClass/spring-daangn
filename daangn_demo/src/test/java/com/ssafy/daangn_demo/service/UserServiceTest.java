package com.ssafy.daangn_demo.service;

import com.ssafy.daangn_demo.entity.ProductEntity;
import com.ssafy.daangn_demo.entity.UserEntity;
import com.ssafy.daangn_demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void ID로_유저를_찾고_작성한_판매물품을_가져울_수_있다() {
        //given
        UserEntity user = new UserEntity();
        user.setEmail("abcd@gmail.com");
        user.setPhoneNumber("010-1234-5678");
        user.setMannerTemperature(36.7);
        userService.create(user);

        ProductEntity product1 = new ProductEntity();
        product1.setWriter(user);
        product1.setTitle("제목1");
        product1.setDescription("내용1");
        product1.setPrice(10000);
        product1.setStatus("on_sale");
        productRepository.save(product1);
        user.addProduct(product1);

        ProductEntity product2 = new ProductEntity();
        product2.setWriter(user);
        product2.setTitle("제목2");
        product2.setDescription("내용2");
        product2.setPrice(10000);
        product2.setStatus("on_sale");
        productRepository.save(product2);
        user.addProduct(product2);

        ProductEntity product3 = new ProductEntity();
        product3.setWriter(user);
        product3.setTitle("제목3");
        product3.setDescription("내용3");
        product3.setPrice(10000);
        product3.setStatus("on_sale");
        productRepository.save(product3);
        user.addProduct(product3);

        //when
        UserEntity user2 = userService.getById(user.getId());
        List<ProductEntity> products = user2.getProducts();

        //then
        assertThat(products.size()).isEqualTo(3);
    }
}