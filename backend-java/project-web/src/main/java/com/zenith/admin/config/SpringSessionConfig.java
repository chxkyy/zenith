package com.zenith.admin.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.SessionIdGenerator;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.session.web.http.SessionRepositoryFilter;

import java.util.List;
import java.util.UUID;

@Configuration
public class SpringSessionConfig {

    @Bean
    public SessionIdGenerator sessionIdGenerator() {
        return () -> UUID.randomUUID().toString().replace("-", "");
    }

    @Bean
    public RedisSessionRepository sessionRepository(StringRedisTemplate redisTemplate, 
                                                     SessionIdGenerator sessionIdGenerator,
                                                     SessionProperties sessionProperties) {
        return new RedisSessionRepository(redisTemplate, sessionIdGenerator, sessionProperties);
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("ZENITH_TOKEN");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setSameSite("Strict");
        serializer.setCookieMaxAge(-1);
        serializer.setUseSecureCookie(false);
        serializer.setCookiePath("/");
        return serializer;
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver(CookieSerializer cookieSerializer) {
        return new CompositeHttpSessionIdResolver(cookieSerializer);
    }

    @Bean
    public FilterRegistrationBean<SessionRepositoryFilter<?>> sessionRepositoryFilterRegistration(
            RedisSessionRepository sessionRepository,
            HttpSessionIdResolver httpSessionIdResolver) {
        
        SessionRepositoryFilter<RedisSessionRepository.RedisSession> filter = 
            new SessionRepositoryFilter<>(sessionRepository);
        filter.setHttpSessionIdResolver(httpSessionIdResolver);
        
        FilterRegistrationBean<SessionRepositoryFilter<?>> registration = 
            new FilterRegistrationBean<>(filter);
        registration.setOrder(-101);
        registration.addUrlPatterns("/*");
        
        return registration;
    }

    public static class CompositeHttpSessionIdResolver implements HttpSessionIdResolver {

        private final CookieHttpSessionIdResolver cookieResolver;
        private final HeaderHttpSessionIdResolver headerResolver;

        public CompositeHttpSessionIdResolver(CookieSerializer cookieSerializer) {
            this.cookieResolver = new CookieHttpSessionIdResolver();
            this.cookieResolver.setCookieSerializer(cookieSerializer);
            this.headerResolver = HeaderHttpSessionIdResolver.xAuthToken();
        }

        @Override
        public List<String> resolveSessionIds(jakarta.servlet.http.HttpServletRequest request) {
            List<String> sessionIds = cookieResolver.resolveSessionIds(request);
            if (!sessionIds.isEmpty() && !sessionIds.get(0).isEmpty()) {
                return sessionIds;
            }
            return headerResolver.resolveSessionIds(request);
        }

        @Override
        public void setSessionId(jakarta.servlet.http.HttpServletRequest request, 
                                  jakarta.servlet.http.HttpServletResponse response, 
                                  String sessionId) {
            cookieResolver.setSessionId(request, response, sessionId);
            headerResolver.setSessionId(request, response, sessionId);
        }

        @Override
        public void expireSession(jakarta.servlet.http.HttpServletRequest request, 
                                   jakarta.servlet.http.HttpServletResponse response) {
            cookieResolver.expireSession(request, response);
            headerResolver.expireSession(request, response);
        }
    }
}
