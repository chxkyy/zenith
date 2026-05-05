package com.zenith.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.SessionIdGenerator;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

import java.util.UUID;

@Configuration
@EnableJdbcHttpSession(tableName = "t_sys_session")
public class SpringSessionConfig {

    @Bean
    public SessionIdGenerator sessionIdGenerator() {
        return () -> UUID.randomUUID().toString().replace("-", "");
    }
}
