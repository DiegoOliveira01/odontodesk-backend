package com.diego.odontodesk.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        // Nome do scheme de segurança — usado como referência abaixo
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("OdontoDesk API")
                        .description("Sistema de Gerenciamento de Clínica Odontológica")
                        .version("v1.0.0"))

                // Define que a API usa Bearer Token (JWT)
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))

                // Configura o componente de segurança
                // Isso adiciona o botão "Authorize" no Swagger UI
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
