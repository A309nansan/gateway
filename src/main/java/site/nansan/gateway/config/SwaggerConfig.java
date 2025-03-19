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

//@Configuration
public class SwaggerConfig {

//    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8000").description("API Gateway")  // Gateway를 통해 모든 API 호출
                ))
                .components(new Components().addSecuritySchemes("Bearer",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
                .info(new Info()
                        .title("Nansan API Gateway")
                        .description("API Gateway를 통한 전체 API 문서 제공")
                        .version("v1"));
    }
}
