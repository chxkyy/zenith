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
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('ceo', 'CEO', 'ceo@example.com', 1, 'ADMIN', 'Zenith 集团总部');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('cto', '技术总监', 'cto@example.com', 1, 'ADMIN', '研发中心');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('fe1', '前端开发工程师1', 'fe1@example.com', 1, 'USER', '前端开发组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('fe2', '前端开发工程师2', 'fe2@example.com', 1, 'USER', '前端开发组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('fe3', '前端开发工程师3', 'fe3@example.com', 1, 'USER', '前端开发组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('fe4', '前端开发工程师4', 'fe4@example.com', 0, 'USER', '前端开发组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('be1', '后端开发工程师1', 'be1@example.com', 1, 'USER', '后端开发组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('be2', '后端开发工程师2', 'be2@example.com', 1, 'USER', '后端开发组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('be3', '后端开发工程师3', 'be3@example.com', 1, 'USER', '后端开发组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('be4', '后端开发工程师4', 'be4@example.com', 1, 'USER', '后端开发组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('test1', '测试工程师1', 'test1@example.com', 1, 'USER', '测试组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('test2', '测试工程师2', 'test2@example.com', 1, 'USER', '测试组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('ops1', '运维工程师1', 'ops1@example.com', 1, 'USER', '运维组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('ops2', '运维工程师2', 'ops2@example.com', 1, 'USER', '运维组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('marketing1', '市场策划专员1', 'marketing1@example.com', 1, 'USER', '市场策划组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('marketing2', '市场策划专员2', 'marketing2@example.com', 1, 'USER', '市场策划组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('brand1', '品牌推广专员1', 'brand1@example.com', 1, 'USER', '品牌推广组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('brand2', '品牌推广专员2', 'brand2@example.com', 1, 'USER', '品牌推广组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('sales1', '国内销售专员1', 'sales1@example.com', 1, 'USER', '国内销售组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('sales2', '国内销售专员2', 'sales2@example.com', 1, 'USER', '国内销售组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('sales3', '国际销售专员1', 'sales3@example.com', 1, 'USER', '国际销售组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('sales4', '国际销售专员2', 'sales4@example.com', 1, 'USER', '国际销售组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('hr1', '招聘专员1', 'hr1@example.com', 1, 'USER', '招聘组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('hr2', '招聘专员2', 'hr2@example.com', 1, 'USER', '招聘组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('hr3', '培训专员1', 'hr3@example.com', 1, 'USER', '培训组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('finance1', '会计1', 'finance1@example.com', 1, 'USER', '会计组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('finance2', '会计2', 'finance2@example.com', 1, 'USER', '会计组');
INSERT INTO sys_user (username, nickname, email, status, role, org_name) VALUES ('finance3', '出纳1', 'finance3@example.com', 1, 'USER', '出纳组');

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
    content TEXT,
    status VARCHAR(20) NOT NULL,
    remark VARCHAR(255),
    is_pinned BOOLEAN DEFAULT false,
    read_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO sys_notice (title, type, author, content, status, remark) VALUES ('关于2024年春节放假安排的通知', 'system', '行政部', '根据国家法定节假日安排，2024年春节放假时间为2月10日至2月17日，共8天。请各部门做好工作安排。', '已发布', '春节放假通知');
INSERT INTO sys_notice (title, type, author, content, status, remark) VALUES ('系统升级维护公告', 'system', '技术部', '为了提供更好的服务，系统将于2024年1月20日凌晨2:00-4:00进行升级维护，期间系统将暂时无法访问。', '已发布', '系统维护通知');
INSERT INTO sys_notice (title, type, author, content, status, remark) VALUES ('新员工入职培训指南', 'business', '人力资源部', '新员工入职培训包括公司文化、规章制度、业务流程等内容，请按时参加。', '草稿', '入职培训指南');

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
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (1, '组织管理', '/system/dept', 'DeptTable', 'Building', 4, 'MENU', 'sys:dept:list');
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (0, '核心管理', '/core', NULL, 'Home', 2, 'DIR', NULL);
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (6, '权限管理', '/core/permission', 'PermissionTable', 'Lock', 1, 'MENU', 'core:permission:list');
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (0, '系统运维', '/ops', NULL, 'Server', 3, 'DIR', NULL);
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (8, '制定管理', '/ops/plan', 'PlanTable', 'Calendar', 1, 'MENU', 'ops:plan:list');
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (8, '操作日志', '/ops/oper-log', 'OperLogTable', 'FileText', 2, 'MENU', 'ops:oper-log:list');
INSERT INTO sys_menu (parent_id, name, path, component, icon, sort, type, permission) VALUES (8, '登录日志', '/ops/login-log', 'LoginLogTable', 'LogIn', 3, 'MENU', 'ops:login-log:list');

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
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (2, '测试组', 3, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (2, '运维组', 4, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (1, '市场部', 2, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (7, '市场策划组', 1, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (7, '品牌推广组', 2, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (1, '销售部', 3, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (10, '国内销售组', 1, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (10, '国际销售组', 2, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (1, '人力资源部', 4, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (13, '招聘组', 1, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (13, '培训组', 2, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (1, '财务部', 5, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (16, '会计组', 1, 1);
INSERT INTO sys_org (parent_id, name, sort, status) VALUES (16, '出纳组', 2, 1);

-- 字典类型表
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL COMMENT '字典名称',
    type VARCHAR(255) NOT NULL UNIQUE COMMENT '字典类型',
    status INT DEFAULT 1 COMMENT '状态',
    remark VARCHAR(255) COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 字典项表
CREATE TABLE IF NOT EXISTS sys_dict_item (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL COMMENT '字典类型',
    label VARCHAR(255) NOT NULL COMMENT '标签',
    dict_value VARCHAR(255) NOT NULL COMMENT '值',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态',
    remark VARCHAR(255) COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_type_value UNIQUE (type, dict_value)
);

-- 插入字典类型
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('用户状态', 'user_status', 1, '用户状态字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('通知类型', 'notice_type', 1, '通知类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('性别', 'gender', 1, '性别字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('部门类型', 'dept_type', 1, '部门类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('角色类型', 'role_type', 1, '角色类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('菜单类型', 'menu_type', 1, '菜单类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('操作类型', 'oper_type', 1, '操作类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('操作结果', 'oper_result', 1, '操作结果字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('日志级别', 'log_level', 1, '日志级别字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('缓存类型', 'cache_type', 1, '缓存类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('文件类型', 'file_type', 1, '文件类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('图片类型', 'image_type', 1, '图片类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('文档类型', 'document_type', 1, '文档类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('国家/地区', 'country', 1, '国家/地区字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('省份', 'province', 1, '省份字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('城市', 'city', 1, '城市字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('学历', 'education', 1, '学历字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('婚姻状况', 'marital_status', 1, '婚姻状况字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('工作状态', 'work_status', 1, '工作状态字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('员工类型', 'employee_type', 1, '员工类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('合同类型', 'contract_type', 1, '合同类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('薪酬类型', 'salary_type', 1, '薪酬类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('货币类型', 'currency', 1, '货币类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('语言', 'language', 1, '语言字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('操作系统', 'os', 1, '操作系统字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('浏览器', 'browser', 1, '浏览器字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('设备类型', 'device_type', 1, '设备类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('网络类型', 'network_type', 1, '网络类型字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('天气状况', 'weather', 1, '天气状况字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('季节', 'season', 1, '季节字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('月份', 'month', 1, '月份字典');
INSERT INTO sys_dict_type (name, type, status, remark) VALUES ('星期', 'week', 1, '星期字典');

-- 插入字典项
-- 用户状态
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('user_status', '正常', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('user_status', '禁用', '0', 2, 1);

-- 通知类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('notice_type', '通知', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('notice_type', '公告', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('notice_type', '警告', '3', 3, 1);

-- 性别
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('gender', '男', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('gender', '女', '0', 2, 1);

-- 部门类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('dept_type', '集团', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('dept_type', '部门', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('dept_type', '小组', '3', 3, 1);

-- 角色类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('role_type', '管理员', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('role_type', '运营编辑', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('role_type', '普通用户', '3', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('role_type', '访客', '4', 4, 1);

-- 菜单类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('menu_type', '目录', 'DIR', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('menu_type', '菜单', 'MENU', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('menu_type', '按钮', 'BUTTON', 3, 1);

-- 操作类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('oper_type', '新增', 'ADD', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('oper_type', '修改', 'UPDATE', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('oper_type', '删除', 'DELETE', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('oper_type', '查询', 'SELECT', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('oper_type', '登录', 'LOGIN', 5, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('oper_type', '退出', 'LOGOUT', 6, 1);

-- 操作结果
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('oper_result', '成功', 'SUCCESS', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('oper_result', '失败', 'FAILED', 2, 1);

-- 日志级别
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('log_level', 'DEBUG', 'DEBUG', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('log_level', 'INFO', 'INFO', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('log_level', 'WARN', 'WARN', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('log_level', 'ERROR', 'ERROR', 4, 1);

-- 缓存类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('cache_type', '本地缓存', 'LOCAL', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('cache_type', 'Redis', 'REDIS', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('cache_type', 'Memcached', 'MEMCACHED', 3, 1);

-- 文件类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('file_type', '图片', 'IMAGE', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('file_type', '文档', 'DOCUMENT', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('file_type', '视频', 'VIDEO', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('file_type', '音频', 'AUDIO', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('file_type', '其他', 'OTHER', 5, 1);

-- 图片类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('image_type', 'JPG', 'jpg', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('image_type', 'PNG', 'png', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('image_type', 'GIF', 'gif', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('image_type', 'WebP', 'webp', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('image_type', 'SVG', 'svg', 5, 1);

-- 文档类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('document_type', 'Word', 'doc', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('document_type', 'Excel', 'xls', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('document_type', 'PDF', 'pdf', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('document_type', 'PPT', 'ppt', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('document_type', 'TXT', 'txt', 5, 1);

-- 国家/地区
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('country', '中国', 'CN', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('country', '美国', 'US', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('country', '日本', 'JP', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('country', '德国', 'DE', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('country', '英国', 'GB', 5, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('country', '法国', 'FR', 6, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('country', '加拿大', 'CA', 7, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('country', '澳大利亚', 'AU', 8, 1);

-- 省份
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '北京', '110000', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '上海', '310000', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '广东', '440000', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '浙江', '330000', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '江苏', '320000', 5, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '山东', '370000', 6, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '河南', '410000', 7, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '湖北', '420000', 8, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '四川', '510000', 9, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('province', '河北', '130000', 10, 1);

-- 城市
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '北京市', '110100', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '上海市', '310100', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '广州市', '440100', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '深圳市', '440300', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '杭州市', '330100', 5, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '南京市', '320100', 6, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '济南市', '370100', 7, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '郑州市', '410100', 8, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '武汉市', '420100', 9, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('city', '成都市', '510100', 10, 1);

-- 学历
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('education', '小学', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('education', '初中', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('education', '高中', '3', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('education', '大专', '4', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('education', '本科', '5', 5, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('education', '硕士', '6', 6, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('education', '博士', '7', 7, 1);

-- 婚姻状况
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('marital_status', '未婚', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('marital_status', '已婚', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('marital_status', '离异', '3', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('marital_status', '丧偶', '4', 4, 1);

-- 工作状态
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('work_status', '在职', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('work_status', '离职', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('work_status', '休假', '3', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('work_status', '退休', '4', 4, 1);

-- 员工类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('employee_type', '正式员工', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('employee_type', '试用期', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('employee_type', '临时工', '3', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('employee_type', '实习生', '4', 4, 1);

-- 合同类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('contract_type', '固定期限', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('contract_type', '无固定期限', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('contract_type', '临时合同', '3', 3, 1);

-- 薪酬类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('salary_type', '月薪', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('salary_type', '日薪', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('salary_type', '时薪', '3', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('salary_type', '计件', '4', 4, 1);

-- 货币类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('currency', '人民币', 'CNY', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('currency', '美元', 'USD', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('currency', '欧元', 'EUR', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('currency', '英镑', 'GBP', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('currency', '日元', 'JPY', 5, 1);

-- 语言
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('language', '中文', 'zh', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('language', '英文', 'en', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('language', '日语', 'ja', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('language', '德语', 'de', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('language', '法语', 'fr', 5, 1);

-- 操作系统
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('os', 'Windows', 'windows', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('os', 'macOS', 'macos', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('os', 'Linux', 'linux', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('os', 'iOS', 'ios', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('os', 'Android', 'android', 5, 1);

-- 浏览器
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('browser', 'Chrome', 'chrome', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('browser', 'Firefox', 'firefox', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('browser', 'Safari', 'safari', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('browser', 'Edge', 'edge', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('browser', 'IE', 'ie', 5, 1);

-- 设备类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('device_type', 'PC', 'pc', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('device_type', '手机', 'mobile', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('device_type', '平板', 'tablet', 3, 1);

-- 网络类型
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('network_type', '有线', 'wired', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('network_type', 'WiFi', 'wifi', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('network_type', '4G', '4g', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('network_type', '5G', '5g', 4, 1);

-- 天气状况
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('weather', '晴', 'sunny', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('weather', '多云', 'cloudy', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('weather', '阴', 'overcast', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('weather', '雨', 'rainy', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('weather', '雪', 'snowy', 5, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('weather', '雾', 'foggy', 6, 1);

-- 季节
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('season', '春季', 'spring', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('season', '夏季', 'summer', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('season', '秋季', 'autumn', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('season', '冬季', 'winter', 4, 1);

-- 月份
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '1月', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '2月', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '3月', '3', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '4月', '4', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '5月', '5', 5, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '6月', '6', 6, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '7月', '7', 7, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '8月', '8', 8, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '9月', '9', 9, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '10月', '10', 10, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '11月', '11', 11, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('month', '12月', '12', 12, 1);

-- 星期
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('week', '周一', '1', 1, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('week', '周二', '2', 2, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('week', '周三', '3', 3, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('week', '周四', '4', 4, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('week', '周五', '5', 5, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('week', '周六', '6', 6, 1);
INSERT INTO sys_dict_item (type, label, dict_value, sort, status) VALUES ('week', '周日', '7', 7, 1);

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
