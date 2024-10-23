package br.com.created.connectedbackend.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EntityScan(basePackages = {
        "br.com.created.connectedbackend.domain.model"
})
@EnableJpaRepositories(basePackages = {
        "br.com.created.connectedbackend.domain.repository"
})
public class JpaConfig {
    // A configuração é feita principalmente através das anotações
    // Configurações adicionais podem ser adicionadas conforme necessário
}