package com.NTFOODS.Api_tanty.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenApiConfig - Configuration de la documentation OpenAPI/Swagger
 * Configure la documentation API pour l'ERP TANTY avec authentification JWT
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure l'instance OpenAPI pour la documentation Swagger
     * Définit les informations générales, serveurs, schémas de sécurité et documentation externe
     * @return Configuration OpenAPI
     */
    @Bean
    public OpenAPI apiTantyOpenAPI() {
        // Schéma de sécurité JWT
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                // Informations générales sur l'API
                .info(new Info()
                        .title("ERP TANTY API")
                        .description("API REST pour le système ERP TANTY. Cette API permet de gérer les utilisateurs, le stock, les commandes, et d'autres fonctionnalités de l'ERP.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Équipe NTFOODS")
                                .email("contact@ntfoods.com")
                                .url("https://www.ntfoods.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                
                // Serveurs disponibles
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Serveur de développement local"),
                        new Server()
                                .url("https://api.tanty.erp")
                                .description("Serveur de production")))
                
                // Documentation externe
                .externalDocs(new ExternalDocumentation()
                        .description("Documentation complète de l'ERP TANTY")
                        .url("https://docs.tanty.erp"))
                
                // Schémas de sécurité
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT d'authentification. Obtenez-le via l'endpoint /api/v1/auth/login")));
    }
}
