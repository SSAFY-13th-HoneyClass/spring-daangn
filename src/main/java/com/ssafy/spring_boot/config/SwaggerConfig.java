package com.ssafy.spring_boot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("http://localhost:8080")))
                .info(new Info()
                        .title("🥕 당근마켓 API")
                        .description("당근마켓 클론 프로젝트 REST API 문서\n\n" +
                                "🔐 JWT 인증 사용법:\n" +
                                "1. POST /api/users/signup으로 회원가입\n" +
                                "2. POST /api/users/login으로 로그인 후 accessToken 복사\n" +
                                "3. 우측 상단 🔒 Authorize 버튼 클릭\n" +
                                "4. 'Bearer {토큰}' 형태로 입력 (Bearer 뒤에 공백 한 칸)\n" +
                                "5. Authorize 후 인증이 필요한 API 테스트 가능")
                        .version("1.0.0"))

                .components(new Components()
                        .addSecuritySchemes(BEARER_TOKEN_PREFIX,
                                new SecurityScheme()
                                        .name(BEARER_TOKEN_PREFIX)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .description("JWT 토큰을 입력하세요. 'Bearer ' 접두사는 자동으로 추가됩니다.")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_TOKEN_PREFIX));
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