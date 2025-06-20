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

## 로컬 빌드 - jar 파일 생성
방법 1. Gradle 확장프로그램을 통해 jar 파일 생성

`Tasks/bulid/bootJar` 실행

![image](https://github.com/user-attachments/assets/164ca120-b4f2-4127-a449-c418b7f746b8)


방법 2. 콘솔로 파일 생성
```
./gradlew clean bootJar -x test
```
- `-x test` : 테스트 코드 제외하고 빌드

실행된 jar 파일 생성 위치

`build/libs/*-SNAPSHOT.jar`

![image](https://github.com/user-attachments/assets/b6504593-de93-489f-89ae-aded2d9b6b8e)

## Docker 이미지 빌드 및 실행
### Dockerfile 생성
```
FROM openjdk:21
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]
```
[이미지 빌드 과정 버전 이슈 발생](#애플리케이션-java-버전와-docker의-jre-java-버전-불일치)

### Docker 이미지 빌드
```docker
docker build -t {docker image 이름} {Dockerfile의 위치}
```

```docker
docker build -t daangn .
```

### Docker 이미지 실행
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

## AWS 설정
### 회원가입
- 프리티어 기간인 1년이 지나서 새 계정 생성
- 서울(ap-northeast-2) 리전에서 인스턴스 생성

![image](https://github.com/user-attachments/assets/3c410cbc-03ef-4bca-9faf-9aa18a92cb4a)

### EC2 인스턴스 생성
- t2.micro Ubuntu 22.04 인스턴스 생성
- 보안 그룹 설정:
  - SSH 22/TCP (내 IP)
  - HTTP 80/TCP, Spring Boot 8080/TCP (0.0.0.0/0)

![image](https://github.com/user-attachments/assets/88ed877f-a79f-42a3-8183-5117a34a4cbc)

### RDS 인스턴스 생성

![image](https://github.com/user-attachments/assets/8bd4c7b9-b1eb-432b-b455-899da0823a4a)

- EC2와 동일 보안 그룹 사용
  - 인바운드/아웃바운드에 MySQL/Aurora 3306 포트 추가

![image](https://github.com/user-attachments/assets/4199b689-e357-4793-b321-45f53428f712)
![image](https://github.com/user-attachments/assets/feffca22-9ee3-443e-850d-bea7e47c7f82)


## 수동 배포를 위한 이미지 푸시
### 빌드 - jar 파일 생성
```
./gradlew clean build
```

### Docker 이미지 빌드
```docker
docker build --platform linux/amd64 -t [도커아이디]/[리포지토리명] .
```

```docker
docker build --platform linux/amd64 -t [도커아이디]/spring-daangn:v1.0.0 .
```

### Docker hub 이미지 푸시
```docker
docker push [도커아이디]/[리포지토리명]
```

```docker
docker push [도커아이디]/spring-daangn:v1.0.0
```

![image](https://github.com/user-attachments/assets/cf7bce8d-77da-4480-b39a-d0dd916c2d47)

## EC2의 배포 설정
### 키 파일 권한 설정 및 SSH 접속
```
chmod 400 my-key-pair.pem
ssh -i my-key-pair.pem ubuntu@[Public IPv4 주소]
```

### 스왑 메모리 설정
1. 루트 파일 시스템에 Swap 파일을 생성
```
sudo dd if=/dev/zero of=/swapfile bs=128M count=16
```

2. Swap 파일에 읽기 및 쓰기 권한을 부여(600 ➝ r, w)
```
sudo chmod 600 /swapfile
```

3. 리눅스 Swap 영역 설정
```
sudo mkswap /swapfile
```

4. Swap 공간에 Swap 파일 설정
```
sudo swapon /swapfile
```

5. 부팅 시 Swap 파일 활성화 설정
```
echo '/swapfile swap swap defaults 0 0' | sudo tee -a /etc/fstab
```

6. Swap 메모리 확인
```
free -m
```

![image](https://github.com/user-attachments/assets/8d7e8f3d-b03c-4611-97ff-ca6c5994bb3b)
- Swap 메모리 생성 확인

## EC2에서 Docker 배포
### 사용자 네트워크(bridge) 생성
```
docker network create daangn-net
```
- MySQL과 Spring 컨테이너를 같은 네트워크에 묶기

### 환경변수 파일(.env) 생성
```
cat <<EOF > ~/spring-daangn.env
SPRING_DATASOURCE_URL=jdbc:mysql://daangn-mysql:3306/daangn?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul
SPRING_DATASOURCE_USERNAME=ssafy
SPRING_DATASOURCE_PASSWORD=ssafy
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true

JWT_SECRET=IFTHEWORLDWASENDINGIDWANNABENEXTTOYOU
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=1209600000
EOF
```

### MySQL 공식 이미지 풀
```
sudo docker pull mysql
```

### Spring 이미지 풀
```
sudo docker pull [도커아이디]/spring-daangn:v1.0.0
```

### MySQL 컨테이너 실행
```
docker run -d \
  --name daangn-mysql \
  --network daangn-net \
  -e MYSQL_ROOT_PASSWORD=0307 \
  -e MYSQL_DATABASE=daangn \
  -e MYSQL_USER=ssafy \
  -e MYSQL_PASSWORD=ssafy \
  mysql:latest
```

### Spring Boot 컨테이너 실행
```
docker run -d \
  --name daangn-app \
  --network daangn-net \
  --env-file ~/spring-daangn.env \
  -p 80:8080 \
  nodb00/spring-daangn:v1.0.0
```
- 컨테이너 실행 시 `--env-file` 로 환경변수 주입
- env-file + 네트워크 + 포트 매핑

### 배포 확인
실행 중 컨테이너 확인
```
docker ps
```

![image](https://github.com/user-attachments/assets/09bbce9d-0680-485c-b952-a5a7d847a03d)

로그 확인
```
docker logs -f daangn-app
```

## Swagger UI 접근
[Swagger 접속 링크](http://43.200.181.113/swagger-ui/index.html)

![image](https://github.com/user-attachments/assets/defebaf5-52c8-41af-9ccc-55481eb6298c)


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

- 이외에는 형준이형 자료에 너무 자세하게 나와있어서 그대로 진행하니 막힘없이 진행되었다...
- 배포가 이렇게까지 잘 준비되어있다니! 한 학기 과정보다도 더 값진 경험이었다 :)
- 🥰👍
