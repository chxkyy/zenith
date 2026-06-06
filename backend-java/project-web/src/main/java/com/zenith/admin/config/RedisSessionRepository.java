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
        log.debug("[SESSION] Created new session: {} at {}", sessionId, now);
        return session;
    }

    @Override
    public void save(RedisSession session) {
        if (session.isDeleted()) {
            deleteById(session.getId());
            // 如果 session 被标记删除且之前有原始 ID（changeSessionId 场景），也清理旧 key
            if (session.getOriginalId() != null && !session.getOriginalId().equals(session.getId())) {
                String oldKey = SESSION_KEY_PREFIX + session.getOriginalId();
                redisTemplate.delete(oldKey);
                log.debug("[SESSION] Cleaned up old session key after changeSessionId: {}", session.getOriginalId());
            }
            return;
        }

        // 【修复 Bug 1】从 attributes 同步用户字段到顶层字段
        syncFieldsFromAttributes(session);

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

        Map<String, Object> filteredAttributes = new HashMap<>();
        for (Map.Entry<String, Object> entry : session.getAttributes().entrySet()) {
            if (!entry.getKey().startsWith("SPRING_SECURITY_")) {
                filteredAttributes.put(entry.getKey(), entry.getValue());
            }
        }
        json.put("attributes", filteredAttributes);

        long ttlSeconds = session.getMaxInactiveInterval().getSeconds();
        redisTemplate.opsForValue().set(sessionKey, json.toJSONString(), ttlSeconds, TimeUnit.SECONDS);

        // 【修复 Bug 1 续】现在 userId 能正确获取了，user:sessions Set 可以正常工作
        if (session.getUserId() != null) {
            String userSessionsKey = USER_SESSIONS_KEY_PREFIX + session.getUserId();
            redisTemplate.opsForSet().add(userSessionsKey, session.getId());
            redisTemplate.expire(userSessionsKey, ttlSeconds, TimeUnit.SECONDS);
        }

        // 【修复 Bug 2】如果 session ID 变更过（changeSessionId），删除旧的 Redis key
        if (session.getOriginalId() != null && !session.getOriginalId().equals(session.getId())) {
            String oldKey = SESSION_KEY_PREFIX + session.getOriginalId();
            redisTemplate.delete(oldKey);
            log.info("[SESSION] Cleaned up old session key after changeSessionId: {} -> {}",
                    session.getOriginalId(), session.getId());
            session.setOriginalId(null);
        }

        session.setChanged(false);
        log.debug("[SESSION] Saved session: {} userId:{}", session.getId(), session.getUserId());
    }

    /**
     * 从 attributes map 同步 userId/username/ip/userAgent/loginTime 到顶层字段
     * 解决 AuthController 通过 setAttribute() 存储数据但 save() 读取顶层字段导致的 null 问题
     */
    private void syncFieldsFromAttributes(RedisSession session) {
        Map<String, Object> attrs = session.getAttributes();

        Object userIdAttr = attrs.get("userId");
        if (userIdAttr instanceof Number) {
            session.setUserId(((Number) userIdAttr).longValue());
        }

        Object usernameAttr = attrs.get("username");
        if (usernameAttr instanceof String) {
            session.setUsername((String) usernameAttr);
        }

        Object ipAttr = attrs.get("ip");
        if (ipAttr instanceof String) {
            session.setIp((String) ipAttr);
        }

        Object userAgentAttr = attrs.get("userAgent");
        if (userAgentAttr instanceof String) {
            session.setUserAgent((String) userAgentAttr);
        }

        Object loginTimeAttr = attrs.get("loginTime");
        if (loginTimeAttr instanceof Number) {
            session.setLoginTime(((Number) loginTimeAttr).longValue());
        }
    }

    @Override
    public RedisSession findById(String sessionId) {
        if (isKicked(sessionId)) {
            return null;
        }

        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        String sessionJson = redisTemplate.opsForValue().get(sessionKey);

        if (sessionJson == null) {
            log.debug("[SESSION] Session not found: {}", sessionId);
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
        List<RedisSession> sessions = new ArrayList<>();
        java.util.concurrent.atomic.AtomicInteger expiredCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger kickedCount = new java.util.concurrent.atomic.AtomicInteger(0);
        try (var cursor = redisTemplate.scan(
                org.springframework.data.redis.core.ScanOptions.scanOptions()
                        .match(SESSION_KEY_PREFIX + "*")
                        .count(100)
                        .build())) {
            cursor.forEachRemaining(key -> {
                String sessionJson = redisTemplate.opsForValue().get(key);
                if (sessionJson != null) {
                    try {
                        JSONObject json = JSON.parseObject(sessionJson);
                        RedisSession session = new RedisSession();
                        session.setId(json.getString("id"));
                        session.setCreationTime(parseInstant(json.get("creationTime")));
                        session.setLastAccessedTime(parseInstant(json.get("lastAccessedTime")));
                        session.setMaxInactiveInterval(Duration.ofSeconds(parseLong(json.get("maxInactiveInterval"))));

                        // 从 attributes 读取 userId（因为旧数据的顶层字段可能为 null）
                        session.setUserId(json.getLong("userId"));
                        session.setUsername(json.getString("username"));
                        session.setIp(json.getString("ip"));
                        session.setUserAgent(json.getString("userAgent"));
                        session.setLoginTime(parseLong(json.get("loginTime")));

                        JSONObject attrs = json.getJSONObject("attributes");
                        if (attrs != null) {
                            Map<String, Object> attributes = new HashMap<>();
                            for (String key2 : attrs.keySet()) {
                                attributes.put(key2, attrs.get(key2));
                            }
                            session.setAttributes(attributes);

                            // 兼容旧数据：如果顶层 userId 为空，尝试从 attributes 获取
                            if (session.getUserId() == null) {
                                Object attrUserId = attrs.get("userId");
                                if (attrUserId instanceof Number) {
                                    session.setUserId(((Number) attrUserId).longValue());
                                }
                            }
                            if (session.getUsername() == null) {
                                Object attrUsername = attrs.get("username");
                                if (attrUsername instanceof String) {
                                    session.setUsername((String) attrUsername);
                                }
                            }
                        }

                        if (isKicked(session.getId())) {
                            kickedCount.incrementAndGet();
                            redisTemplate.delete(key);
                            return;
                        }

                        if (session.isExpired()) {
                            expiredCount.incrementAndGet();
                            deleteById(session.getId());
                            return;
                        }

                        sessions.add(session);
                    } catch (Exception e) {
                        log.error("Failed to parse session from key: {}", key, e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("Failed to scan sessions", e);
        }
        if (expiredCount.get() > 0 || kickedCount.get() > 0) {
            log.info("findAllActiveSessions cleaned up {} expired and {} kicked sessions", expiredCount.get(), kickedCount.get());
        }
        return sessions;
    }

    /**
     * 清理 Redis 中所有 session 数据（用于清除孤儿 session）
     */
    public int clearAllSessions() {
        int count = 0;
        try (var cursor = redisTemplate.scan(
                org.springframework.data.redis.core.ScanOptions.scanOptions()
                        .match(SESSION_KEY_PREFIX + "*")
                        .count(500)
                        .build())) {
            List<String> keysToDelete = new ArrayList<>();
            cursor.forEachRemaining(keysToDelete::add);
            if (!keysToDelete.isEmpty()) {
                count = keysToDelete.size();
                redisTemplate.delete(keysToDelete);
                // 同时清理 user:sessions 索引
                Set<String> indexKeys = redisTemplate.keys(USER_SESSIONS_KEY_PREFIX + "*");
                if (indexKeys != null && !indexKeys.isEmpty()) {
                    redisTemplate.delete(indexKeys);
                }
            }
        } catch (Exception e) {
            log.error("Failed to clear sessions", e);
        }
        log.info("Cleared {} orphan sessions from Redis", count);
        return count;
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
        /** 追踪 changeSessionId 前的原始 ID，用于清理旧 Redis key */
        private transient String originalId;
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
            // 【修复 Bug 2】保存原始 ID，以便 save() 时清理旧 key
            this.originalId = this.id;
            String newId = java.util.UUID.randomUUID().toString().replace("-", "");
            this.id = newId;
            this.changed = true;
            log.debug("[SESSION] changeSessionId: {} -> {}", originalId, newId);
            return newId;
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
