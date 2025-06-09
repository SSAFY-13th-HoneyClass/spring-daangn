package com.ssafy.spring_boot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("http://localhost:8080")))
                .info(new Info()
                        .title("🥕 당근마켓 API")
                        .description("당근마켓 클론 프로젝트 REST API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SSAFY 11기")
                                .email("ssafy@example.com")
                                .url("https://www.ssafy.com")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("당근마켓-API")
                .pathsToMatch("/api/**")
                // GlobalExceptionHandler는 스캔에서 제외
                .packagesToExclude("com.ssafy.spring_boot.common.exception")
                .build();
    }
}