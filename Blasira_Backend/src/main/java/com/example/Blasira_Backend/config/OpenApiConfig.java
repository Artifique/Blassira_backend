package com.example.Blasira_Backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Blasira Backend API",
        version = "1.0",
        description = "Documentation de l'API pour le backend de l'application Blasira.",
        contact = @Contact(
            name = "Support Blasira",
            email = "support@blasira.com"
        ),
        license = @License(
            name = "Licence propriétaire",
            url = "http://www.blasira.com/license"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Serveur de développement local"),
        @Server(url = "https://api.blasira.com", description = "Serveur de production")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Bearer Token pour l'authentification",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // Cette classe peut rester vide
}
