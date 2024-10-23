package br.com.created.connectedbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("br.com.created.connectedbackend.domain.model")
@EnableJpaRepositories("br.com.created.connectedbackend.domain.repository")
@ComponentScan(basePackages = {
        "br.com.created.connectedbackend.infrastructure.config",
        "br.com.created.connectedbackend.infrastructure.security",
        "br.com.created.connectedbackend.infrastructure.security.service",
        "br.com.created.connectedbackend.domain.repository",
        "br.com.created.connectedbackend.domain.service",
        "br.com.created.connectedbackend.api.controller"
})

public class ConnectedBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConnectedBackendApplication.class, args);
    }

}
