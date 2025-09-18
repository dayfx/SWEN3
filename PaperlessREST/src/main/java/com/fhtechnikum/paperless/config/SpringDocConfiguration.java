package com.fhtechnikum.paperless.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {

    @Bean(name = "com.fhtechnikum.paperless.config.SpringDocConfiguration.apiInfo")
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Paperless Document Management REST-Server")
                                .description("DMS")
                                .contact(
                                        new Contact()
                                                .name("SWEN3")
                                                .url("https://www.technikum-wien.at")
                                )
                                .version("1.0.0")
                )
        ;
    }
}