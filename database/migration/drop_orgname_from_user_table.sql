-- ============================================================
-- 数据库清理脚本：删除 t_sys_user 表的 org_name 字段
--
-- 功能说明：
--   1. 验证 org_id 字段数据完整性（确保所有用户都有正确的 org_id）
--   2. 备份当前表结构（创建备份表）
--   3. 删除废弃的 org_name 字段
--   4. 更新相关注释
--   5. 验证最终结果
--
-- 前置条件：
--   - 已成功执行 add_org_id_to_user_table.sql 脚本
--   - 所有用户的 org_id 已正确填充
--
-- 影响范围：
--   - t_sys_user 表（删除 org_name 列）
--   - 可能影响使用 org_name 的查询或应用逻辑
--
-- 执行环境：PostgreSQL
-- 作者：系统自动生成
-- 创建时间：2026-05-03
-- 版本：v1.0
-- ============================================================


-- ============================================================
-- 第一步：前置验证 - 检查 org_id 数据完整性
-- ============================================================

-- 1.1 统计当前数据状态
SELECT
    '=== 前置验证 ===' AS step,
    COUNT(*) AS total_users,
    COUNT(org_id) AS users_with_org_id,
    COUNT(*) - COUNT(org_id) AS users_without_org_id,
    COUNT(org_name) AS users_with_org_name
FROM public.t_sys_user;

-- 预期输出：
-- step           | total_users | users_with_org_id | users_without_org_id | users_with_org_name
-- ---------------+-------------+-------------------+---------------------+-----------------
-- === 前置验证 === |          31 |                29 |                    2 |                 29

-- 1.2 检查是否有用户只有 org_name 但没有 org_id（这些是问题数据）
SELECT
    id AS user_id,
    username,
    org_name,
    org_id,
    CASE
        WHEN org_id IS NOT NULL THEN '✅ 正常'
        WHEN org_name IS NULL THEN '✅ 无组织（可接受）'
        ELSE '❌ 有名称但无ID（需处理）'
    END AS status
FROM public.t_sys_user
WHERE org_id IS NULL AND org_name IS NOT NULL;

-- 预期输出：（应该为空，表示没有问题数据）
-- 如果有数据，请先处理这些问题用户再继续！


-- ============================================================
-- 第二步：备份数据（重要！）
-- ============================================================

-- 2.1 创建包含 org_name 的完整备份表（用于回滚）
CREATE TABLE IF NOT EXISTS t_sys_user_backup_before_drop_orgname_20260503 AS
SELECT * FROM public.t_sys_user;

-- 2.2 验证备份成功
SELECT
    't_sys_user_backup_before_drop_orgname_20260503' AS backup_table,
    COUNT(*) AS total_records,
    COUNT(org_name) AS records_with_orgname_preserved,
    COUNT(org_id) AS records_with_orgid_preserved
FROM t_sys_user_backup_before_drop_orgname_20260503;

-- 输出示例：
-- backup_table                              | total_records | records_with_orgname_preserved | records_with_orgid_preserved
-- ------------------------------------------+---------------+-------------------------------|-----------------------------
-- t_sys_user_backup_before_drop_orgname_20260503 |            31 |                            29 |                          29


-- ============================================================
-- 第三步：DDL - 删除 org_name 字段
-- ============================================================

-- 3.1 删除 org_name 列
ALTER TABLE public.t_sys_user
DROP COLUMN IF EXISTS org_name;

-- 3.2 验证字段已删除
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 't_sys_user'
ORDER BY ordinal_position;

-- 预期输出（org_name 应该不存在了）：
-- column_name   | data_type      | is_nullable
-- -------------+---------------+------------
-- id           | bigint         | NO
-- username     | varchar        | NO
-- email        | varchar        | NO
-- status       | integer        | NO
-- role         | varchar        | YES
-- password     | varchar        | NO
-- created_time| timestamp      | YES
-- update_time | timestamp      | YES
-- update_user_id | bigint      | YES
-- create_user_id | bigint      | YES
-- org_id       | bigint         | YES  ← 这个保留


-- ============================================================
-- 第四步：更新注释（移除 org_name 相关注释）
-- ============================================================

-- 4.1 确认表注释
COMMENT ON TABLE public.t_sys_user IS '系统用户表，存储系统所有用户的基本信息';

-- 4.2 确认各列注释（org_name 的注释已随列一起删除）
COMMENT ON COLUMN public.t_sys_user.id IS '主键ID，自增序列';
COMMENT ON COLUMN public.tsys_user.username IS '用户名，唯一标识，用于登录';
COMMENT ON COLUMN public.t_sys_user.email IS '电子邮箱';
COMMENT ON COLUMN public.t_sys_user.status IS '用户状态：1-正常，0-禁用';
COMMENT ON COLUMN public.t_sys_user."role" IS '角色编码，关联角色表(如 ROLE_ADMIN / ROLE_USER)';
-- COMMENT ON COLUMN public.t_sys_user.org_name IS '所属组织名称，关联组织表';  -- 已删除
COMMENT ON COLUMN public.t_sys_user.created_time IS '创建时间';
COMMENT ON COLUMN public.t_sys_user.update_time IS '最后更新时间';
COMMENT ON COLUMN public.t_sys_user.update_user_id IS '最后更新人ID，关联 t_sys_user.id';
COMMENT ON COLUMN public.t_sys_user.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN public.t_sys_user."password" IS '登录密码，BCrypt加密存储';
COMMENT ON COLUMN public.t_sys_user.org_id IS '所属组织ID，关联 t_sys_org.id';


-- ============================================================
-- 第五步：最终验证
-- ============================================================

-- 5.1 显示当前表结构
\d public.t_sys_user

-- 5.2 统计最终数据状态
SELECT
    '=== 最终验证 ===' AS step,
    COUNT(*) AS total_users,
    COUNT(org_id) AS users_with_org_id,
    COUNT(*) - COUNT(org_id) AS users_without_org_id
FROM public.t_sys_user;

-- 预期输出：
-- step          | total_users | users_with_org_id | users_without_org_id
-- --------------+-------------+-------------------+---------------------
-- === 最终验证 === |          31 |               29 |                   2

-- 5.3 显示部分样本数据（确认 org_id 存在且 org_name 已删除）
SELECT
    id AS user_id,
    username,
    email,
    status,
    role,
    org_id,
    created_time
FROM public.t_sys_user
LIMIT 10;

-- 预期输出示例：
-- user_id | username | email             | status | role      | org_id | created_time
-- --------+----------+------------------+ -------+-----------+-------+---------------------
--       1 | ROLE_ADMIN| admin@example.com |      1 | ROLE_ADMIN|     1 | 2026-04-20 20:22:56
--       2 | ceo      | ceo@example.com   |      1 | ROLE_ADMIN|     1 | 2026-04-20 20:22:56
--       3 | cto      | cto@example.com   |      1 | ROLE_ADMIN|     2 | 2026-04-20 20:22:56
-- ...

-- 5.4 完整性报告
WITH stats AS (
    SELECT
        COUNT(*) AS total_users,
        COUNT(org_id) AS with_org_id
    FROM public.t_sys_user
)
SELECT
    '========================================' AS separator,
    '数据库清理完成报告' AS title,
    '========================================' AS separator2,
    '' AS blank1,
    '操作内容：' AS operation_title,
    '  ✅ 删除字段：org_name (varchar(100))' AS deleted_field,
    '  ✅ 保留字段：org_id (bigint)' AS kept_field,
    '' AS blank2,
    '数据统计：' AS stats_title,
    CONCAT('  总用户数：', CAST(total_users AS VARCHAR)) AS total,
    CONCAT('  有组织的用户：', CAST(with_org_id AS VARCHAR)) AS with_org,
    CONCAT('  无组织的用户：', CAST(total_users - with_org_id AS VARCHAR)) AS without_org,
    '' AS blank3,
    '索引状态：' AS index_title,
    CASE
        WHEN count(*) > 0 THEN '  ✅ idx_user_org_id 索引存在'
        ELSE '  ❌ idx_user_org_id 索引缺失'
    END AS index_status,
    '' AS blank4,
    '迁移状态：' AS migration_status,
    CASE
        WHEN total_users - with_org_id <= 2 THEN '  ✅ 清理成功'
        ELSE '  ⚠️ 请检查未分配组织的用户'
    END AS final_status,
    '========================================' AS separator3
FROM stats,
(SELECT count(*) FROM pg_indexes WHERE tablename = 't_sys_user' AND indexname = 'idx_user_org_id') idx_check;


-- ============================================================
-- 回滚方案（如果需要恢复 org_name 字段）
-- ============================================================

-- 注意：回滚前请确保已备份！

-- 步骤1：重新添加 org_name 字段
-- ALTER TABLE public.t_sys_user
-- ADD COLUMN IF NOT EXISTS org_name varchar(100) NULL;
-- COMMENT ON COLUMN public.t_sys_user.org_name IS '所属组织名称，关联组织表';

-- 步骤2：从备份表恢复 org_name 数据
-- UPDATE public.t_sys_user u
-- SET org_name = b.org_name
-- FROM t_sys_user_backup_before_drop_orgname_20260503 b
-- WHERE u.id = b.id;

-- 步骤3：验证恢复结果
-- SELECT id, username, org_name, org_id FROM public.t_sys_user LIMIT 10;


-- ============================================================
-- 脚本执行完毕
-- ============================================================

-- 下一步操作建议：
-- 1. 确认上述所有 SQL 执行无报错
-- 2. 检查"最终验证"部分的输出是否符合预期
-- 3. 如果一切正常，可以通知前端/后端开发人员：
--    - org_name 字段已删除
--    - 后续统一使用 org_id 关联组织
--    - 需要检查并更新所有引用 org_name 的代码
-- 4. 重启后端服务进行集成测试

-- 联系方式：如有问题请联系开发团队
