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
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        // 테스트 사용자 생성 - Builder 사용
        user1 = User.builder()
                .email("user1@example.com")
                .password("password1")
                .phone("010-1111-1111")
                .name("사용자1")
                .profile("안녕하세요 사용자1입니다.")
                .nickname("닉네임1")
                .profileImgPath("/images/profile1.jpg")
                .role("USER")
                .build();
        userRepository.save(user1);

        user2 = User.builder()
                .email("user2@example.com")
                .password("password2")
                .phone("010-2222-2222")
                .name("사용자2")
                .profile("안녕하세요 사용자2입니다.")
                .nickname("닉네임2")
                .profileImgPath("/images/profile2.jpg")
                .role("USER")
                .build();
        userRepository.save(user2);
    }

    @Test
    @DisplayName("게시물 등록 및 조회 테스트")
    public void createAndFindPosts() {
        // given
        Post post1 = Post.builder()
                .user(user1)
                .title("첫 번째 게시물")
                .content("첫 번째 게시물 내용입니다.")
                .status("판매중")
                .build();

        Post post2 = Post.builder()
                .user(user1)
                .title("두 번째 게시물")
                .content("두 번째 게시물 내용입니다.")
                .status("판매중")
                .build();

        Post post3 = Post.builder()
                .user(user2)
                .title("세 번째 게시물")
                .content("세 번째 게시물 내용입니다.")
                .status("판매완료")
                .build();

        // when
        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        List<Post> allPosts = postRepository.findAll();
        Post foundPost = postRepository.findById(post1.getPostId()).orElse(null);
        List<Post> user1Posts = postRepository.findByUser(user1);
        List<Post> sellingPosts = postRepository.findByStatus("판매중");
        List<Post> soldPosts = postRepository.findByStatus("판매완료");
        List<Post> latestPosts = postRepository.findAllOrderByCreatedAtDesc();

        // then
        assertThat(allPosts).hasSize(3);

        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTitle()).isEqualTo("첫 번째 게시물");

        assertThat(user1Posts).hasSize(2);
        assertThat(user1Posts.get(0).getUser().getEmail()).isEqualTo("user1@example.com");

        assertThat(sellingPosts).hasSize(2);

        assertThat(soldPosts).hasSize(1);
        assertThat(soldPosts.get(0).getUser().getEmail()).isEqualTo("user2@example.com");

        assertThat(latestPosts).hasSize(3);
        assertThat(latestPosts.get(0).getTitle()).isEqualTo("세 번째 게시물");
    }

    @Test
    @DisplayName("게시물과 관련된 사진 등록 및 조회 테스트")
    public void testPostWithPhotos() {
        // given
        // 1. 게시물 생성
        Post post = Post.builder()
                .user(user1)
                .title("사진이 있는 게시물")
                .content("사진이 첨부된 게시물 내용입니다.")
                .status("판매중")
                .build();
        postRepository.save(post);

        // 2. 해당 게시물에 관련된 사진 3개 생성
        Photo photo1 = Photo.builder()
                .post(post)
                .path("/images/posts/photo1.jpg")
                .build();

        Photo photo2 = Photo.builder()
                .post(post)
                .path("/images/posts/photo2.jpg")
                .build();

        Photo photo3 = Photo.builder()
                .post(post)
                .path("/images/posts/photo3.jpg")
                .build();

        // when
        // 3. 사진들을 저장
        photoRepository.save(photo1);
        photoRepository.save(photo2);
        photoRepository.save(photo3);

        // 4. 게시물 ID로 게시물을 다시 조회
        Post retrievedPost = postRepository.findById(post.getPostId()).orElse(null);

        // 5. 게시물에 연결된 사진 목록 조회
        List<Photo> photos = photoRepository.findByPost(retrievedPost);

        // then
        // 6. 게시물이 null이 아닌지 확인
        assertThat(retrievedPost).isNotNull();

        // 7. 게시물의 제목이 올바른지 확인
        assertThat(retrievedPost.getTitle()).isEqualTo("사진이 있는 게시물");

        // 8. 사진 목록이 비어있지 않은지 확인
        assertThat(photos).isNotEmpty();

        // 9. 사진 목록의 크기가 3인지 확인
        assertThat(photos).hasSize(3);

        // 10. 각 사진의 경로가 올바른지 확인
        assertThat(photos).extracting("path")
                .containsExactlyInAnyOrder(
                        "/images/posts/photo1.jpg",
                        "/images/posts/photo2.jpg",
                        "/images/posts/photo3.jpg"
                );

        // 11. 모든 사진이 동일한 게시물을 참조하는지 확인
        assertThat(photos).allMatch(photo -> photo.getPost().getPostId().equals(post.getPostId()));
    }
}