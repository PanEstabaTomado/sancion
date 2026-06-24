package dsy1103.bibliotecaam.sancion.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
@Configuration
public class SwaggerConfig {

    @Value("${springdoc.servers[0].url:http://localhost:8098}")
    private String serverUrl;

    @Value("${springdoc.servers[0].description:Via del Api Gateway}")
    private String serverDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");
        return new OpenAPI()
                .info(new Info()
                        .title("API 2026 Sistema de sanciones por la Biblioteca AM")
                        .version("1.0")
                        .description("Gestion de usuarios que no hayan cumplido su plazo de entrega en la Biblioteca AM"))
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description(serverDescription)
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
