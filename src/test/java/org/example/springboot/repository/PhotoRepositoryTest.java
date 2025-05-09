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
        // given
        // 테스트 사용자 생성
        User user = User.builder()
                .email("user@example.com")
                .password("password")
                .phone("010-1234-5678")
                .name("테스트사용자")
                .profile("안녕하세요 테스트 사용자입니다.")
                .nickname("테스트닉네임")
                .profileImgPath("/images/profile.jpg")
                .role("USER")
                .build();
        userRepository.save(user);

        // 테스트 게시물 생성
        post1 = Post.builder()
                .user(user)
                .title("첫 번째 게시물")
                .content("첫 번째 게시물 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post1);

        post2 = Post.builder()
                .user(user)
                .title("두 번째 게시물")
                .content("두 번째 게시물 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post2);
    }

    @Test
    @DisplayName("사진 등록 및 조회 테스트")
    public void createAndFindPhotos() {
        // given
        Photo photo1 = Photo.builder()
                .post(post1)
                .path("/images/posts/post1_1.jpg")
                .build();

        Photo photo2 = Photo.builder()
                .post(post1)
                .path("/images/posts/post1_2.jpg")
                .build();

        Photo photo3 = Photo.builder()
                .post(post2)
                .path("/images/posts/post2_1.jpg")
                .build();

        Photo photo4 = Photo.builder()
                .post(post2)
                .path("/images/posts/post2_2.jpg")
                .build();

        Photo photo5 = Photo.builder()
                .post(post2)
                .path("/images/posts/post2_3.jpg")
                .build();

        // when
        photoRepository.save(photo1);
        photoRepository.save(photo2);
        photoRepository.save(photo3);
        photoRepository.save(photo4);
        photoRepository.save(photo5);

        List<Photo> allPhotos = photoRepository.findAll();
        Photo foundPhoto = photoRepository.findById(photo1.getImageId()).orElse(null);
        List<Photo> post1Photos = photoRepository.findByPost(post1);
        List<Photo> post2Photos = photoRepository.findByPost(post2);

        // then
        assertThat(allPhotos).hasSize(5);

        assertThat(foundPhoto).isNotNull();
        assertThat(foundPhoto.getPath()).isEqualTo("/images/posts/post1_1.jpg");

        assertThat(post1Photos).hasSize(2);

        assertThat(post2Photos).hasSize(3);

        assertThat(post1Photos.get(0).getPath()).contains("post1");
        assertThat(post2Photos.get(0).getPath()).contains("post2");
    }
}