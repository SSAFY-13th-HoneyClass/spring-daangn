package com.example.daangn.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger OpenAPI 설정 클래스
 * API 문서화를 위한 기본 정보를 설정합니다.
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
                        .description("당근마켓 클론 프로젝트의 REST API 문서")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("DaangnClone Team")
                                .email("contact@daangnclone.com")));
    }
}