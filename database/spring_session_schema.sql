-- ============================================
-- Spring Session JDBC 自定义表结构
-- 表名：t_sys_session（主表）+ t_sys_session_ATTRIBUTES（属性表）
-- PRIMARY_ID: BIGSERIAL 自增
-- SESSION_ID: VARCHAR(32) UUID去横线
--
-- 注意：自定义 SessionRepository 支持数据库自增主键
-- ============================================

-- 1. 会话主表
CREATE TABLE IF NOT EXISTS t_sys_session (
    PRIMARY_ID            BIGSERIAL NOT NULL,
    SESSION_ID            VARCHAR(32) NOT NULL,
    CREATION_TIME         BIGINT NOT NULL,
    LAST_ACCESS_TIME      BIGINT NOT NULL,
    MAX_INACTIVE_INTERVAL INT NOT NULL,
    EXPIRY_TIME           BIGINT NOT NULL,
    PRINCIPAL_NAME        VARCHAR(100),

    CONSTRAINT t_sys_session_pk PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX IF NOT EXISTS t_sys_session_ix1 ON t_sys_session (SESSION_ID);
CREATE INDEX IF NOT EXISTS t_sys_session_ix2 ON t_sys_session (EXPIRY_TIME);
CREATE INDEX IF NOT EXISTS t_sys_session_ix3 ON t_sys_session (PRINCIPAL_NAME);

COMMENT ON TABLE t_sys_session IS 'Spring Session 会话主表，存储用户登录会话信息';
COMMENT ON COLUMN t_sys_session.PRIMARY_ID IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_session.SESSION_ID IS '会话ID（UUID去横线32位），即Token/Cookie值';
COMMENT ON COLUMN t_sys_session.CREATION_TIME IS '创建时间戳（毫秒）';
COMMENT ON COLUMN t_sys_session.LAST_ACCESS_TIME IS '最后访问时间戳（毫秒），框架自动刷新';
COMMENT ON COLUMN t_sys_session.MAX_INACTIVE_INTERVAL IS '最大空闲间隔（秒），即超时阈值';
COMMENT ON COLUMN t_sys_session.EXPIRY_TIME IS '过期时间戳 = LAST_ACCESS_TIME + INTERVAL';
COMMENT ON COLUMN t_sys_session.PRINCIPAL_NAME IS '用户标识名（username），用于按用户索引查找';


-- 2. 会话属性表（Spring Session 自动命名规则：{主表名}_ATTRIBUTES）
CREATE TABLE IF NOT EXISTS t_sys_session_ATTRIBUTES (
    SESSION_PRIMARY_ID   BIGINT NOT NULL,
    ATTRIBUTE_NAME       VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES      BYTEA NOT NULL,

    CONSTRAINT t_sys_session_attributes_pk PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
    CONSTRAINT t_sys_session_attributes_fk FOREIGN KEY (SESSION_PRIMARY_ID)
        REFERENCES t_sys_session(PRIMARY_ID) ON DELETE CASCADE
);

COMMENT ON TABLE t_sys_session_ATTRIBUTES IS 'Spring Session 属性表，存储会话中的属性值（userId/username/ip等）';
COMMENT ON COLUMN t_sys_session_ATTRIBUTES.SESSION_PRIMARY_ID IS '关联主表主键（BIGSERIAL自增ID）';
COMMENT ON COLUMN t_sys_session_ATTRIBUTES.ATTRIBUTE_NAME IS '属性名：userId / username / ip / userAgent / loginTime';
COMMENT ON COLUMN t_sys_session_ATTRIBUTES.ATTRIBUTE_BYTES IS '属性值（Java序列化后的字节数组）';
