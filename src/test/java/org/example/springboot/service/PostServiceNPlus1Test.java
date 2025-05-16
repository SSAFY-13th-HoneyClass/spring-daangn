package org.example.springboot.service;

import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.example.springboot.repository.PostRepository;
import org.example.springboot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class PostServiceNPlus1Test {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("N+1 문제 발생과 해결 테스트")
    @Transactional
    public void testNPlus1Problem() {
        // given: 5명의 사용자가 각각 3개의 게시글을 작성하는 상황
        System.out.println("\n\n===== 테스트 데이터 준비 =====");
        List<User> users = new ArrayList<>();

        // 5명의 사용자 생성
        for (int i = 0; i < 5; i++) {
            User user = User.builder()
                    .email("user" + i + "@example.com")
                    .password("password" + i)
                    .phone("010-1111-111" + i)
                    .name("사용자" + i)
                    .profile("안녕하세요 사용자" + i + "입니다.")
                    .nickname("닉네임" + i)
                    .profileImgPath("/images/profile" + i + ".jpg")
                    .role("USER")
                    .build();

            users.add(userRepository.save(user));
        }

        // 각 사용자별로 3개의 게시글 생성 (총 15개)
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);

            for (int j = 0; j < 3; j++) {
                Post post = Post.builder()
                        .user(user)
                        .title("게시글 " + i + "-" + j)
                        .content("게시글 내용 " + i + "-" + j)
                        .status("판매중")
                        .build();

                postRepository.save(post);
            }
        }

        System.out.println("===== 테스트 데이터 준비 완료 =====");
        System.out.println("총 사용자 수: " + users.size());
        System.out.println("총 게시글 수: " + postRepository.count());
        
        // 영속성 컨텍스트 초기화 - 이전에 로드된 엔티티를 모두 제거하여 N+1 문제를 더 명확하게 보여줌
        System.out.println("\n----- 영속성 컨텍스트 초기화 -----");
        entityManager.flush();
        entityManager.clear();
        System.out.println("----- 영속성 컨텍스트 초기화 완료 -----");

        // 트랜잭션을 분리하여 N+1 문제가 명확히 보이도록 함
        demonstrateNPlus1Problem();
        
        // 영속성 컨텍스트 초기화
        System.out.println("\n----- 영속성 컨텍스트 초기화 -----");
        entityManager.flush();
        entityManager.clear();
        System.out.println("----- 영속성 컨텍스트 초기화 완료 -----");
        
        // Fetch Join 해결 방법 테스트
        demonstrateNPlus1Solution();
        
        // 영속성 컨텍스트 초기화
        System.out.println("\n----- 영속성 컨텍스트 초기화 -----");
        entityManager.flush();
        entityManager.clear();
        System.out.println("----- 영속성 컨텍스트 초기화 완료 -----");
        
        // Entity Graph 해결 방법 테스트
        demonstrateEntityGraphSolution();
    }
    
    // N+1 문제를 명확하게 보여주는 메서드
    private void demonstrateNPlus1Problem() {
        System.out.println("\n\n===== N+1 문제 발생 케이스 =====");
        System.out.println("findAllPosts() 메서드 호출 - 게시글 조회 쿼리 1번 실행");
        System.out.println("SQL쿼리 예상: SELECT * FROM posts");

        // 모든 게시글 조회 - 이 시점에서는 사용자 정보는 로드되지 않음
        List<Post> posts = postRepository.findAllPosts();
        System.out.println("게시글 조회 완료: " + posts.size() + "개");
        
        System.out.println("\n----- 이제 각 게시글의 사용자 정보를 조회하며 N+1 문제 발생 -----");
        System.out.println("각 게시글마다 SELECT * FROM users WHERE user_id = ? 쿼리가 실행됨");
        
        // 각 게시글의 사용자 정보에 접근할 때마다 추가 쿼리 발생
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            System.out.println("\n=== [" + (i+1) + "/" + posts.size() + "] 게시글 ID: " + post.getPostId() + " 사용자 정보 접근 시작 ===");
            System.out.println("이제 게시글 작성자 정보에 접근하면 지연 로딩으로 인해 추가 쿼리 발생:");
            
            // 이 시점에서 사용자 정보에 접근하면 추가 쿼리가 발생함 (지연 로딩)
            System.out.println("--> 실행될 SQL: SELECT * FROM users WHERE user_id = " + post.getUser().getUserId());
            String nickname = post.getUser().getNickname();
            
            System.out.println("게시글: " + post.getTitle() + ", 작성자: " + nickname);
            System.out.println("=== 게시글 ID: " + post.getPostId() + " 사용자 정보 접근 완료 ===");
        }

        System.out.println("\nN+1 문제 발생: 게시글 조회 쿼리 1번 + 사용자 조회 쿼리 " + posts.size() + "번 = 총 " + (1 + posts.size()) + "번 쿼리 실행");
    }
    
    // Fetch Join을 사용한 N+1 문제 해결 방법
    private void demonstrateNPlus1Solution() {
        System.out.println("\n\n===== N+1 문제 해결 케이스 1: Fetch Join =====");
        System.out.println("findAllPostsWithUser() 메서드 호출 - Fetch Join으로 게시글과 사용자 함께 조회 쿼리 1번 실행");
        System.out.println("SQL쿼리 예상: SELECT p.*, u.* FROM posts p JOIN users u ON p.user_id = u.user_id");

        // Fetch Join을 사용하여 게시글과 사용자 정보를 한 번에 조회
        List<Post> posts = postRepository.findAllPostsWithUser();
        System.out.println("게시글과 사용자 정보 조회 완료: " + posts.size() + "개");
        
        System.out.println("\n----- 이제 각 게시글의 사용자 정보를 조회하지만 추가 쿼리 없음 -----");
        System.out.println("이미 모든 사용자 정보가 로딩되어 있으므로 추가 쿼리 발생하지 않음");

        // 각 게시글의 사용자 정보에 접근해도 추가 쿼리가 발생하지 않음 (이미 로딩됨)
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            System.out.println("\n=== [" + (i+1) + "/" + posts.size() + "] 게시글 ID: " + post.getPostId() + " 사용자 정보 접근 시작 ===");
            System.out.println("이미 로딩된 사용자 정보에 접근 (추가 SQL 쿼리 없음):");
            
            // 이미 로딩된 사용자 정보에 접근 (추가 쿼리 없음)
            String nickname = post.getUser().getNickname();
            
            System.out.println("게시글: " + post.getTitle() + ", 작성자: " + nickname);
            System.out.println("=== 게시글 ID: " + post.getPostId() + " 사용자 정보 접근 완료 ===");
        }

        System.out.println("\nN+1 문제 해결: Fetch Join으로 단 1번의 쿼리로 모든 데이터 조회 완료");
    }
    
    // Entity Graph를 사용한 N+1 문제 해결 방법
    private void demonstrateEntityGraphSolution() {
        System.out.println("\n\n===== N+1 문제 해결 케이스 2: Entity Graph =====");
        System.out.println("findAll() 메서드 호출 - Entity Graph로 게시글과 사용자 함께 조회 쿼리 1번 실행");
        System.out.println("SQL쿼리 예상: SELECT p.*, u.* FROM posts p LEFT JOIN users u ON p.user_id = u.user_id");

        // Entity Graph를 사용하여 게시글과 사용자 정보를 한 번에 조회
        List<Post> posts = postRepository.findAll();
        System.out.println("게시글과 사용자 정보 조회 완료: " + posts.size() + "개");
        
        System.out.println("\n----- 이제 각 게시글의 사용자 정보를 조회하지만 추가 쿼리 없음 -----");
        System.out.println("Entity Graph로 모든 사용자 정보가 로딩되어 있으므로 추가 쿼리 발생하지 않음");

        // 각 게시글의 사용자 정보에 접근해도 추가 쿼리가 발생하지 않음 (이미 로딩됨)
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            System.out.println("\n=== [" + (i+1) + "/" + posts.size() + "] 게시글 ID: " + post.getPostId() + " 사용자 정보 접근 시작 ===");
            System.out.println("이미 로딩된 사용자 정보에 접근 (추가 SQL 쿼리 없음):");
            
            // 이미 로딩된 사용자 정보에 접근 (추가 쿼리 없음)
            String nickname = post.getUser().getNickname();
            
            System.out.println("게시글: " + post.getTitle() + ", 작성자: " + nickname);
            System.out.println("=== 게시글 ID: " + post.getPostId() + " 사용자 정보 접근 완료 ===");
        }

        System.out.println("\nN+1 문제 해결: Entity Graph로 단 1번의 쿼리로 모든 데이터 조회 완료");
    }
}