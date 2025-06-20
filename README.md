# 지난주 보완 및 리펙토링

# 구현 중 이슈

## 애플리케이션 Java 버전와 Docker의 JRE Java 버전 불일치

문제 사항

- 이미지 빌드후 이미지 실행 과정 중 오류

```
Exception in thread "main" java.lang.UnsupportedClassVersionError: com/ssafy/daangn/Application has been compiled by a more recent version of the Java Runtime (class file version 65.0), this version of the Java Runtime only recognizes class file versions up to 61.0
```

문제 원인

- 애플리케이션을 Java 21으로 컴파일했는데, Docker 이미지 안의 JRE가 Java 17이라서 발생하는 클래스 버전 불일치 문제

해결

- Dockerfile의 베이스 이미지를 Java 21로 수정
  `Dockerfile`

```
- FROM openjdk:17
+ FROM openjdk:21
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]
```

> [!NOTE]
> 코드 수정 이후 이미지 재빌드/실행를 위한 기존 도커 컨테이너/이미지 삭제 명령어
>
> ```
> docker rm -f $(docker ps -a -q --filter ancestor=spring-daangn:latest) && docker rmi spring-daangn:latest
> ```
>
> - `docker rm -f ...` : 컨테이너를 강제 중지 후 삭제
> - `docker rmi` : 이미지 제거
