package uz.poster.integration.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080")
                ))
                .info(new Info()
                        .title("Baaza Car App API")
                        .description("API documentation for Baaza Car Marketplace")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .name("bearerAuth")));
    }

    @Bean
    public List<GroupedOpenApi> apis() {
        return List.of(
                GroupedOpenApi.builder()
                        .group("admin")
                        .pathsToMatch("/api/admin/**")
                        .build(),

                GroupedOpenApi.builder()
                        .group("analytics")
                        .pathsToMatch("/api/analytics/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("users")
                        .pathsToMatch("/api/users/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("auth")
                        .pathsToMatch("/api/auth/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("brands")
                        .pathsToMatch("/api/brands/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("categories")
                        .pathsToMatch("/api/categories/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("conversations")
                        .pathsToMatch("/api/conversations/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("exchange-rate")
                        .pathsToMatch("/api/exchange-rate/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("favorites")
                        .pathsToMatch("/api/favorites/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("files")
                        .pathsToMatch("/api/files/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("listings")
                        .pathsToMatch("/api/listings/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("models")
                        .pathsToMatch("/api/models/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("notifications")
                        .pathsToMatch("/api/notifications/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("recommendations")
                        .pathsToMatch("/api/recommendations/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("regions")
                        .pathsToMatch("/api/regions/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("reports")
                        .pathsToMatch("/api/reports/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("saved-searches")
                        .pathsToMatch("/api/saved-searches/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("sync")
                        .pathsToMatch("/api/sync/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("health")
                        .pathsToMatch("/api/health/**")
                        .build()
        );
    }
}
