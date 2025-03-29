package com.rober.bank.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(
        info = @Info(
                title = "Bank Service",
                description = "demo application",
                version = "1.0.0",
                contact = @Contact(
                        name = "Roberth Zambrano",
                        url = "https://github.com/juniorDeveloper8/",
                        email = "${spring.mail.username}"
                ),
                license = @License(
                        name = "copyleft"
                )
        ),
        servers = {
                @Server(
                        description = "DEV SERVER",
                        url = "http://localhost:8080"
                )

        },
        security = @SecurityRequirement(
                name = "Security Token"
        )
)
@SecurityScheme(
        name = "Security Token",
        description = "Access Token For My Api",
        type = SecuritySchemeType.HTTP,
        paramName = HttpHeaders.AUTHORIZATION,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}
