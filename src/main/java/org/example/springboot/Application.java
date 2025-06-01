package org.example.springboot;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "당근마켓 Item API",
        version = "1.0.0",
        description = "Spring Boot 기반 당근마켓 아이템 관리 API",
        contact = @Contact(
            name = "SSAFY 개발팀",
            email = "ssafy@example.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "로컬 개발 서버")
    }
)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}