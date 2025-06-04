# 당근마켓 클론 프로젝트 - 4주차

## 1️⃣ 당근마켓의 4가지 HTTP Method API 만들어봐요
-----------

### API 구현 현황
> User, Post, Product 도메인에 대한 CRUD API 구현 완료

#### 구현된 API 목록

**User API**
- `POST /api/users/` - 새로운 사용자 생성 (회원가입)
- `GET /api/users/` - 모든 사용자 조회
- `GET /api/users/{id}/` - 특정 사용자 조회
- `DELETE /api/users/{id}/` - 특정 사용자 삭제

**Post API**
- `POST /api/posts/` - 새로운 게시글 생성
- `GET /api/posts/` - 모든 게시글 조회
- `GET /api/posts/{id}/` - 특정 게시글 조회
- `DELETE /api/posts/{id}/` - 특정 게시글 삭제

**Product API**
- `POST /api/products/` - 새로운 상품 생성
- `GET /api/products/` - 모든 상품 조회
- `GET /api/products/{id}/` - 특정 상품 조회
- `DELETE /api/products/{id}/` - 특정 상품 삭제

### Controller 구현 예시

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto requestDto) {
        User user = UserRequestDto.toEntity(requestDto);
        User savedUser = userService.join(user);
        
        if (savedUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 존재하는 사용자 ID입니다.");
        }
        
        UserResponseDto responseDto = UserResponseDto.fromEntity(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
```

## 2️⃣ 정적 팩토리 메서드를 사용해서 DTO 사용해봐요
-----------

### DTO 패턴 구현
> Request/Response DTO와 정적 팩토리 메서드를 활용한 엔티티 변환 구현

#### DTO 구조

**Request DTO (Entity 생성용)**
```java
@Getter
@Builder
public class UserRequestDto {
    private String id;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    
    // 정적 팩토리 메서드
    public static User toEntity(UserRequestDto dto) {
        return User.builder()
                .id(dto.getId())
                .password(dto.getPassword())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .manner(dto.getManner() != null ? dto.getManner() : new BigDecimal("36.5"))
                .role(dto.getRole() != null ? dto.getRole() : "USER")
                .build();
    }
}
```

**Response DTO (조회 결과 반환용)**
```java
@Getter
@Builder
public class UserResponseDto {
    private Long uuid;
    private String id;
    private String name;
    private String nickname;
    private String phone;
    private BigDecimal manner;
    
    // 정적 팩토리 메서드
    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .uuid(user.getUuid())
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .manner(user.getManner())
                .build();
    }
}
```

### 장점
- **캡슐화**: 내부 로직 숨김
- **가독성**: 메서드명으로 의도 명확화 (`fromEntity`, `toEntity`)
- **유지보수**: 변환 로직 중앙 집중화

## 3️⃣ Global Exception을 만들어봐요
-----------

### Exception 처리 구조

**ErrorCode 열거형**
```java
@Getter
public enum ErrorCode {
    // Global Error
    BAD_REQUEST_ERROR(400, "G001", "잘못된 요청입니다"),
    NOT_FOUND_ERROR(404, "G005", "요청한 리소스를 찾을 수 없습니다"),
    
    // Custom Error
    USER_NOT_FOUND(404, "U001", "존재하지 않는 사용자입니다"),
    USER_ALREADY_EXISTS(409, "U002", "이미 존재하는 사용자 ID입니다"),
    POST_NOT_FOUND(404, "P001", "존재하지 않는 게시글입니다"),
    PRODUCT_NOT_FOUND(404, "PR001", "존재하지 않는 상품입니다");

    private final int status;
    private final String divisionCode;
    private final String message;
}
```

**Global Exception Handler**
```java
@Slf4j
@RestControllerAdvice(basePackages = "com.example.daangn.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR, 
                                                       String.valueOf(stringBuilder));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
```

## 4️⃣ Swagger 연동 후 Controller 통합 테스트를 해봐요
-----------

### Swagger 설정

**SwaggerConfig 구성**
```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("당근마켓 클론 API")
                        .description("당근마켓 클론 프로젝트의 REST API 문서")
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버")));
    }
}
```

### Controller 통합 테스트

**테스트 구조**
```java
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @DisplayName("사용자 생성 성공 테스트")
    void createUser_Success() throws Exception {
        // given
        UserRequestDto requestDto = UserRequestDto.builder()
                .id("testuser")
                .name("테스트 사용자")
                .build();

        // when & then
        mockMvc.perform(post("/api/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("testuser"));
    }
}
```

### API 문서 접근
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`

## 5️⃣ 리팩토링 사항
-----------

### MySQL 예약어 충돌 해결
> 테이블 컬럼명과 MySQL 예약어 충돌 방지를 위한 컬럼명 변경

**변경 사항**
- `chatting_logs.check` → `chatting_logs.is_checked`
- `user_locations.range` → `user_locations.location_range`

**Entity 수정 예시**
```java
@Entity
public class ChattingLog {
    @Column(name = "is_checked")
    private Boolean check;
}

@Entity 
public class UserLocation {
    @Column(name = "location_range")
    private Integer range;
}
```

## 📊 테스트 결과
-----------

### Controller 통합 테스트 현황
- **UserControllerTest**: 6개 테스트 성공
- **PostControllerTest**: 6개 테스트 성공
- **ProductControllerTest**: 8개 테스트 성공

### 프로젝트 구조
```
src/main/java/com/example/daangn/
├── controller/          # REST API 컨트롤러
├── domain/             # 도메인별 패키지
│   ├── user/           # 사용자 도메인
│   ├── post/           # 게시글 도메인
│   └── product/        # 상품 도메인
├── config/             # 설정 클래스
└── exception/          # 예외 처리
```

## 결론
-----------

> [!IMPORTANT] API 설계의 완성도 향상
> - RESTful API 설계 원칙 준수
> - DTO 패턴을 통한 계층 분리 강화
> - 전역 예외 처리로 일관된 에러 응답
> - Swagger를 통한 API 문서화 자동화
> - 통합 테스트로 API 동작 검증 완료

### 핵심 성과
1. **완전한 CRUD API** 구현으로 실제 서비스 수준의 API 제공
2. **정적 팩토리 메서드** 활용으로 코드 가독성과 유지보수성 향상
3. **Global Exception Handler**로 일관된 에러 응답 체계 구축
4. **Swagger 연동**으로 개발 생산성 및 협업 효율성 증대