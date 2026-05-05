package com.zenith.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.SessionIdGenerator;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
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
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("ZENITH_TOKEN");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setSameSite("Lax");
        serializer.setCookieMaxAge(1800);
        serializer.setUseSecureCookie(false);
//        serializer.setUseSecureFlag(false);
        serializer.setDomainNamePattern(".*");
//        serializer.setPath("/");
        serializer.setCookiePath("/");
        return serializer;
    }

    @Bean
    public SessionRepositoryFilter<?> sessionRepositoryFilter(CustomJdbcSessionRepository sessionRepository, CookieSerializer cookieSerializer) {
        SessionRepositoryFilter<CustomJdbcSessionRepository.CustomSession> filter = new SessionRepositoryFilter<>(sessionRepository);
        CookieHttpSessionIdResolver sessionIdResolver = new CookieHttpSessionIdResolver();
        sessionIdResolver.setCookieSerializer(cookieSerializer);
        filter.setHttpSessionIdResolver(sessionIdResolver);
        return filter;
    }
}
