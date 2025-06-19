# Eclipse Temurin JRE 21 사용
FROM eclipse-temurin:21-jre
WORKDIR /app

# 이미 빌드된 JAR 파일 복사
COPY build/libs/spring-daangn-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dspring.redis.host=${SPRING_DATA_REDIS_HOST:-localhost} -Dspring.redis.port=${SPRING_DATA_REDIS_PORT:-6379} -jar app.jar"] 