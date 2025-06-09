package com.ssafy.daangn_demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Daangn demo API")
                .description("중고거래 플랫폼")
                .version("1.0");

        return new OpenAPI()
                .info(info);
    }
}
