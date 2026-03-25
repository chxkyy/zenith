CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100) NOT NULL,
    status INTEGER NOT NULL DEFAULT 1,
    role VARCHAR(50),
    org_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('admin', '超级管理员', 'admin@example.com', 1, 'ADMIN', 'Zenith 集团总部');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('editor', '运营编辑', 'editor@example.com', 1, 'EDITOR', '研发中心');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('user1', '普通用户1', 'user1@example.com', 1, 'USER', '前端开发组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('user2', '普通用户2', 'user2@example.com', 0, 'USER', '后端开发组');

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    status INTEGER NOT NULL DEFAULT 1,
    member_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_role (name, code, description, status, member_count) VALUES ('超级管理员', 'ROLE_ADMIN', '拥有系统所有权限', 1, 2);
INSERT INTO sys_role (name, code, description, status, member_count) VALUES ('运营编辑', 'ROLE_EDITOR', '负责内容发布与审核', 1, 5);
INSERT INTO sys_role (name, code, description, status, member_count) VALUES ('普通用户', 'ROLE_USER', '仅拥有基础查看权限', 1, 1240);
INSERT INTO sys_role (name, code, description, status, member_count) VALUES ('访客', 'ROLE_GUEST', '只读权限', 0, 0);

CREATE TABLE IF NOT EXISTS sys_notice (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    type VARCHAR(50) NOT NULL,
    author VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_notice (title, type, author, status) VALUES ('关于2024年春节放假安排的通知', '公告', '行政部', '已发布');
INSERT INTO sys_notice (title, type, author, status) VALUES ('系统升级维护公告', '通知', '技术部', '已发布');
INSERT INTO sys_notice (title, type, author, status) VALUES ('新员工入职培训指南', '通知', '人力资源部', '草稿');

CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(50) NOT NULL,
    path VARCHAR(100),
    component VARCHAR(100),
    icon VARCHAR(50),
    sort INTEGER DEFAULT 0,
    type VARCHAR(20),
    permission VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (0, '系统管理', '/system', NULL, 'Settings', 1, 'DIR', NULL);
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (1, '用户管理', '/system/user', 'UserTable', 'Users', 1, 'MENU', 'sys:user:list');
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (1, '角色管理', '/system/role', 'RoleTable', 'Shield', 2, 'MENU', 'sys:role:list');
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (1, '菜单管理', '/system/menu', 'MenuTable', 'Menu', 3, 'MENU', 'sys:menu:list');

CREATE TABLE IF NOT EXISTS sys_org (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(100) NOT NULL,
    sort INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_org (parent_id, name, sort, status) VALUES (0, 'Zenith 集团总部', 1, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (1, '研发中心', 1, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (2, '前端开发组', 1, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (2, '后端开发组', 2, 1);

CREATE TABLE IF NOT EXISTS sys_dict (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    label VARCHAR(100) NOT NULL,
    value VARCHAR(100) NOT NULL,
    sort INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_dict (type, label, value, sort) VALUES ('user_status', '正常', '1', 1);
INSERT INTO sys_dict (type, label, value, sort) VALUES ('user_status', '禁用', '0', 2);
INSERT INTO sys_dict (type, label, value, sort) VALUES ('notice_type', '通知', '1', 1);
INSERT INTO sys_dict (type, label, value, sort) VALUES ('notice_type', '公告', '2', 2);

CREATE TABLE IF NOT EXISTS sys_oper_log (
    id BIGSERIAL PRIMARY KEY,
    module VARCHAR(100),
    content VARCHAR(500),
    operator VARCHAR(50),
    ip VARCHAR(128),
    result VARCHAR(20),
    remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_oper_log (module, content, operator, ip, result, remark) VALUES ('用户管理', '新增用户: test_user', 'admin', '192.168.1.1', '成功', '无');
INSERT INTO sys_oper_log (module, content, operator, ip, result, remark) VALUES ('角色管理', '修改角色权限: 运营编辑', 'admin', '192.168.1.1', '成功', '无');

CREATE TABLE IF NOT EXISTS sys_login_log (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50),
    ip VARCHAR(128),
    status VARCHAR(20),
    msg VARCHAR(200),
    login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_at TIMESTAMP
);

INSERT INTO sys_login_log (username, ip, status, msg) VALUES ('admin', '192.168.1.1', '成功', '登录成功');
INSERT INTO sys_login_log (username, ip, status, msg) VALUES ('editor', '192.168.1.2', '失败', '密码错误');

CREATE TABLE IF NOT EXISTS sys_error_log (
    id BIGSERIAL PRIMARY KEY,
    module VARCHAR(100),
    ip VARCHAR(128),
    error_msg TEXT,
    stack_trace TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO sys_error_log (module, ip, error_msg, stack_trace) VALUES ('系统管理', '127.0.0.1', 'NullPointerException', 'java.lang.NullPointerException: null\n    at com.zenith.admin.app.UserService.listByPage(UserService.java:25)');
