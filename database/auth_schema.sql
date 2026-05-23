-- 为t_sys_user添加password字段
ALTER TABLE t_sys_user ADD COLUMN password varchar(100) NOT NULL DEFAULT '';
-- 初始化admin密码（000000的BCrypt加密）
UPDATE t_sys_user SET password = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW' WHERE username = 'admin';

-- 创建用户角色关联表
CREATE TABLE t_sys_user_role (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

-- 创建角色菜单关联表
CREATE TABLE t_sys_role_menu (
    id bigserial PRIMARY KEY,
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, menu_id)
);

-- 为admin用户分配admin角色（假设admin角色ID为1）
INSERT INTO t_sys_user_role (user_id, role_id) VALUES (1, 1) ON CONFLICT DO NOTHING;

-- 为admin角色分配所有菜单权限（假设菜单ID从1开始）
INSERT INTO t_sys_role_menu (role_id, menu_id) 
SELECT 1, id FROM t_sys_menu ON CONFLICT DO NOTHING;
