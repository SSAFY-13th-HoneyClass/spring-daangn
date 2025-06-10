package com.ssafy.springdaangn;

import com.ssafy.springdaangn.Domain.Address;
import com.ssafy.springdaangn.Domain.Post;
import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Repository.AddressRepository;
import com.ssafy.springdaangn.Repository.PostRepository;
import com.ssafy.springdaangn.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    private User user;
    private Address address;

    @BeforeEach
    void setUp() {
        // given: 회원과 주소 먼저 저장
        user = User.builder()
                .id("testuser")
                .password("pass1234")
                .nickname("Tester")
                .build();
        user = userRepository.save(user);

        address = Address.builder()
                .user(user)
                .neighborhood("역삼동")
                .build();
        address = addressRepository.save(address);
    }

    @Test
    void Test_findAll() {

        Post p1 = Post.builder()
                .seller(user)
                .location(address)
                .status("SELLING")
                .title("상품1")
                .description("설명1")
                .categoryId("cat1")
                .price(100)
                .views(0)
                .likeCount(0)
                .chatRoomCount(0)
                .build();

        Post p2 = Post.builder()
                .seller(user)
                .location(address)
                .status("SELLING")
                .title("상품2")
                .description("설명2")
                .categoryId("cat2")
                .price(200)
                .views(0)
                .likeCount(0)
                .chatRoomCount(0)
                .build();

        Post p3 = Post.builder()
                .seller(user)
                .location(address)
                .status("SELLING")
                .title("상품3")
                .description("설명3")
                .categoryId("cat3")
                .price(300)
                .views(0)
                .likeCount(0)
                .chatRoomCount(0)
                .build();

        postRepository.save(p1);
        postRepository.save(p2);
        postRepository.save(p3);

        // when
        List<Post> posts = postRepository.findAll();

        //then
        assertThat(posts).hasSize(3);
        assertThat(posts.get(0).getTitle()).isEqualTo("상품1");
        assertThat(posts.get(1).getTitle()).isEqualTo("상품2");
        assertThat(posts.get(2).getTitle()).isEqualTo("상품3");
    }
}