package com.example.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("University Management System API")
                        .description("RESTful API for managing students, teachers, courses and enrollments")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("University")
                                .email("admin@university.ua")));
    }
}

