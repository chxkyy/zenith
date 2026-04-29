-- ============================================================
-- Zenith Admin System - 数据库表/字段中文注释（完整版 v2）
-- 基于实际 DB 查询结果生成，列名严格匹配
-- 说明: 仅添加/覆盖 COMMENT，不修改任何表结构
-- 兼容: PostgreSQL (COMMENT ON 语法)
-- ============================================================

-- ============================================================
-- 1. t_sys_user | 系统用户表（补全缺失字段）
-- ============================================================
COMMENT ON TABLE t_sys_user IS '系统用户表，存储系统所有用户的基本信息';

-- 已有: id, username, nickname, email, status, role, org_name, created_time, password
COMMENT ON COLUMN t_sys_user.update_time IS '最后更新时间';
COMMENT ON COLUMN t_sys_user.update_user_id IS '最后更新人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_user.create_user_id IS '创建人ID，关联 t_sys_user.id';

-- ============================================================
-- 2. t_sys_role | 系统角色表（补全缺失字段）
-- ============================================================
COMMENT ON TABLE t_sys_role IS '系统角色表，定义角色及权限范围';

-- 已有: id, name, code, description, status, member_count, created_time
COMMENT ON COLUMN t_sys_role.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_role.update_user_id IS '最后更新人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_role.update_time IS '最后更新时间';

-- ============================================================
-- 3. t_sys_notice | 系统通知公告表（补全缺失字段）
-- ============================================================
COMMENT ON TABLE t_sys_notice IS '系统通知公告表，管理系统通知、公告等信息发布';

-- 已有: id, title, type, author, content, status, remark, is_pinned, read_count, created_time, update_time
COMMENT ON COLUMN t_sys_notice.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_notice.update_user_id IS '最后更新人ID，关联 t_sys_user.id';

-- ============================================================
-- 4. t_sys_menu | 系统菜单表（补全缺失字段）
-- ============================================================
COMMENT ON TABLE t_sys_menu IS '系统菜单表，定义前端菜单结构和权限标识';

-- 已有: id, parent_id, name, path, component, icon, sort, type, permission, created_time
COMMENT ON COLUMN t_sys_menu.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_menu.update_user_id IS '最后更新人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_menu.update_time IS '最后更新时间';

-- ============================================================
-- 5. t_sys_org | 系统组织表（补全缺失字段）
-- ============================================================
COMMENT ON TABLE t_sys_org IS '系统组织架构表，定义组织/部门的树形结构';

-- 已有: id, parent_id, name, sort, status, created_time
COMMENT ON COLUMN t_sys_org.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_org.update_user_id IS '最后更新人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_org.update_time IS '最后更新时间';

-- ============================================================
-- 6. t_sys_dict_type | 字典类型表（补全缺失字段）
-- ============================================================
COMMENT ON TABLE t_sys_dict_type IS '字典类型表，定义系统字典的分类';

-- 已有: id, name, type, status, remark, created_time
COMMENT ON COLUMN t_sys_dict_type.update_time IS '最后更新时间';
COMMENT ON COLUMN t_sys_dict_type.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_dict_type.update_user_id IS '最后更新人ID，关联 t_sys_user.id';

-- ============================================================
-- 7. t_sys_dict_item | 字典项表（全新注释）
-- ============================================================
COMMENT ON TABLE t_sys_dict_item IS '字典项表，存储字典类型下具体的键值对';

COMMENT ON COLUMN t_sys_dict_item.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_dict_item.type IS '所属字典类型编码，关联 t_sys_dict_type.type';
COMMENT ON COLUMN t_sys_dict_item.label IS '字典项显示标签（如：正常、男、管理员）';
COMMENT ON COLUMN t_sys_dict_item.dict_value IS '字典项实际值（如：1、0、ROLE_ADMIN）';
COMMENT ON COLUMN t_sys_dict_item.sort IS '排序号，数值越小越靠前';
COMMENT ON COLUMN t_sys_dict_item.status IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN t_sys_dict_item.remark IS '备注说明';
COMMENT ON COLUMN t_sys_dict_item.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_dict_item.update_time IS '最后更新时间';
COMMENT ON COLUMN t_sys_dict_item.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_dict_item.update_user_id IS '最后更新人ID，关联 t_sys_user.id';

-- ============================================================
-- 8. t_sys_oper_log | 操作日志表（全新注释）
-- ============================================================
COMMENT ON TABLE t_sys_oper_log IS '操作日志表，记录用户的关键业务操作';

COMMENT ON COLUMN t_sys_oper_log.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_oper_log.module IS '操作模块（如：用户管理、角色管理）';
COMMENT ON COLUMN t_sys_oper_log.content IS '操作内容描述（如：新增用户: xxx）';
COMMENT ON COLUMN t_sys_oper_log.operator IS '操作人用户名';
COMMENT ON COLUMN t_sys_oper_log.ip IS '操作人IP地址';
COMMENT ON COLUMN t_sys_oper_log.result IS '操作结果（如：成功、失败）';
COMMENT ON COLUMN t_sys_oper_log.remark IS '备注/补充说明';
COMMENT ON COLUMN t_sys_oper_log.created_time IS '操作时间/创建时间';
COMMENT ON COLUMN t_sys_oper_log.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_oper_log.update_user_id IS '最后更新人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_oper_log.update_time IS '最后更新时间';

-- ============================================================
-- 9. t_sys_login_log | 登录日志表（全新注释）
-- ============================================================
COMMENT ON TABLE t_sys_login_log IS '登录日志表，记录用户登录/登出行为';

COMMENT ON COLUMN t_sys_login_log.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_login_log.username IS '登录用户名';
COMMENT ON COLUMN t_sys_login_log.ip IS '登录IP地址';
COMMENT ON COLUMN t_sys_login_log.status IS '登录结果（如：成功、失败）';
COMMENT ON COLUMN t_sys_login_log.msg IS '登录结果消息（如：登录成功、密码错误）';
COMMENT ON COLUMN t_sys_login_log.login_at IS '登录时间';
COMMENT ON COLUMN t_sys_login_log.logout_at IS '登出时间';
COMMENT ON COLUMN t_sys_login_log.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_login_log.update_user_id IS '最后更新人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_login_log.created_time IS '创建时间';
COMMENT ON COLUMN t_sys_login_log.update_time IS '最后更新时间';

-- ============================================================
-- 10. t_sys_file | 文件管理表（全新注释）
-- ============================================================
COMMENT ON TABLE t_sys_file IS '文件管理表，存储上传文件的元信息';

COMMENT ON COLUMN t_sys_file.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_file.name IS '文件存储名称（UUID生成）';
COMMENT ON COLUMN t_sys_file.original_name IS '文件原始名称（上传时的文件名）';
COMMENT ON COLUMN t_sys_file.path IS '文件存储路径（相对路径）';
COMMENT ON COLUMN t_sys_file.type IS '文件类型/扩展名（如：JPG、PNG、PDF）';
COMMENT ON COLUMN t_sys_file.size IS '文件大小（字节数）';
COMMENT ON COLUMN t_sys_file.uploader IS '上传者用户名';
COMMENT ON COLUMN t_sys_file.created_time IS '上传时间/创建时间';
COMMENT ON COLUMN t_sys_file.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_file.update_user_id IS '最后更新人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_file.update_time IS '最后更新时间';

-- ============================================================
-- 11. t_sys_error_log | 错误日志表（全新注释）
-- ============================================================
COMMENT ON TABLE t_sys_error_log IS '系统错误日志表，记录运行时异常信息';

COMMENT ON COLUMN t_sys_error_log.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_error_log.module IS '发生错误的模块名称';
COMMENT ON COLUMN t_sys_error_log.ip IS '请求来源IP地址';
COMMENT ON COLUMN t_sys_error_log.error_msg IS '错误消息摘要';
COMMENT ON COLUMN t_sys_error_log.stack_trace IS '异常堆栈信息（完整调用链）';
COMMENT ON COLUMN t_sys_error_log.created_time IS '错误发生时间';
COMMENT ON COLUMN t_sys_error_log.create_user_id IS '创建人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_error_log.update_user_id IS '最后更新人ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_error_log.update_time IS '最后更新时间';

-- ============================================================
-- 12. t_sys_user_role | 用户角色关联表（全新注释）
-- ============================================================
COMMENT ON TABLE t_sys_user_role IS '用户角色关联表，定义用户与角色的多对多关系';

COMMENT ON COLUMN t_sys_user_role.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_user_role.user_id IS '用户ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_user_role.role_id IS '角色ID，关联 t_sys_role.id';
COMMENT ON COLUMN t_sys_user_role.created_time IS '关联关系创建时间';

-- ============================================================
-- 13. t_sys_role_menu | 角色菜单关联表（全新注释）
-- ============================================================
COMMENT ON TABLE t_sys_role_menu IS '角色菜单关联表，定义角色与菜单权限的多对多关系';

COMMENT ON COLUMN t_sys_role_menu.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_role_menu.role_id IS '角色ID，关联 t_sys_role.id';
COMMENT ON COLUMN t_sys_role_menu.menu_id IS '菜单ID，关联 t_sys_menu.id';
COMMENT ON COLUMN t_sys_role_menu.created_time IS '关联关系创建时间';

-- ============================================================
-- 14. t_sys_online_user | 在线用户表（全新注释）
-- ============================================================
COMMENT ON TABLE t_sys_online_user IS '在线用户表，记录当前登录用户的会话令牌';

COMMENT ON COLUMN t_sys_online_user.id IS '主键ID，自增序列';
COMMENT ON COLUMN t_sys_online_user.user_id IS '用户ID，关联 t_sys_user.id';
COMMENT ON COLUMN t_sys_online_user.token IS '登录令牌Token（UUID生成），用于身份验证';
COMMENT ON COLUMN t_sys_online_user.login_time IS '登录时间';
COMMENT ON COLUMN t_sys_online_user.last_access_time IS '最后访问时间，用于判断会话活跃状态';
COMMENT ON COLUMN t_sys_online_user.ip IS '登录IP地址';

-- ============================================================
-- 执行完毕
-- ============================================================
