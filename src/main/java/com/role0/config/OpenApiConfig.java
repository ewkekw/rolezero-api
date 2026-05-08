package com.role0.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Role-Zero API",
        version = "1.0",
        description = "Documentação OpenAPI 3.0 da plataforma Role-Zero. Arquitetura Hexagonal, Microservices Patterns e Segurança Zero-Knowledge.",
        contact = @Contact(name = "Equipe de Engenharia Role-Zero")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Ambiente Local (Desenvolvimento)")
    },
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "Token JWT para acesso aos endpoints privados. Adquira no endpoint /api/v1/auth/sso",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
