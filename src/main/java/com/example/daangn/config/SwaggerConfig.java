package com.example.daangn.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger OpenAPI 설정 클래스
 * API 문서화를 위한 기본 정보를 설정합니다.
 * Spring Boot 3.4.5 + SpringDoc OpenAPI 2.8.5 호환
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 정보를 설정하는 Bean
     * @return OpenAPI 객체
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("당근마켓 클론 API")
                        .description("당근마켓 클론 프로젝트의 REST API 문서입니다. " +
                                "사용자, 게시글, 상품 관련 API를 제공합니다.")
                        .version("v1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                        .contact(new Contact()
                                .name("DaangnClone Team")
                                .email("contact@daangnclone.com")
                                .url("https://github.com/daangn-clone")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버")
                ))
                .tags(List.of(
                        new Tag()
                                .name("User")
                                .description("사용자 관리 API"),
                        new Tag()
                                .name("Post")
                                .description("게시글 관리 API"),
                        new Tag()
                                .name("Product")
                                .description("상품 관리 API")
                ));
    }
}