package com.zenith.admin.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionIdGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisSessionRepository implements FindByIndexNameSessionRepository<RedisSessionRepository.RedisSession> {

    private static final String SESSION_KEY_PREFIX = "session:";
    private static final String USER_SESSIONS_KEY_PREFIX = "user:sessions:";
    private static final String KICKED_SESSIONS_KEY = "kicked:sessions";

    private final StringRedisTemplate redisTemplate;
    private final SessionIdGenerator sessionIdGenerator;
    private final SessionProperties sessionProperties;

    private Duration defaultMaxInactiveInterval = Duration.ofMinutes(120);

    public RedisSessionRepository(StringRedisTemplate redisTemplate, 
                                   SessionIdGenerator sessionIdGenerator,
                                   SessionProperties sessionProperties) {
        this.redisTemplate = redisTemplate;
        this.sessionIdGenerator = sessionIdGenerator;
        this.sessionProperties = sessionProperties;
        this.defaultMaxInactiveInterval = Duration.ofMinutes(sessionProperties.getTimeoutMinutes());
    }

    @Override
    public RedisSession createSession() {
        String sessionId = sessionIdGenerator.generate();
        Instant now = Instant.now();
        RedisSession session = new RedisSession(sessionId, now, now, defaultMaxInactiveInterval);
        log.debug("Creating new session: {}", sessionId);
        return session;
    }

    @Override
    public void save(RedisSession session) {
        if (session.isDeleted()) {
            deleteById(session.getId());
            return;
        }

        String sessionKey = SESSION_KEY_PREFIX + session.getId();
        
        JSONObject json = new JSONObject();
        json.put("id", session.getId());
        json.put("creationTime", session.getCreationTime().toEpochMilli());
        json.put("lastAccessedTime", session.getLastAccessedTime().toEpochMilli());
        json.put("maxInactiveInterval", session.getMaxInactiveInterval().getSeconds());
        json.put("userId", session.getUserId());
        json.put("username", session.getUsername());
        json.put("ip", session.getIp());
        json.put("userAgent", session.getUserAgent());
        json.put("loginTime", session.getLoginTime());
        json.put("attributes", session.getAttributes());

        long ttlSeconds = session.getMaxInactiveInterval().getSeconds();
        redisTemplate.opsForValue().set(sessionKey, json.toJSONString(), ttlSeconds, TimeUnit.SECONDS);

        if (session.getUserId() != null) {
            String userSessionsKey = USER_SESSIONS_KEY_PREFIX + session.getUserId();
            redisTemplate.opsForSet().add(userSessionsKey, session.getId());
            redisTemplate.expire(userSessionsKey, ttlSeconds, TimeUnit.SECONDS);
        }

        session.setChanged(false);
        log.debug("Saved session: {}", session.getId());
    }

    @Override
    public RedisSession findById(String sessionId) {
        if (isKicked(sessionId)) {
            return null;
        }

        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        String sessionJson = redisTemplate.opsForValue().get(sessionKey);

        if (sessionJson == null) {
            log.debug("Session not found: {}", sessionId);
            return null;
        }

        try {
            JSONObject json = JSON.parseObject(sessionJson);
            RedisSession session = new RedisSession();
            session.setId(json.getString("id"));
            session.setCreationTime(parseInstant(json.get("creationTime")));
            session.setLastAccessedTime(parseInstant(json.get("lastAccessedTime")));
            session.setMaxInactiveInterval(Duration.ofSeconds(parseLong(json.get("maxInactiveInterval"))));
            session.setUserId(json.getLong("userId"));
            session.setUsername(json.getString("username"));
            session.setIp(json.getString("ip"));
            session.setUserAgent(json.getString("userAgent"));
            session.setLoginTime(parseLong(json.get("loginTime")));
            
            JSONObject attrs = json.getJSONObject("attributes");
            if (attrs != null) {
                Map<String, Object> attributes = new HashMap<>();
                for (String key : attrs.keySet()) {
                    attributes.put(key, attrs.get(key));
                }
                session.setAttributes(attributes);
            }
            
            if (session.isExpired()) {
                deleteById(sessionId);
                return null;
            }

            return session;
        } catch (Exception e) {
            log.error("Failed to parse session: {}", sessionId, e);
            return null;
        }
    }

    @Override
    public void deleteById(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        String sessionJson = redisTemplate.opsForValue().get(sessionKey);
        
        if (sessionJson != null) {
            try {
                JSONObject json = JSON.parseObject(sessionJson);
                Long userId = json.getLong("userId");
                if (userId != null) {
                    String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
                    redisTemplate.opsForSet().remove(userSessionsKey, sessionId);
                }
            } catch (Exception e) {
                log.warn("Failed to parse session for deletion: {}", sessionId, e);
            }
        }
        
        redisTemplate.delete(sessionKey);
        log.debug("Deleted session: {}", sessionId);
    }

    @Override
    public Map<String, RedisSession> findByIndexNameAndIndexValue(String indexName, String indexValue) {
        if (!PRINCIPAL_NAME_INDEX_NAME.equals(indexName)) {
            return Collections.emptyMap();
        }

        return findByUserId(Long.parseLong(indexValue));
    }

    public Map<String, RedisSession> findByUserId(Long userId) {
        String userSessionsKey = USER_SESSIONS_KEY_PREFIX + userId;
        Set<String> sessionIds = redisTemplate.opsForSet().members(userSessionsKey);

        if (sessionIds == null || sessionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, RedisSession> result = new HashMap<>();
        for (String sessionId : sessionIds) {
            RedisSession session = findById(sessionId);
            if (session != null) {
                result.put(sessionId, session);
            }
        }
        return result;
    }

    public List<RedisSession> findAllActiveSessions() {
        Set<String> keys = redisTemplate.keys(SESSION_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }

        List<RedisSession> sessions = new ArrayList<>();
        for (String key : keys) {
            String sessionJson = redisTemplate.opsForValue().get(key);
            if (sessionJson != null) {
                try {
                    JSONObject json = JSON.parseObject(sessionJson);
                    RedisSession session = new RedisSession();
                    session.setId(json.getString("id"));
                    session.setCreationTime(parseInstant(json.get("creationTime")));
                    session.setLastAccessedTime(parseInstant(json.get("lastAccessedTime")));
                    session.setMaxInactiveInterval(Duration.ofSeconds(parseLong(json.get("maxInactiveInterval"))));
                    session.setUserId(json.getLong("userId"));
                    session.setUsername(json.getString("username"));
                    session.setIp(json.getString("ip"));
                    session.setUserAgent(json.getString("userAgent"));
                    session.setLoginTime(parseLong(json.get("loginTime")));
                    
                    if (!isKicked(session.getId())) {
                        sessions.add(session);
                    }
                } catch (Exception e) {
                    log.error("Failed to parse session from key: {}", key, e);
                }
            }
        }
        return sessions;
    }

    public void kickSession(String sessionId) {
        redisTemplate.opsForSet().add(KICKED_SESSIONS_KEY, sessionId);
        redisTemplate.expire(KICKED_SESSIONS_KEY, 
            sessionProperties.getKickedRecordExpireMinutes(), TimeUnit.MINUTES);
        
        deleteById(sessionId);
    }

    public boolean isKicked(String sessionId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(KICKED_SESSIONS_KEY, sessionId));
    }

    public void enforceMaxConcurrentSessions(Long userId, String currentSessionId) {
        Map<String, RedisSession> userSessions = findByUserId(userId);
        
        int maxConcurrent = sessionProperties.getMaxConcurrent();
        int currentCount = userSessions.size();
        
        if (currentCount >= maxConcurrent) {
            List<RedisSession> sessionsToKick = userSessions.values().stream()
                .filter(s -> !s.getId().equals(currentSessionId))
                .sorted(Comparator.comparing(RedisSession::getLoginTime))
                .limit(currentCount - maxConcurrent + 1)
                .toList();
            
            for (RedisSession session : sessionsToKick) {
                log.info("Kicking session {} for user {} due to max concurrent limit", 
                    session.getId(), userId);
                kickSession(session.getId());
            }
        }
    }

    private Instant parseInstant(Object value) {
        if (value == null) {
            return Instant.now();
        }
        if (value instanceof Number) {
            return Instant.ofEpochMilli(((Number) value).longValue());
        }
        if (value instanceof String) {
            String str = (String) value;
            try {
                return Instant.parse(str);
            } catch (Exception e) {
                try {
                    return Instant.ofEpochMilli(Long.parseLong(str));
                } catch (Exception ex) {
                    return Instant.now();
                }
            }
        }
        return Instant.now();
    }

    private long parseLong(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    @Data
    public static class RedisSession implements Session {
        private String id;
        private Instant creationTime;
        private Instant lastAccessedTime;
        private Duration maxInactiveInterval;
        private Long userId;
        private String username;
        private String ip;
        private String userAgent;
        private Long loginTime;
        private Map<String, Object> attributes = new HashMap<>();
        private transient boolean changed = false;
        private transient boolean deleted = false;

        public RedisSession() {
        }

        public RedisSession(String id, Instant creationTime, Instant lastAccessedTime, Duration maxInactiveInterval) {
            this.id = id;
            this.creationTime = creationTime;
            this.lastAccessedTime = lastAccessedTime;
            this.maxInactiveInterval = maxInactiveInterval;
            this.loginTime = System.currentTimeMillis();
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String changeSessionId() {
            return id;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAttribute(String attributeName) {
            return (T) attributes.get(attributeName);
        }

        @Override
        public Set<String> getAttributeNames() {
            return Collections.unmodifiableSet(attributes.keySet());
        }

        @Override
        public void setAttribute(String attributeName, Object attributeValue) {
            attributes.put(attributeName, attributeValue);
            changed = true;
        }

        @Override
        public void removeAttribute(String attributeName) {
            attributes.remove(attributeName);
            changed = true;
        }

        @Override
        public Instant getCreationTime() {
            return creationTime;
        }

        @Override
        public void setLastAccessedTime(Instant lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
            changed = true;
        }

        @Override
        public Instant getLastAccessedTime() {
            return lastAccessedTime;
        }

        @Override
        public void setMaxInactiveInterval(Duration interval) {
            this.maxInactiveInterval = interval;
            changed = true;
        }

        @Override
        public Duration getMaxInactiveInterval() {
            return maxInactiveInterval;
        }

        @Override
        public boolean isExpired() {
            return lastAccessedTime != null && 
                   Instant.now().isAfter(lastAccessedTime.plus(maxInactiveInterval));
        }

        public boolean isChanged() {
            return changed;
        }

        public void setChanged(boolean changed) {
            this.changed = changed;
        }

        public boolean isDeleted() {
            return deleted;
        }

        public void markDeleted() {
            this.deleted = true;
        }
    }
}
