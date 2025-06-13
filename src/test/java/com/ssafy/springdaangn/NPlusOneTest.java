package com.ssafy.springdaangn;

import com.ssafy.springdaangn.domain.Address;
import com.ssafy.springdaangn.domain.Post;
import com.ssafy.springdaangn.domain.User;
import com.ssafy.springdaangn.repository.AddressRepository;
import com.ssafy.springdaangn.repository.PostRepository;
import com.ssafy.springdaangn.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class NPlusOneTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AddressRepository addressRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {
        // 5명의 사용자 생성
        for (int i = 1; i <= 5; i++) {
            User u = User.builder()
                    .id("user" + i)
                    .password("pw" + i)
                    .nickname("nick" + i)
                    .build();
            u = userRepository.save(u);

            // 각 사용자당 주소 생성
            Address address = Address.builder()
                    .user(u)
                    .neighborhood("동네" + i)
                    .build();
            address = addressRepository.save(address);

            // 각 사용자당 3개의 게시글 생성
            for (int j = 1; j <= 3; j++) {
                Post p = Post.builder()
                        .seller(u)
                        .location(address)
                        .title("게시글 " + i + "-" + j)
                        .price(1000 * i + j)
                        .status("AVAILABLE")
                        .categoryId("cat" + j)
                        .description("내용 " + i + "-" + j)
                        .views(0)
                        .likeCount(0)
                        .chatRoomCount(0)
                        .build();
                postRepository.save(p);
            }
        }

        // 영속성 컨텍스트 플러시 및 클리어
        em.flush();
        em.clear();
    }

    @Test
    void testNPlusOneAndFetchJoin() {
        System.out.println(">>> plain findAll (expect N+1 queries) <<<");

        List<Post> list1 = postRepository.findAll();
        list1.forEach(p ->
                System.out.println(p.getTitle() + " by " + p.getSeller().getNickname())
        );

        System.out.println("\n>>> findAllWithSeller (using fetch join) <<<");
        em.clear();

        List<Post> list2 = postRepository.findAllWithSeller();
        list2.forEach(p ->
                System.out.println(p.getTitle() + " by " + p.getSeller().getNickname())
        );
    }
}