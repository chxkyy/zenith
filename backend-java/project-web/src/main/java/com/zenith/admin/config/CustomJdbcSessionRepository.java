package com.zenith.admin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlProvider;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.session.*;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
public class CustomJdbcSessionRepository implements FindByIndexNameSessionRepository<CustomJdbcSessionRepository.CustomSession> {

    private static final String TABLE_NAME = "t_sys_session";

    private static final String CREATE_SESSION_QUERY = """
            INSERT INTO %s (SESSION_ID, CREATION_TIME, LAST_ACCESS_TIME, MAX_INACTIVE_INTERVAL, EXPIRY_TIME, PRINCIPAL_NAME)
            VALUES (?, ?, ?, ?, ?, ?)
            """.formatted(TABLE_NAME);

    private static final String GET_SESSION_QUERY = """
            SELECT PRIMARY_ID, SESSION_ID, CREATION_TIME, LAST_ACCESS_TIME, MAX_INACTIVE_INTERVAL, EXPIRY_TIME, PRINCIPAL_NAME
            FROM %s WHERE SESSION_ID = ?
            """.formatted(TABLE_NAME);

    private static final String UPDATE_SESSION_QUERY = """
            UPDATE %s SET SESSION_ID = ?, LAST_ACCESS_TIME = ?, MAX_INACTIVE_INTERVAL = ?, EXPIRY_TIME = ?, PRINCIPAL_NAME = ?
            WHERE PRIMARY_ID = ?
            """.formatted(TABLE_NAME);

    private static final String DELETE_SESSION_QUERY = """
            DELETE FROM %s WHERE SESSION_ID = ?
            """.formatted(TABLE_NAME);

    private static final String LIST_SESSION_IDS_QUERY = """
            SELECT SESSION_ID FROM %s WHERE EXPIRY_TIME >= ?
            """.formatted(TABLE_NAME);

    private static final String LIST_SESSION_IDS_BY_PRINCIPAL_QUERY = """
            SELECT SESSION_ID FROM %s WHERE PRINCIPAL_NAME = ? AND EXPIRY_TIME >= ?
            """.formatted(TABLE_NAME);

    private static final String DELETE_SESSION_ATTRIBUTES_QUERY = """
            DELETE FROM %s_ATTRIBUTES WHERE SESSION_PRIMARY_ID = ?
            """.formatted(TABLE_NAME);

    private static final String GET_SESSION_ATTRIBUTES_QUERY = """
            SELECT ATTRIBUTE_NAME, ATTRIBUTE_BYTES FROM %s_ATTRIBUTES WHERE SESSION_PRIMARY_ID = ?
            """.formatted(TABLE_NAME);

    private static final String INSERT_SESSION_ATTRIBUTE_QUERY = """
            INSERT INTO %s_ATTRIBUTES (SESSION_PRIMARY_ID, ATTRIBUTE_NAME, ATTRIBUTE_BYTES)
            VALUES (?, ?, ?)
            ON CONFLICT (SESSION_PRIMARY_ID, ATTRIBUTE_NAME) DO UPDATE SET ATTRIBUTE_BYTES = EXCLUDED.ATTRIBUTE_BYTES
            """.formatted(TABLE_NAME);

    private static final String DELETE_SESSION_ATTRIBUTE_QUERY = """
            DELETE FROM %s_ATTRIBUTES WHERE SESSION_PRIMARY_ID = ? AND ATTRIBUTE_NAME = ?
            """.formatted(TABLE_NAME);

    private static final String CLEANUP_EXPIRED_SESSIONS_QUERY = """
            DELETE FROM %s WHERE EXPIRY_TIME < ?
            """.formatted(TABLE_NAME);

    private final JdbcTemplate jdbcTemplate;
    private final TransactionOperations transactionOperations;
    private final SessionIdGenerator sessionIdGenerator;
    private Duration defaultMaxInactiveInterval = Duration.ofMinutes(30);

    public CustomJdbcSessionRepository(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, SessionIdGenerator sessionIdGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionOperations = transactionTemplate;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    public void setDefaultMaxInactiveInterval(Duration defaultMaxInactiveInterval) {
        this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
    }

    @Override
    public CustomSession createSession() {
        String sessionId = sessionIdGenerator.generate();
        Instant now = Instant.now();
        Duration interval = defaultMaxInactiveInterval;
        return new CustomSession(sessionId, now, now, interval, null, null);
    }

    @Override
    public void save(CustomSession session) {
        transactionOperations.executeWithoutResult(status -> {
            if (session.getPrimaryId() == null) {
                insertSession(session);
            } else {
                updateSession(session);
            }
        });
    }

    private void insertSession(CustomSession session) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_SESSION_QUERY, new String[]{"primary_id"});
            ps.setString(1, session.getId());
            ps.setLong(2, session.getCreationTime().toEpochMilli());
            ps.setLong(3, session.getLastAccessedTime().toEpochMilli());
            ps.setInt(4, (int) session.getMaxInactiveInterval().getSeconds());
            ps.setLong(5, session.getExpiryTime().toEpochMilli());
            ps.setString(6, session.getPrincipalName());
            return ps;
        }, keyHolder);

        Long primaryId = ((Number) keyHolder.getKeys().get("primary_id")).longValue();
        session.setPrimaryId(primaryId);

        saveAttributes(session);
    }

    private void updateSession(CustomSession session) {
        jdbcTemplate.update(UPDATE_SESSION_QUERY,
                session.getId(),
                session.getLastAccessedTime().toEpochMilli(),
                (int) session.getMaxInactiveInterval().getSeconds(),
                session.getExpiryTime().toEpochMilli(),
                session.getPrincipalName(),
                session.getPrimaryId());
        
        saveAttributes(session);
    }

    private void saveAttributes(CustomSession session) {
        for (Map.Entry<String, Object> entry : session.attributes.entrySet()) {
            try {
                byte[] bytes = serializeAttribute(entry.getValue());
                jdbcTemplate.update(INSERT_SESSION_ATTRIBUTE_QUERY, session.getPrimaryId(), entry.getKey(), bytes);
            } catch (Exception e) {
                log.warn("Failed to save session attribute: {}", entry.getKey(), e);
            }
        }
    }

    private byte[] serializeAttribute(Object value) {
        try {
            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
            oos.writeObject(value);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize session attribute", e);
        }
    }

    private Object deserializeAttribute(byte[] bytes) {
        try {
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bytes);
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            log.warn("Failed to deserialize session attribute", e);
            return null;
        }
    }

    @Override
    public CustomSession findById(String sessionId) {
        List<CustomSession> sessions = jdbcTemplate.query(GET_SESSION_QUERY, (rs, rowNum) -> {
            Long primaryId = rs.getLong("PRIMARY_ID");
            String sid = rs.getString("SESSION_ID");
            Instant creationTime = Instant.ofEpochMilli(rs.getLong("CREATION_TIME"));
            Instant lastAccessTime = Instant.ofEpochMilli(rs.getLong("LAST_ACCESS_TIME"));
            Duration interval = Duration.ofSeconds(rs.getInt("MAX_INACTIVE_INTERVAL"));
            Instant expiryTime = Instant.ofEpochMilli(rs.getLong("EXPIRY_TIME"));
            String principalName = rs.getString("PRINCIPAL_NAME");
            
            CustomSession session = new CustomSession(sid, creationTime, lastAccessTime, interval, expiryTime, principalName);
            session.setPrimaryId(primaryId);
            return session;
        }, sessionId);
        
        if (sessions.isEmpty()) {
            return null;
        }
        
        CustomSession session = sessions.get(0);
        
        if (session.isExpired()) {
            deleteById(sessionId);
            return null;
        }
        
        loadAttributes(session);
        return session;
    }

    private void loadAttributes(CustomSession session) {
        jdbcTemplate.query(GET_SESSION_ATTRIBUTES_QUERY, rs -> {
            while (rs.next()) {
                String attrName = rs.getString("ATTRIBUTE_NAME");
                byte[] attrBytes = rs.getBytes("ATTRIBUTE_BYTES");
                Object attrValue = deserializeAttribute(attrBytes);
                if (attrValue != null) {
                    session.attributes.put(attrName, attrValue);
                }
            }
            return null;
        }, session.getPrimaryId());
    }

    @Override
    public void deleteById(String sessionId) {
        transactionOperations.executeWithoutResult(status -> {
            CustomSession session = findById(sessionId);
            if (session != null && session.getPrimaryId() != null) {
                jdbcTemplate.update(DELETE_SESSION_ATTRIBUTES_QUERY, session.getPrimaryId());
            }
            jdbcTemplate.update(DELETE_SESSION_QUERY, sessionId);
        });
    }

    @Override
    public Map<String, CustomSession> findByIndexNameAndIndexValue(String indexName, String indexValue) {
        if (!PRINCIPAL_NAME_INDEX_NAME.equals(indexName)) {
            return Collections.emptyMap();
        }
        
        List<String> sessionIds = jdbcTemplate.queryForList(LIST_SESSION_IDS_BY_PRINCIPAL_QUERY, String.class, indexValue, Instant.now().toEpochMilli());
        
        Map<String, CustomSession> result = new HashMap<>();
        for (String sessionId : sessionIds) {
            CustomSession session = findById(sessionId);
            if (session != null) {
                result.put(sessionId, session);
            }
        }
        return result;
    }

    public List<String> findAllActiveSessionIds() {
        return jdbcTemplate.queryForList(LIST_SESSION_IDS_QUERY, String.class, Instant.now().toEpochMilli());
    }

    public void cleanupExpiredSessions() {
        jdbcTemplate.update(CLEANUP_EXPIRED_SESSIONS_QUERY, Instant.now().toEpochMilli());
    }

    public class CustomSession implements Session {

        private final String sessionId;
        private final Instant creationTime;
        private Instant lastAccessedTime;
        private Duration maxInactiveInterval;
        private Instant expiryTime;
        private String principalName;
        private Long primaryId;
        private final Map<String, Object> attributes = new HashMap<>();
        private boolean changed = false;

        public CustomSession(String sessionId, Instant creationTime, Instant lastAccessedTime, Duration maxInactiveInterval, Instant expiryTime, String principalName) {
            this.sessionId = sessionId;
            this.creationTime = creationTime;
            this.lastAccessedTime = lastAccessedTime;
            this.maxInactiveInterval = maxInactiveInterval;
            this.expiryTime = expiryTime != null ? expiryTime : calculateExpiryTime();
            this.principalName = principalName;
        }

        private Instant calculateExpiryTime() {
            return lastAccessedTime.plus(maxInactiveInterval);
        }

        @Override
        public String getId() {
            return sessionId;
        }

        @Override
        public String changeSessionId() {
            String newSessionId = sessionIdGenerator.generate();
            return newSessionId;
        }

        @Override
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
            this.expiryTime = calculateExpiryTime();
        }

        @Override
        public Instant getLastAccessedTime() {
            return lastAccessedTime;
        }

        @Override
        public void setMaxInactiveInterval(Duration interval) {
            this.maxInactiveInterval = interval;
            this.expiryTime = calculateExpiryTime();
        }

        @Override
        public Duration getMaxInactiveInterval() {
            return maxInactiveInterval;
        }

        @Override
        public boolean isExpired() {
            return expiryTime != null && Instant.now().isAfter(expiryTime);
        }

        public Long getPrimaryId() {
            return primaryId;
        }

        public void setPrimaryId(Long primaryId) {
            this.primaryId = primaryId;
        }

        public Instant getExpiryTime() {
            return expiryTime;
        }

        public String getPrincipalName() {
            return principalName;
        }

        public void setPrincipalName(String principalName) {
            this.principalName = principalName;
        }

        public boolean isChanged() {
            return changed;
        }
    }
}
