# 1. Java 21 기반 이미지 사용
FROM openjdk:21-jdk-slim

# 2. 컨테이너 내 작업 디렉토리
WORKDIR /app

# 3. 빌드된 JAR 복사
COPY build/libs/spring-boot-0.0.1-SNAPSHOT.jar app.jar

# 4. (선택) 기본 환경 변수 설정 - 보통 Compose에서 함
#ENV DB_URL=jdbc:mysql://localhost:3306/daangn?useSSL=false&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true
#ENV DB_USERNAME=ssafy
#ENV DB_PASSWORD=ssafy
#ENV DB_DRIVER=com.mysql.cj.jdbc.Driver

# 5. 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
