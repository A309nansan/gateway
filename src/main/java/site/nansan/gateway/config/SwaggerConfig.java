package site.nansan.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Gateway 자체가 8000 포트에서 뜨므로, swagger-ui가 표시할 "Server" URL
                .servers(List.of(
                        new Server()
                                .url("https://nansan.site/api/v1/test")
                                .description("API Gateway를 통한 test-service")
                ))
                .components(
                        new Components().addSecuritySchemes("Bearer",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .info(
                        new Info()
                                .title("Nansan API Gateway")
                                .description("API Gateway를 통한 전체 API 문서 제공")
                                .version("v1")
                );
    }
}
