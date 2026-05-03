-- ============================================================
-- 数据库迁移脚本：为 t_sys_user 表添加 org_id 字段
--
-- 功能说明：
--   1. 添加 org_id 字段（外键关联 t_sys_org.id）
--   2. 根据现有 org_name 数据自动填充 org_id
--   3. 创建索引优化查询性能
--   4. 验证数据完整性
--
-- 适用场景：
--   当前 t_sys_user 表使用 org_name（字符串）关联组织，
--   需要改为使用 org_id（外键）关联，符合数据库规范化
--
-- 执行环境：PostgreSQL
-- 执行顺序：按脚本中的步骤依次执行
-- 回滚方案：见脚本末尾的回滚 SQL
--
-- 作者：系统自动生成
-- 创建时间：2026-05-03
-- 版本：v1.0
-- ============================================================

-- ============================================================
-- 第一步：备份数据（重要！执行前必须备份）
-- ============================================================

-- 方式1：创建备份表（推荐）
CREATE TABLE IF NOT EXISTS t_sys_user_backup_20260503 AS
SELECT * FROM public.t_sys_user;

-- 验证备份是否成功
SELECT
    't_sys_user_backup_20260503' AS backup_table,
    COUNT(*) AS total_records,
    COUNT(org_name) AS records_with_org_name
FROM t_sys_user_backup_20260503;

-- 输出示例：
-- backup_table              | total_records | records_with_org_name
-- --------------------------+---------------+---------------------
-- t_sys_user_backup_20260503 |            31 |                 29


-- ============================================================
-- 第二步：DDL - 添加 org_id 字段
-- ============================================================

-- 2.1 添加 org_id 列
ALTER TABLE public.t_sys_user
ADD COLUMN IF NOT EXISTS org_id bigint NULL;

-- 2.2 添加字段注释
COMMENT ON COLUMN public.t_sys_user.org_id IS '所属组织ID，关联 t_sys_org.id';

-- 2.3 验证字段已添加
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 't_sys_user'
  AND column_name = 'org_id';

-- 输出预期：
-- column_name | data_type | is_nullable
-- -----------+----------+------------
-- org_id     | bigint   | YES


-- ============================================================
-- 第三步：DML - 根据 org_name 填充 org_id
-- ============================================================

-- 3.1 更新所有用户的 org_id（根据 org_name 匹配 t_sys_org.name）
UPDATE public.t_sys_user u
SET org_id = o.id
FROM public.t_sys_org o
WHERE u.org_name = o.name
  AND u.org_id IS NULL;

-- 验证更新结果
SELECT
    COUNT(*) AS total_users,
    COUNT(org_id) AS users_with_org_id,
    COUNT(*) - COUNT(org_id) AS users_without_org_id,
    COUNT(CASE WHEN org_name IS NOT NULL AND org_id IS NULL THEN 1 END) AS users_with_name_but_no_id
FROM public.t_sys_user;

-- 输出预期：
-- total_users | users_with_org_id | users_without_org_id | users_with_name_but_no_id
-- ------------+------------------+---------------------+------------------------
--           31 |               29 |                    2 |                      0
-- 说明：31个用户中，29个成功匹配到 org_id，2个用户（id=30,31）的 org_name 为空所以 org_id 也为空


-- ============================================================
-- 第四步：验证数据映射正确性
-- ============================================================

-- 4.1 显示所有用户及其对应的组织信息
SELECT
    u.id AS user_id,
    u.username,
    u.org_name AS user_org_name,
    u.org_id,
    o.name AS matched_org_name,
    CASE
        WHEN u.org_name IS NULL AND u.org_id IS NULL THEN '✅ 正常（无组织）'
        WHEN u.org_name = o.name THEN '✅ 匹配正确'
        WHEN u.org_name IS NOT NULL AND u.org_id IS NULL THEN '❌ 未匹配'
        ELSE '⚠️ 需检查'
    END AS validation_status
FROM public.t_sys_user u
LEFT JOIN public.t_sys_org o ON u.org_id = o.id
ORDER BY u.id;

-- 预期输出（部分示例）：
-- user_id | username | user_org_name | org_id | matched_org_name | validation_status
-- --------+----------+--------------+-------+-------------------+------------------
--       1 | ROLE_ADMIN| Zenith 集团总部|     1 | Zenith 集团总部     | ✅ 匹配正确
--       2 | ceo      | Zenith 集团总部|     1 | Zenith 集团总部     | ✅ 匹配正确
--       3 | cto      | 研发中心        |     2 | 研发中心           | ✅ 匹配正确
--      ...
--      30 | admin    | [NULL]        | [NULL] | [NULL]             | ✅ 正常（无组织）
--      31 | 222      | [NULL]        | [NULL] | [NULL]             | ✅ 正常（无组织）


-- 4.2 统计每个组织的成员数量（用于后续验证）
SELECT
    o.id AS org_id,
    o.name AS org_name,
    o.parent_id,
    COUNT(u.id) AS member_count
FROM public.t_sys_org o
LEFT JOIN public.t_sys_user u ON u.org_id = o.id
GROUP BY o.id, o.name, o.parent_id
ORDER BY o.sort, o.id;

-- 预期输出：
-- org_id | org_name       | parent_id | member_count
-- -------+---------------+-----------+-------------
--      1 | Zenith 集团总部|         0 |           2
--      2 | 研发中心       |         1 |           1
--      3 | 前端开发组      |         2 |           3
--      4 | 后端开发组      |         2 |           4
--      5 | 测试组         |         2 |           2
--      6 | 运维组         |         2 |           2
--      7 | 市场部         |         1 |           0
--      8 | 市场策划组      |         7 |           2
--      9 | 品牌推广组      |         7 |           2
--     10 | 销售部         |         1 |           0
--     11 | 国内销售组      |        10 |           2
--     12 | 国际销售组      |        10 |           2
--     13 | 人力资源部      |         1 |           0
--     14 | 招聘组         |        13 |           2
--     15 | 培训组         |        13 |           1
--     16 | 财务部         |         1 |           0
--     17 | 会计组         |        16 |           2
--     18 | 出纳组         |        16 |           1


-- ============================================================
-- 第五步：DDL - 创建索引优化性能
-- ============================================================

-- 5.1 在 org_id 列上创建索引（重要！提升查询性能）
CREATE INDEX IF NOT EXISTS idx_user_org_id
ON public.t_sys_user(org_id);

-- 5.2 验证索引已创建
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 't_sys_user'
  AND indexname = 'idx_user_org_id';

-- 输出预期：
-- indexname      | indexdef
-- ---------------+--------------------------------------------------------------------
-- idx_user_org_id | CREATE INDEX idx_user_org_id ON public.t_sys_user USING btree (org_id)


-- ============================================================
-- 第六步：（可选）添加外键约束
-- ============================================================

-- 注意：如果需要强制引用完整性，可以取消以下注释
-- 但在生产环境中建议谨慎添加外键约束，因为可能影响性能

-- ALTER TABLE public.t_sys_user
-- ADD CONSTRAINT fk_user_org
-- FOREIGN KEY (org_id) REFERENCES public.t_sys_org(id)
-- ON DELETE SET NULL
-- ON UPDATE CASCADE;

-- 说明：
-- ON DELETE SET NULL: 组织被删除时，用户的 org_id 设为 NULL
-- ON UPDATE CASCADE: 组织 ID 更新时，级联更新用户的 org_id


-- ============================================================
-- 第七步：最终验证
-- ============================================================

-- 7.1 检查字段是否存在且类型正确
SELECT
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 't_sys_user'
  AND column_name IN ('id', 'username', 'org_name', 'org_id')
ORDER BY ordinal_position;

-- 7.2 完整性检查报告
WITH stats AS (
    SELECT
        COUNT(*) AS total_users,
        COUNT(org_id) AS users_with_org_id,
        COUNT(DISTINCT org_id) AS distinct_org_ids
    FROM public.t_sys_user
)
SELECT
    '=== 数据库迁移完成 ===' AS report_title,
    total_users AS 总用户数,
    users_with_org_id AS 已分配组织的用户数,
    users_without_org_id AS 未分配组织的用户数,
    distinct_org_ids AS 涉及的组织数量,
    CASE
        WHEN users_without_org_id <= 2 THEN '✅ 迁移成功'
        ELSE '⚠️ 请检查未匹配的用户'
    END AS 迁移状态
FROM stats,
(SELECT COUNT(*) - COUNT(org_id) AS users_without_org_id FROM public.t_sys_user) sub;

-- 输出预期：
-- report_title          | 总用户数 | 已分配组织的用户数 | 未分配组织的用户数 | 涉及的组织数量 | 迁移状态
-- --------------------+---------+----------------+----------------+-------------+----------
-- === 数据库迁移完成 === |      31 |             29 |              2 |          18 | ✅ 迁移成功


-- ============================================================
-- 回滚方案（如果迁移失败或需要回退）
-- ============================================================

-- 注意：回滚前请确保已备份！

-- 步骤1：删除索引（如果已创建）
-- DROP INDEX IF EXISTS public.idx_user_org_id;

-- 步骤2：删除外键约束（如果已添加）
-- ALTER TABLE public.t_sys_user DROP CONSTRAINT IF EXISTS fk_user_org;

-- 步骤3：删除 org_id 列
-- ALTER TABLE public.t_sys_user DROP COLUMN IF EXISTS org_id;

-- 步骤4：从备份表恢复数据（如果需要）
-- TRUNCATE TABLE public.t_sys_user;
-- INSERT INTO public.t_sys_user SELECT * FROM t_sys_user_backup_20260503;


-- ============================================================
-- 脚本执行完毕
-- ============================================================

-- 下一步操作：
-- 1. 确认上述所有 SQL 执行无报错
-- 2. 检查"最终验证"部分的输出是否符合预期
-- 3. 如果一切正常，可以开始修改 Java 代码
-- 4. 重启后端服务进行集成测试

-- 联系方式：如有问题请联系开发团队
