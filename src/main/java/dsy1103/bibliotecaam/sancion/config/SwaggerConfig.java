package dsy1103.bibliotecaam.sancion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");
        return new OpenAPI()
                .info(new Info().title("API 2026 Sanciones de la Biblioteca AM")
                        .version("1.0")
                        .description("Documentacion de la API para el sistema de sanciones de la Biblioteca AM"))
                .components(new Components().addSecuritySchemes("bearerAuth",securityScheme))
                .addSecurityItem(securityRequirement);
    }
}