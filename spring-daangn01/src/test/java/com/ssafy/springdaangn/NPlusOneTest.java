package com.ssafy.springdaangn;

import com.ssafy.springdaangn.Domain.Post;
import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Repository.PostRepository;
import com.ssafy.springdaangn.Repository.UserRepository;
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
    private EntityManager em;

    @BeforeEach
    void setUp() {
        // 5명의 사용자 생성
        for (int i = 1; i <= 5; i++) {
            User u = new User();
            u.setId("user" + i);
            u.setPassword("pw" + i);
            u.setNickname("nick" + i);
            userRepository.save(u);

            // 각 사용자당 3개의 게시글 생성
            for (int j = 1; j <= 3; j++) {
                Post p = new Post();
                p.setSeller(u);
                p.setTitle("게시글 " + i + "-" + j);
                p.setPrice(1000 * i + j);
                p.setStatus("AVAILABLE");
                p.setCategoryId("cat" + j);
                //p.setNeighborhood(0L);
                p.setDescription("내용 " + i + "-" + j);
                postRepository.save(p);
            }
        }
    }

    @Test
    void testNPlusOneAndFetchJoin() {
        System.out.println(">>> plain findAll (expect N+1 queries) <<<");
//        em.flush();
//        em.clear();

        List<Post> list1 = postRepository.findAll();
        list1.forEach(p ->
                System.out.println(p.getTitle() + " by " + p.getSeller().getNickname())
        );

        System.out.println("\n>>> findAllWithSeller<<<");
        em.clear();



        List<Post> list2 = postRepository.findAllWithSeller();
        list2.forEach(p ->
                System.out.println(p.getTitle() + " by " + p.getSeller().getNickname())
        );
    }
}
