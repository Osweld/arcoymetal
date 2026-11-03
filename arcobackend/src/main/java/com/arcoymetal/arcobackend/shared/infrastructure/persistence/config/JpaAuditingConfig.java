package com.arcoymetal.arcobackend.shared.infrastructure.persistence.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing 
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        // TODO: Implement the actual logic to retrieve the current user ID
        return Optional::empty;
    }

}
