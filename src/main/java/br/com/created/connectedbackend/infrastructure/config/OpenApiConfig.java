package br.com.created.connectedbackend.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Connected Backend API")
                        .version("1.0")
                        .description("API RESTful para gerenciamento de leads em eventos")
                        .contact(new Contact()
                                .name("Ediney Mendonça")
                                .email("ediney@created.com.br")));
    }
}
