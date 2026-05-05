package com.zenith.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.SessionIdGenerator;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Configuration
public class SpringSessionConfig {

    @Bean
    public SessionIdGenerator sessionIdGenerator() {
        return () -> UUID.randomUUID().toString().replace("-", "");
    }

    @Bean
    public CustomJdbcSessionRepository sessionRepository(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, SessionIdGenerator sessionIdGenerator) {
        CustomJdbcSessionRepository repository = new CustomJdbcSessionRepository(jdbcTemplate, transactionTemplate, sessionIdGenerator);
        repository.setDefaultMaxInactiveInterval(java.time.Duration.ofMinutes(30));
        return repository;
    }

    @Bean
    public SessionRepositoryFilter<?> sessionRepositoryFilter(CustomJdbcSessionRepository sessionRepository) {
        return new SessionRepositoryFilter<>(sessionRepository);
    }
}
