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
                        .group("auth")
                        .pathsToMatch("/api/auth/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("order")
                        .pathsToMatch("/api/order/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("health")
                        .pathsToMatch("/api/health/**")
                        .build()
        );
    }
}
