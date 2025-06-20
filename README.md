# 지난주 보완 및 리펙토링
## 데이터베이스 변경
- H2 → MySQL

## JPA ddl-auto 속성 변경
- 변경 전 : create-drop
  - 엔티티로 등록된 클래스와 매핑되는 테이블이 존재한다면 기존 테이블을 삭제하고 자동으로 테이블을 생성
  - 애플리케이션이 종료될 때 테이블을 삭제
- 변경 후 : update
  - 엔티티로 등록된 클래스와 매핑되는 테이블이 없으면 새로 생성
  - 기존 테이블이 존재한다면 삭제 X, 그대로 사용

# 구현 기능

## jar 파일 생성
방법 1. Gradle 확장프로그램을 통해 jar 파일 생성

`Tasks > bulid > bootJar` 실행

![image](https://github.com/user-attachments/assets/eb2fbbae-d3a5-433e-87b9-3b1be8099b29)

방법 2. 콘솔로 파일 생성
```
./gradlew clean bootJar -x test
```
- `-x test` : 테스트 코드 제외하고 빌드

실행된 jar 파일 생성 위치

`build > libs > *-SNAPSHOT.jar`

![image](https://github.com/user-attachments/assets/b6504593-de93-489f-89ae-aded2d9b6b8e)

## 도커 이미지 생성 및 실행
### Dockerfile 생성
```
FROM openjdk:21
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]
```
[구현 이후 버전 이슈 발생](#애플리케이션-java-버전와-docker의-jre-java-버전-불일치)

### 이미지 생성
```docker
docker build -t {docker image 이름} {Dockerfile의 위치}
```

```docker
docker build -t daangn .
```

### 이미지 실행
```docker
docker run -p 8080:8080 {docker image 이름}
```

```docker
docker run -p 8080:8080 daangn
```

### docker-compose.yml 생성
- Dockerfile은 하나의 이미지
- docker-compose.yml는 여러 이미지를 같은 네트워크 연결을 통한 제어
  - Spring과 MySQL을 동시에 제어
```yaml
services:
  db:
    ...MySQL 관련 설정

  app:
    ...Spring 관련 설정
```
![image](https://github.com/user-attachments/assets/1e5ff0ae-c983-4695-b3c5-74a056918c9a)

### docker-compose.yml 실행
```docker
docker-compose -f docker-compose.yml up --build
```
- `-d` : 백그라운드 실행
  - 생략시 터미널에 실시간 로그 출력


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
