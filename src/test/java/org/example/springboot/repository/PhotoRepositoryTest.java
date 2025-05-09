package org.example.springboot.repository;

import org.example.springboot.domain.Photo;
import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PhotoRepositoryTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private Post post1;
    private Post post2;

    @BeforeEach
    public void setup() {
        // 테스트 사용자 생성
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setPhone("010-1234-5678");
        user.setName("테스트사용자");
        user.setProfile("안녕하세요 테스트 사용자입니다.");
        user.setNickname("테스트닉네임");
        user.setProfileImgPath("/images/profile.jpg");
        user.setRole("USER");
        userRepository.save(user);

        // 테스트 게시물 생성
        post1 = new Post();
        post1.setUser(user);
        post1.setTitle("첫 번째 게시물");
        post1.setContent("첫 번째 게시물 내용입니다.");
        post1.setStatus("판매중");
        postRepository.save(post1);

        post2 = new Post();
        post2.setUser(user);
        post2.setTitle("두 번째 게시물");
        post2.setContent("두 번째 게시물 내용입니다.");
        post2.setStatus("판매중");
        postRepository.save(post2);
    }

    @Test
    @DisplayName("사진 등록 및 조회 테스트")
    public void createAndFindPhotos() {
        // 첫 번째 게시물 사진 생성
        Photo photo1 = new Photo();
        photo1.setPost(post1);
        photo1.setPath("/images/posts/post1_1.jpg");
        photoRepository.save(photo1);

        Photo photo2 = new Photo();
        photo2.setPost(post1);
        photo2.setPath("/images/posts/post1_2.jpg");
        photoRepository.save(photo2);

        // 두 번째 게시물 사진 생성
        Photo photo3 = new Photo();
        photo3.setPost(post2);
        photo3.setPath("/images/posts/post2_1.jpg");
        photoRepository.save(photo3);

        Photo photo4 = new Photo();
        photo4.setPost(post2);
        photo4.setPath("/images/posts/post2_2.jpg");
        photoRepository.save(photo4);

        Photo photo5 = new Photo();
        photo5.setPost(post2);
        photo5.setPath("/images/posts/post2_3.jpg");
        photoRepository.save(photo5);

        // 전체 사진 조회 테스트
        List<Photo> allPhotos = photoRepository.findAll();
        assertThat(allPhotos).hasSize(5);

        // ID로 사진 조회 테스트
        Photo foundPhoto = photoRepository.findById(photo1.getImageId()).orElse(null);
        assertThat(foundPhoto).isNotNull();
        assertThat(foundPhoto.getPath()).isEqualTo("/images/posts/post1_1.jpg");

        // 게시물별 사진 조회 테스트
        List<Photo> post1Photos = photoRepository.findByPost(post1);
        assertThat(post1Photos).hasSize(2);

        List<Photo> post2Photos = photoRepository.findByPost(post2);
        assertThat(post2Photos).hasSize(3);

        // 사진 경로 확인
        assertThat(post1Photos.get(0).getPath()).contains("post1");
        assertThat(post2Photos.get(0).getPath()).contains("post2");
    }
}