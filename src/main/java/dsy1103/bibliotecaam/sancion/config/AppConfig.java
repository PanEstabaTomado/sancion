package dsy1103.bibliotecaam.sancion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${prestamo.url}")
    private String prestamoUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(prestamoUrl)   // http://localhost:8085
                .build();
    }

}
