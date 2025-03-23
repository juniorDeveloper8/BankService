package com.rober.bank.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Bank Service",
                description = "demo application",
                version = "1.0.0",
                contact = @Contact(
                        name = "Roberth Zambrano",
                        url = "https://github.com/juniorDeveloper8/",
                        email = "oscuridadd38@gmail.com"
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
        }
)
public class SwaggerConfig {
}
