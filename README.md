# Spring 스터디 - 당근 클론 코딩

---

## 📚 주차별 미션

### [🌱 2주차 미션](./2주차%20미션.md)
- 당근마켓의 DB 모델링
- Repository 단위 테스트 진행
- JPA 엔티티에서 @Data와 Setter 사용을 지양해야 하는 이유

### [🥕 3주차 미션](./3주차%20미션.md)
- Service 계층 코드 작성
- Repository 계층 테스트 작성 (연관관계 매핑 및 N+1 문제 포함)
- Service 계층 단위 테스트 작성
- 순환 참조 테스트

### [🥕 4주차 미션](./4주차%20미션.md)
- 당근마켓의 4가지 HTTP Method API 만들기
- 정적 팩토리 메서드를 사용한 DTO 구현
- Global Exception Handler 만들기
- Swagger 연동 후 Controller 통합 테스트

---

## 🛠️ 기술 스택

- **Java**: 17
- **Spring Boot**: 3.4.5
- **Spring Data JPA**
- **H2 Database** (개발/테스트 환경)
- **Swagger/OpenAPI 3.0**
- **Spring Security**
- **Gradle**

---

## 📂 프로젝트 구조

```
spring-daangn/
├── src/main/java/org/example/springboot/
│   ├── controller/        # REST API 컨트롤러
│   ├── service/          # 비즈니스 로직 서비스
│   ├── repository/       # 데이터 접근 계층
│   ├── entity/           # JPA 엔티티
│   ├── dto/              # 데이터 전송 객체
│   ├── exception/        # 예외 처리 클래스
│   └── config/           # 설정 클래스
├── src/test/java/        # 테스트 코드
├── 2주차 미션.md         # 2주차 미션 문서
├── 3주차 미션.md         # 3주차 미션 문서
├── 4주차 미션.md         # 4주차 미션 문서
└── README.md            # 프로젝트 소개
```

---

## 🚀 실행 방법

1. **프로젝트 클론**
   ```bash
   git clone <repository-url>
   cd spring-daangn
   ```

2. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

3. **Swagger UI 접속**
   ```
   http://localhost:8080/swagger-ui.html
   ```

---

## 📖 학습 목표

- **JPA/Hibernate**: 엔티티 설계, 연관관계 매핑, N+1 문제 해결
- **Spring Boot**: RESTful API 개발, 계층형 아키텍처
- **테스트**: 단위 테스트, 통합 테스트, TDD
- **API 문서화**: Swagger/OpenAPI를 활용한 문서화
- **예외 처리**: Global Exception Handler를 통한 통합 예외 처리 