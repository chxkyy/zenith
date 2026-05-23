-- 删除 t_sys_online_user 表（已改用 spring-session-jdbc 管理会话）
DROP TABLE IF EXISTS t_sys_online_user;

-- 为角色表添加数据范围字段
ALTER TABLE t_sys_role ADD COLUMN IF NOT EXISTS data_scope smallint DEFAULT 1;

COMMENT ON COLUMN t_sys_role.data_scope IS '数据范围：1-全部数据，2-自定义，3-本部门及下级，4-本部门，5-仅本人';

-- 创建角色-组织关联表（用于自定义数据范围）
CREATE TABLE IF NOT EXISTS t_sys_role_org (
    id bigserial PRIMARY KEY,
    role_id bigint NOT NULL,
    org_id bigint NOT NULL,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, org_id)
);

COMMENT ON TABLE t_sys_role_org IS '角色-组织关联表（自定义数据范围）';
COMMENT ON COLUMN t_sys_role_org.role_id IS '角色ID';
COMMENT ON COLUMN t_sys_role_org.org_id IS '组织ID';

-- 删除 MenuDO 中冗余的 permission 字段（可选，保留也可）
-- ALTER TABLE t_sys_menu DROP COLUMN IF EXISTS permission;
