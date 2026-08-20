package com.example.busai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI busAiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Bus Seat Finder API")
                        .description("AI-powered natural-language bus search and seat availability backend")
                        .version("1.0.0")
                        .contact(new Contact().name("AI Bus Seat Finder Team")));
    }
}
