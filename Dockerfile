# 사용하려는 JDK 버전의 이미지 선택
FROM openjdk:21-jdk-slim
# .jar 파일을 컨테이너의 /app 디렉토리에 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
# 컨테이너 실행 시 .jar 파일을 실행
ENTRYPOINT ["java","-jar","/app.jar"]
# 애플리케이션이 사용할 포트 지정
EXPOSE 8080