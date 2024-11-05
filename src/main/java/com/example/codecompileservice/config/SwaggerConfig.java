package com.example.codecompileservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("코딩테스트 API")
                .description("1번문제: https://www.acmicpc.net/problem/1152 2번문제: https://www.acmicpc.net/problem/10989" +
                        " 3번문제: https://www.acmicpc.net/problem/2606  4번문제: https://www.acmicpc.net/problem/10815" +
                        " 5번문제: https://www.acmicpc.net/problem/1753")
                .version("1.0.1");
    }
}