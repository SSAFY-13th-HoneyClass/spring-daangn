# spring-daangn

# 🥕 당근마켓 데이터베이스 모델링

## 📦 주요 기능

- 회원
  - 당근은 회원가입할 때 휴대폰번호만 이용
  - 이메일 등록 가능
  - 매너온도라는 사용자 수치 존재
  - 회원은 자신이 거래할 지역 설정
- 상품
  - 사용자는 중고상품을 게시글로 등록가능
  - 상품 등록시 제목, 내용, 사진, 거래 위치, 가격 등록
  - 상품 카테고리 지정 가능
  - 상품은 판매중, 판매완료와 같은 상태가 존재
  - 상품을 판매할 수 있는 위치는 회원이 거래할 수 있는 지역내에서 가능    
- 관심
  - 회원은 상품들을 자신의 관심상품으로 등록가능   
- 채팅
  - 구매하고 싶은 상품에 대해 판매자와 구매자가 채팅으로 소통
  - 한 상품게시글에 여러 채팅방 존재가능


---

## 📐 ERD(Entity Relationship Diagram)

> 아래는 당근마켓 서비스를 기반으로 설계된 ERD입니다.

<img width="1190" alt="image" src="https://github.com/user-attachments/assets/7efe189b-3ae4-4551-918f-03957cc30c17" />

---

## 🧩 도메인 모델 설명

### ✅ Users

| 컬럼명                 | 타입     | 설명                |
| ------------------- | ------ | ----------------- |
| id                  | Long   | 회원 ID (PK)        |
| area\_id            | Long   | 지역 ID (FK, areas) |
| email               | String | 이메일               |
| phone\_number       | String | 휴대폰 번호            |
| manner\_temperature | Double | 매너온도              |


### ✅ Products
| 컬럼명          | 타입      | 설명                       |
| ------------ | ------- | ------------------------ |
| id           | Long    | 상품 ID (PK)               |
| writer\_id   | Long    | 작성자 ID (FK, users)       |
| category\_id | Long    | 카테고리 ID (FK, categories) |
| area\_id     | Long    | 지역 ID (FK, areas)        |
| title        | String  | 제목                       |
| description  | String  | 설명                       |
| price        | Integer | 가격                       |
| status       | String  | 판매 상태                    |


### ✅ Categories
| 컬럼명            | 타입     | 설명           |
| -------------- | ------ | ------------ |
| id             | Long   | 카테고리 ID (PK) |
| category\_name | String | 카테고리 이름      |


### ✅ Areas
| 컬럼명  | 타입     | 설명         |
| ---- | ------ | ---------- |
| id   | Long   | 지역 ID (PK) |
| name | String | 지역명        |


### ✅ Likes
| 컬럼명         | 타입   | 설명                   |
| ----------- | ---- | -------------------- |
| id          | Long | 관심 ID (PK)           |
| user\_id    | Long | 회원 ID (FK, users)    |
| product\_id | Long | 상품 ID (FK, products) |


### ✅ Images
| 컬럼명         | 타입     | 설명                   |
| ----------- | ------ | -------------------- |
| id          | Long   | 이미지 ID (PK)          |
| product\_id | Long   | 상품 ID (FK, products) |
| image\_name | String | 이미지 이름               |
| image\_url  | String | 이미지 URL              |


### ✅ ChatRoom
| 컬럼명         | 타입   | 설명                   |
| ----------- | ---- | -------------------- |
| id          | Long | 채팅방 ID (PK)          |
| product\_id | Long | 상품 ID (FK, products) |
| buyer\_id   | Long | 구매자 ID (FK, users)   |


### ✅ ChatMessages
| 컬럼명            | 타입     | 설명                      |
| -------------- | ------ | ----------------------- |
| id             | Long   | 메시지 ID (PK)             |
| chat\_room\_id | Long   | 채팅방 ID (FK, chat\_room) |
| user\_id       | Long   | 작성자 회원 ID (FK, users)   |
| message        | String | 메시지 내용                  |


---

# 🗂 Repository 단위 테스트
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void 상품등록() {
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
        productRepository.save(product1);

        //when
        ProductEntity result = productRepository.findById(product1.getId()).get();

        //then
        assertThat(result).isEqualTo(product1);
    }

    @Test
    void 상품목록조회() {
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
        productRepository.save(product1);

        ProductEntity product2 = new ProductEntity();
        product2.setWriter(user);
        product2.setTitle("제목2");
        product2.setDescription("내용2");
        product2.setPrice(10000);
        product2.setStatus("on_sale");
        productRepository.save(product2);

        ProductEntity product3 = new ProductEntity();
        product3.setWriter(user);
        product3.setTitle("제목3");
        product3.setDescription("내용3");
        product3.setPrice(10000);
        product3.setStatus("on_sale");
        productRepository.save(product3);

        //when
        List<ProductEntity> list = productRepository.findAllByWriterId(user.getId());

        //then
        assertThat(list.size()).isEqualTo(3);
    }
}
```
| 테스트 이름     | 설명                                                                  |
| ---------- | ------------------------------------------------------------------- |
| `상품등록()`   | 회원(User)을 생성한 뒤, 상품(Product)을 등록하고, 등록한 상품이 정상적으로 저장되고 조회되는지 검증합니다. |
| `상품목록조회()` | 동일한 작성자가 등록한 상품 3개를 저장한 후, 작성자의 ID로 조회하여 올바르게 3개의 상품이 반환되는지 검증합니다.  |

### 🧾 테스트 결과 로그 (콘솔 출력)
```
2025-05-09T17:49:42.744+09:00 DEBUG 39221 --- [    Test worker] org.hibernate.SQL                        : 
    insert 
    into
        users
        (area_id, email, manner_temperature, phone_number) 
    values
        (?, ?, ?, ?)
Hibernate: 
    insert 
    into
        users
        (area_id, email, manner_temperature, phone_number) 
    values
        (?, ?, ?, ?)
2025-05-09T17:49:42.759+09:00 DEBUG 39221 --- [    Test worker] org.hibernate.SQL                        : 
    insert 
    into
        products
        (area_id, category_id, description, price, status, title, writer_id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
Hibernate: 
    insert 
    into
        products
        (area_id, category_id, description, price, status, title, writer_id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
2025-05-09T17:49:42.762+09:00 DEBUG 39221 --- [    Test worker] org.hibernate.SQL                        : 
    insert 
    into
        products
        (area_id, category_id, description, price, status, title, writer_id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
Hibernate: 
    insert 
    into
        products
        (area_id, category_id, description, price, status, title, writer_id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
2025-05-09T17:49:42.763+09:00 DEBUG 39221 --- [    Test worker] org.hibernate.SQL                        : 
    insert 
    into
        products
        (area_id, category_id, description, price, status, title, writer_id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
Hibernate: 
    insert 
    into
        products
        (area_id, category_id, description, price, status, title, writer_id) 
    values
        (?, ?, ?, ?, ?, ?, ?)
2025-05-09T17:49:42.796+09:00 DEBUG 39221 --- [    Test worker] org.hibernate.SQL                        : 
    select
        pe1_0.id,
        pe1_0.area_id,
        pe1_0.category_id,
        pe1_0.description,
        pe1_0.price,
        pe1_0.status,
        pe1_0.title,
        pe1_0.writer_id 
    from
        products pe1_0 
    left join
        users w1_0 
            on w1_0.id=pe1_0.writer_id 
    where
        w1_0.id=?
Hibernate: 
    select
        pe1_0.id,
        pe1_0.area_id,
        pe1_0.category_id,
        pe1_0.description,
        pe1_0.price,
        pe1_0.status,
        pe1_0.title,
        pe1_0.writer_id 
    from
        products pe1_0 
    left join
        users w1_0 
            on w1_0.id=pe1_0.writer_id 
    where
        w1_0.id=?
```
실행된 쿼리문들을 확인할 수 있다.

### 🧾 테스트 결과 이미지
<img width="313" alt="image" src="https://github.com/user-attachments/assets/87773dff-a4e9-4328-a217-da3dccffb90b" />
