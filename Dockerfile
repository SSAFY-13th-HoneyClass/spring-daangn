FROM openjdk:21
ARG JAR_FILE=/build/libs/spring-daangn-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
#ENV jwt_secret=fjlsflsfieiwofjhwohefnddddfnnled556654eyhjuhytfrdhikdjhlfgogreghdslsodjddddddddddddddddddksososlslslslsls
ENTRYPOINT ["java","-jar", "/app.jar"]