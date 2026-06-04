package com.diego.odontodesk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    // Lê a URL do frontend do application.properties
    // Em dev: http://localhost:4200
    // Em prod: https://...

    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        // Só aceita requisições dessa origem
        config.setAllowedOrigins(List.of(frontendUrl));

        // Método HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers permitidos nas requisições
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // Permite o browser enviar cookies e headers de autenticação
        config.setAllowCredentials(true);

        // Aplica essa configuração para todas as rotas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
