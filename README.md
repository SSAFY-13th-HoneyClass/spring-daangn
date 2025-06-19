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

### [🔐 5주차 미션](./5주차%20미션.md)
- JWT 인증(Authentication) 방법에 대해서 알아보기
- 액세스 토큰 발급 및 검증 로직 구현하기
- 회원가입 및 로그인 API 구현하고 테스트하기
- 토큰이 필요한 API 1개 이상 구현하고 테스트하기
- 리프레쉬 토큰 발급 로직 구현하고 테스트하기

### [🐳 6주차 미션](./6주차%20미션.md)
- Docker 이미지 생성 및 배포하기 (Docker Hub)
- AWS EC2 인스턴스에 컨테이너 배포
- Redis와 Spring Boot 컨테이너 연동
- 배포 환경에서 API 테스트 및 스크린샷 제공

---

## 🛠️ 기술 스택

### **Backend**
- **Java**: 21
- **Spring Boot**: 3.4.5
- **Spring Data JPA**
- **Spring Security**
- **JWT (JSON Web Token)**
- **Gradle**

### **Database**
- **H2 Database** (개발/테스트 환경)
- **MySQL 8.0** (AWS RDS)
- **Redis 7** (RefreshToken 관리)

### **DevOps & Infrastructure**
- **Docker**: 컨테이너화
- **Docker Hub**: 이미지 레지스트리
- **AWS EC2**: Ubuntu 22.04 LTS
- **AWS RDS**: MySQL 데이터베이스

### **Documentation & Testing**
- **Swagger/OpenAPI 3.0**
- **JUnit 5**

---

## 📂 프로젝트 구조

```
spring-daangn/
├── src/main/java/org/example/springboot/
│   ├── controller/        # REST API 컨트롤러
│   ├── service/          # 비즈니스 로직 서비스
│   ├── repository/       # 데이터 접근 계층
│   ├── domain/           # JPA 엔티티
│   ├── dto/              # 데이터 전송 객체
│   ├── exception/        # 예외 처리 클래스
│   ├── config/           # JWT 인증 및 보안 설정
│   └── Application.java  # 메인 애플리케이션
├── src/test/java/        # 테스트 코드
├── img/6주차/            # 6주차 배포 스크린샷
├── Dockerfile           # Docker 이미지 빌드 설정
├── docker-compose.yml   # Docker Compose 설정
├── .dockerignore        # Docker 빌드 제외 파일
├── .gitignore           # Git 추적 제외 파일
├── 2주차 미션.md         # 2주차 미션 문서
├── 3주차 미션.md         # 3주차 미션 문서
├── 4주차 미션.md         # 4주차 미션 문서
├── 5주차 미션.md         # 5주차 미션 문서
├── 6주차 미션.md         # 6주차 미션 문서
└── README.md            # 프로젝트 소개
```

---

## 🚀 실행 방법

### **로컬 환경에서 실행**

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

### **Docker 환경에서 실행**

1. **Docker Compose로 전체 스택 실행**
   ```bash
   docker-compose up -d
   ```

2. **개별 컨테이너로 실행**
   ```bash
   # Redis 컨테이너 실행
   docker run -d --name redis --network bridge -p 6379:6379 redis:7-alpine
   
   # Spring Boot 애플리케이션 실행
   docker run -d --name spring-app --network bridge \
     -e SPRING_DATA_REDIS_HOST=redis \
     -p 8080:8080 \
     chane00/spring-daangn
   ```

### **배포된 환경에서 접근**

- **EC2 배포 환경**: `http://13.124.28.159:8080` (보안 그룹 설정 필요)
- **Swagger UI**: `http://13.124.28.159:8080/swagger-ui.html`

---

## 📖 학습 목표

- **JPA/Hibernate**: 엔티티 설계, 연관관계 매핑, N+1 문제 해결
- **Spring Boot**: RESTful API 개발, 계층형 아키텍처
- **테스트**: 단위 테스트, 통합 테스트, TDD
- **API 문서화**: Swagger/OpenAPI를 활용한 문서화
- **예외 처리**: Global Exception Handler를 통한 통합 예외 처리
- **JWT 인증**: 토큰 기반 인증 시스템, RefreshToken 관리
- **Redis**: 인메모리 데이터베이스를 활용한 토큰 관리
- **보안**: Spring Security, XSS/CSRF 방지, HttpOnly 쿠키
- **Docker**: 컨테이너화, 멀티 플랫폼 빌드, Docker Compose
- **클라우드 배포**: AWS EC2를 활용한 프로덕션 배포
- **DevOps**: CI/CD 파이프라인, 컨테이너 오케스트레이션 