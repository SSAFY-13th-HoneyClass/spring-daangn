# 프로젝트명: 당근마켓 클론 코딩 (Daangn Clone)

## 📌 프로젝트 소개

- **프로젝트 이름**: 당근 클론코딩
- **한줄 소개**: 당근 서비스를 보고 DB 설계 및 JPA를 이용하여 레포지토리 작성, JUnit 테스트 진행
- **목표**: 서비스를 처음부터 직접 만들어보며, 다른 사람의 코드를 참고해 클린 코드에 대해 배우고자 함. 선택한 이유와 배경을 중심으로 고민하며 프로젝트를 진행함.

## 🗂️ ERD

![alt text](/image/daangn_erd.png)

## 🧩 주요 기능

### 🔸 카테고리

- 실제로는 `INSERT INTO` 쿼리로 직접 넣을 가능성이 높지만, JPA의 `save()` 메서드를 활용하여 작성 예정

### 🔸 채팅

- **Create**
- **Read**
  - 채팅방 아이디 기준으로 보낸 시간순 정렬 (최대 100개)

### 🔸 채팅방

- **Create**
- **Read**
  - 판매자 아이디 기준 생성일순 정렬
  - 구매자 아이디 기준 생성일순 정렬
  - 상품 기준 생성일순 정렬
  - 구매자 + 상품 기준 생성일순 정렬

### 🔸 중고거래 이미지

- **Create / Update / Delete**
- **Read**
  - 중고거래 상세 조회 시 함께 조회 (JPQL 사용 예정)

### 🔸 중고거래

- **Create / Update / Delete**
- **Read**
  - 판매자 아이디 기준 생성일순 리스트 조회
  - 제목 + 상세설명 키워드 검색
  - 특정 위도, 경도로부터 거리 N 이하 물품 조회
  - 중고거래 상세 보기

### 🔸 좋아요

- **Create / Delete**

### 🔸 중고거래 상태

- 실제 서비스에서는 직접 쿼리문 사용 예정이나, 본 프로젝트에서는 `save()`로 생성

### 🔸 검색

- **Create**

### 🔸 사용자

- **Create / Update / Delete**
- **Read**
  - 나의 정보 상세 보기

## 🛠 기술 스택

- Spring Boot
- JPA
- MySQL
- JUnit
- Lombok

## 🗂 프로젝트 구조

- `.env` 파일을 만들어 환경변수 관리


<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>

# 3주차 과제

## [N+1문제 TEST]

### 1. laze로 그냥 불러왔을 때
```java
//레파지토리
List<Sale> findAll();

///테스트
@Test
@Transactional
public void problem() {
    List<Sale> sales = saleRepository.findAll();
    System.out.println("sales : " + sales);
    for (Sale sale : sales) {
        System.out.println(sale.getUser().getNickname()); // ← 여기서 추가 쿼리 N번 발생
    }
}
```
 - select문이 22개 나오는 것을 알 수 있다. 
![alt text](/image/image.png)

### 2. fetch로 불러왔을 때
```java
//레파지토리
@Query("""
  SELECT DISTINCT s FROM Sale s
  JOIN FETCH s.user
  JOIN FETCH s.category
  JOIN FETCH s.status
""")
List<Sale> findAllWithUserCategoryStatus();

///테스트
@Test
@Transactional
public void solution() {
    List<Sale> sales = saleRepository.findAllWithUserCategoryStatus();
    for (Sale sale : sales) {
        System.out.println(sale.getUser().getNickname());
    }
}
```
 - select문이 2개 나오는 것을 알 수 있다. 
![alt text](/image/image-1.png)


## [service 테스트]
- 채팅방 저장 및 삭제 테스트 
![alt text](/image/image-2.png)




# 4주차 과제
## swagger
![alt text](/image/image-4.png)

## 중고거래 게시판 테스트
- 이미지와 같이 들어가는 경우 swagger로 테스트가 잘 안되어서 post는 postman 사용하여 테스트 진행.

### post요청 보냄.
![alt text](/image/image-5.png)

### 로컬에 이미지 잘 들어간거 확인 가능
![alt text](/image/image-6.png)

### 다시 조회 시 화긴 가능
![alt text](/image/image-7.png)


### delete요청
![alt text](/image/image-8.png)
- 이미지도 함께 삭제 됨.


### 커스텀 에러 처리
![alt text](/image/image-3.png)




# 5주차 과제 : 스프링 시큐리티

## 1. JWT 인증(Authentication) 방법
STUDY > auth_flow_summary.md 참고

## 2. 액세스 토큰 및 인증 인가

### 회원가입
![alt text](/image/image-9.png)

### encoding된 password
![alt text](/image/image-10.png)

### 로그인 시 액세스 토큰 발급
![alt text](/image/image-11.png)

### 토큰 없이 채팅방 생성 시 인증 필요
![alt text](/image/image-12.png)

### 토큰 인증 후 채팅방 생성 시 성공
![alt text](/image/image-13.png)

![alt text](/image/image-14.png)

### 리프레시 토큰 로그인
![alt text](/image/image-15.png)

### 리프레시 토큰을 이용한 액세스 토큰 갱신
![alt text](/image/image-16.png)


### 추가 하고싶은 일
- 로그아웃 시 액세스 토큰도 블랙리스트에 등록하기
- 토큰에 있는 user정보와 실제 api에 필요한 권한이 일치하는지 확인하는 filter추가
- 레디스에 올리고 쿠키에서 안보이게 하기

