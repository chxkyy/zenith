-- ============================================================
-- Zenith Admin System - 菜单拆表迁移脚本
-- 将 t_sys_menu 中 button/field 类型数据拆分到 t_sys_function
-- 兼容: PostgreSQL
-- ============================================================

-- ============================================================
-- 1. 创建 t_sys_function（功能字段表）
-- ============================================================
CREATE TABLE t_sys_function (
    id bigserial PRIMARY KEY,
    menu_id bigint NOT NULL,                          -- 所属菜单ID，关联 t_sys_menu.id
    name varchar(100) NOT NULL,                       -- 功能/字段名称
    type varchar(20) NOT NULL,                        -- 类型：button / field
    permission varchar(100),                          -- 权限标识
    sort int DEFAULT 0,                               -- 排序号
    status int DEFAULT 1,                             -- 状态：1-启用，0-禁用
    created_time timestamp DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    update_time timestamp,                            -- 最后更新时间
    create_user_id bigint,                            -- 创建人ID，关联 t_sys_user.id
    update_user_id bigint                             -- 最后更新人ID，关联 t_sys_user.id
);

-- 添加中文注释
COMMENT ON TABLE t_sys_function IS '功能字段表，存储按钮权限和字段权限';
COMMENT ON COLUMN t_sys_function.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_function.menu_id IS '所属菜单ID，关联 t_sys_menu.id';
COMMENT ON COLUMN t_sys_function.name IS '功能/字段名称';
COMMENT ON COLUMN t_sys_function.type IS '类型：button-按钮权限，field-字段权限';
COMMENT ON COLUMN t_sys_function.permission IS '权限标识（如：YHGL_XZ）';
COMMENT ON COLUMN t_sys_function.sort IS '排序号，数值越小越靠前';
COMMENT ON COLUMN t_sys_function.status IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN t_sys_function.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_function.update_time IS '最后更新时间';
COMMENT ON COLUMN t_sys_function.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_function.update_user_id IS '最后更新人ID，关联 t_sys_user.id';

-- 创建索引
CREATE INDEX idx_t_sys_function_menu_id ON t_sys_function(menu_id);
CREATE INDEX idx_t_sys_function_type ON t_sys_function(type);

-- ============================================================
-- 2. 创建 t_sys_role_function（角色功能关联表）
-- ============================================================
CREATE TABLE t_sys_role_function (
    id bigserial PRIMARY KEY,
    role_id bigint NOT NULL,                          -- 角色ID，关联 t_sys_role.id
    function_id bigint NOT NULL,                      -- 功能ID，关联 t_sys_function.id
    created_time timestamp DEFAULT CURRENT_TIMESTAMP, -- 关联关系创建时间
    UNIQUE(role_id, function_id)
);

-- 添加中文注释
COMMENT ON TABLE t_sys_role_function IS '角色功能关联表，定义角色与功能权限的多对多关系';
COMMENT ON COLUMN t_sys_role_function.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_role_function.role_id IS '角色ID，关联 t_sys_role.id';
COMMENT ON COLUMN t_sys_role_function.function_id IS '功能ID，关联 t_sys_function.id';
COMMENT ON COLUMN t_sys_role_function.created_time IS '关联关系创建时间';

-- ============================================================
-- 3. 数据迁移：从 t_sys_menu 迁移 button/field 数据到 t_sys_function
-- ============================================================
INSERT INTO t_sys_function (id, menu_id, name, type, permission, sort, status, created_time, update_time, create_user_id, update_user_id)
SELECT id, parent_id, name, type, permission, sort, status, created_time, update_time, create_user_id, update_user_id
FROM t_sys_menu
WHERE type IN ('button', 'field');

-- ============================================================
-- 4. 数据迁移：从 t_sys_role_menu 迁移 button/field 的关联到 t_sys_role_function
--    原 t_sys_role_menu 中 menu_id 指向 button/field 记录的，
--    现在需要迁移到 t_sys_role_function 中 function_id
-- ============================================================
INSERT INTO t_sys_role_function (role_id, function_id, created_time)
SELECT rm.role_id, rm.menu_id, rm.created_time
FROM t_sys_role_menu rm
INNER JOIN t_sys_menu m ON rm.menu_id = m.id
WHERE m.type IN ('button', 'field')
ON CONFLICT (role_id, function_id) DO NOTHING;

-- ============================================================
-- 5. 删除 t_sys_role_menu 中关联 button/field 的记录
-- ============================================================
DELETE FROM t_sys_role_menu
WHERE menu_id IN (
    SELECT id FROM t_sys_menu WHERE type IN ('button', 'field')
);

-- ============================================================
-- 6. 删除 t_sys_menu 中 button/field 数据
-- ============================================================
DELETE FROM t_sys_menu WHERE type IN ('button', 'field');

-- ============================================================
-- 7. 更新 t_sys_menu 表注释（不再包含权限标识）
-- ============================================================
COMMENT ON TABLE t_sys_menu IS '系统菜单表，定义前端菜单的目录和菜单结构';

-- ============================================================
-- 迁移完毕
-- ============================================================
