-- ============================================================
-- 数据权限控制功能 - 数据库迁移脚本
-- 对应规格: specs/data-permission-control/PRODUCT.md, TECH.md
-- 策略二（OWNER_ORG）所需的数据绑定表
-- ============================================================

-- 1. 创建人员-数据权限绑定表（仅策略二 OWNER_ORG 使用）
-- 记录用户与业务数据实体的负责人关系
CREATE TABLE IF NOT EXISTS t_data_permission (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,               -- 负责人（用户ID），关联 t_sys_user.id
    data_type varchar(50) NOT NULL,         -- 业务数据类型标识（如 customer、product）
    data_id bigint NOT NULL,                -- 业务数据记录的主键ID
    create_user_id bigint,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    update_user_id bigint,
    update_time timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, data_type, data_id)
);

COMMENT ON TABLE t_data_permission IS '人员-数据权限绑定表（策略二 OWNER_ORG 使用）';
COMMENT ON COLUMN t_data_permission.id IS '主键';
COMMENT ON COLUMN t_data_permission.user_id IS '负责人用户ID，关联t_sys_user.id';
COMMENT ON COLUMN t_data_permission.data_type IS '业务数据类型标识（如customer、product）';
COMMENT ON COLUMN t_data_permission.data_id IS '业务数据记录主键ID';
COMMENT ON COLUMN t_data_permission.create_user_id IS '创建人';
COMMENT ON COLUMN t_data_permission.created_time IS '创建时间';
COMMENT ON COLUMN t_data_permission.update_user_id IS '修改人';
COMMENT ON COLUMN t_data_permission.update_time IS '修改时间';

-- 2. 为 t_data_permission 创建索引
-- 满足 PRODUCT.md #50: 确保 (data_type, data_id) 和 (user_id) 有联合索引
CREATE INDEX IF NOT EXISTS idx_dp_data ON t_data_permission(data_type, data_id);
CREATE INDEX IF NOT EXISTS idx_dp_user ON t_data_permission(user_id);
