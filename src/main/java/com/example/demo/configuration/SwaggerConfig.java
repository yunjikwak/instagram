package com.example.demo.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .version("v1")
                        .title("instagram 데모 서비스 API")
                )
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("로컬 서버"),
                        new Server().url("https://dev-api.instagram.com").description("개발 서버"),
                        new Server().url("https://api.instagram.com").description("운영 서버")
                ));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("유저 API")
                .pathsToMatch("/api/v1/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi postApi() {
        return GroupedOpenApi.builder()
                .group("게시물 API")
                .pathsToMatch("/api/v1/posts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi commentApi() {
        return GroupedOpenApi.builder()
                .group("댓글 API")
                .pathsToMatch("/api/v1/comments/**")
                .build();
    }
}