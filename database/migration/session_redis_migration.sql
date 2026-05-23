-- ============================================
-- 会话迁移到 Redis 数据库变更脚本
-- 1. 新增 login_log 表的 user_agent 字段
-- 2. 删除 Spring Session JDBC 相关表
-- ============================================

-- 1. 为登录日志表新增 user_agent 字段
ALTER TABLE t_sys_login_log ADD COLUMN IF NOT EXISTS user_agent VARCHAR(500);

COMMENT ON COLUMN t_sys_login_log.user_agent IS '浏览器/设备信息';

-- 2. 删除 Spring Session JDBC 相关表
DROP TABLE IF EXISTS t_sys_session_ATTRIBUTES;
DROP TABLE IF EXISTS t_sys_session;
