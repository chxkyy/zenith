-- ============================================================
-- 合作机构管理中心 - 数据库迁移脚本（第一批）
-- 覆盖: 1.1.1 合作机构管理池 + 1.1.2 管理人准入
-- 对应规格: specs/cooperative-institution-center/PRODUCT.md, TECH.md
-- ============================================================

-- 1. 合作机构池表
CREATE TABLE IF NOT EXISTS t_inst_pool (
    id bigserial PRIMARY KEY,
    name varchar(100) NOT NULL,
    pool_type varchar(50) NOT NULL,
    description text,
    owner_id bigint,
    status smallint DEFAULT 1,
    create_user_id bigint,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    update_user_id bigint,
    update_time timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(name)
);

COMMENT ON TABLE t_inst_pool IS '合作机构管理池';
COMMENT ON COLUMN t_inst_pool.id IS '主键';
COMMENT ON COLUMN t_inst_pool.name IS '池名称（唯一）';
COMMENT ON COLUMN t_inst_pool.pool_type IS '池类型（公募池/私募池/专户池等）';
COMMENT ON COLUMN t_inst_pool.description IS '描述说明';
COMMENT ON COLUMN t_inst_pool.owner_id IS '负责人用户ID';
COMMENT ON COLUMN t_inst_pool.status IS '状态：1=启用 0=停用';
CREATE INDEX idx_inst_pool_status ON t_inst_pool(status);
CREATE INDEX idx_inst_pool_type ON t_inst_pool(pool_type);

-- 2. 合作机构表（企业信息主表）
CREATE TABLE IF NOT EXISTS t_inst_institution (
    id bigserial PRIMARY KEY,
    full_name varchar(200) NOT NULL,
    short_name varchar(100),
    credit_code varchar(18),
    inst_type varchar(50),
    establish_date date,
    registered_capital varchar(50),
    legal_representative varchar(100),
    registered_address varchar(500),
    contact_phone varchar(50),
    contact_email varchar(100),
    logo_url varchar(500),
    cooperation_status varchar(20) DEFAULT 'pending',
    create_user_id bigint,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    update_user_id bigint,
    update_time timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_inst_institution IS '合作机构（企业信息）';
COMMENT ON COLUMN t_inst_institution.full_name IS '机构全称';
COMMENT ON COLUMN t_inst_institution.short_name IS '机构简称';
COMMENT ON COLUMN t_inst_institution.credit_code IS '统一社会信用代码';
COMMENT ON COLUMN t_inst_institution.inst_type IS '机构类型';
COMMENT ON COLUMN t_inst_institution.cooperation_status IS '合作状态：pending/cooperating/terminated/suspended/pending_admit';
CREATE INDEX idx_inst_credit_code ON t_inst_institution(credit_code);
CREATE INDEX idx_inst_full_name ON t_inst_institution(full_name);

-- 3. 机构-池关联表（多对多）
CREATE TABLE IF NOT EXISTS t_inst_pool_institution (
    id bigserial PRIMARY KEY,
    pool_id bigint NOT NULL REFERENCES t_inst_pool(id),
    institution_id bigint NOT NULL REFERENCES t_inst_institution(id),
    join_date date DEFAULT CURRENT_DATE,
    remark text,
    create_user_id bigint,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(pool_id, institution_id)
);

COMMENT ON TABLE t_inst_pool_institution IS '机构-池关联关系（多对多）';

-- 4. 合作产品表
CREATE TABLE IF NOT EXISTS t_inst_product (
    id bigserial PRIMARY KEY,
    institution_id bigint NOT NULL REFERENCES t_inst_institution(id),
    product_name varchar(200) NOT NULL,
    product_code varchar(50),
    product_type varchar(50),
    cooperation_status varchar(20) DEFAULT 'cooperating',
    cooperation_start_date date,
    end_date date,
    contact_person varchar(100),
    create_user_id bigint,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    update_user_id bigint,
    update_time timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_inst_product IS '合作产品清单';
CREATE INDEX idx_prod_inst ON t_inst_product(institution_id);

-- 5. 准入申请单表
CREATE TABLE IF NOT EXISTS t_inst_admission (
    id bigserial PRIMARY KEY,
    admission_no varchar(30) NOT NULL UNIQUE,
    process_instance_id bigint,
    manager_name varchar(200) NOT NULL,
    manager_type varchar(50) NOT NULL,
    credit_code varchar(18),
    registered_capital varchar(50),
    establish_date date,
    legal_representative varchar(100),
    registered_address varchar(500),
    contact_person varchar(100) NOT NULL,
    contact_phone varchar(50) NOT NULL,
    contact_email varchar(100),
    target_pool_ids text,
    basic_info jsonb,
    status varchar(20) DEFAULT 'DRAFT',
    scorer_id bigint,
    score_result jsonb,
    approver_id bigint,
    approval_opinion text,
    rejection_reason text,
    create_user_id bigint NOT NULL,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    update_user_id bigint,
    update_time timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_inst_admission IS '管理人准入申请单';
COMMENT ON COLUMN t_inst_admission.admission_no IS '申请单号 ADM-YYYYMMDD-NNNN';
COMMENT ON COLUMN t_inst_admission.status IS '状态：DRAFT/PENDING_REVIEW/PENDING_APPROVAL/APPROVED/REJECTED/WITHDRAWN';
CREATE INDEX idx_admission_no ON t_inst_admission(admission_no);
CREATE INDEX idx_admission_status ON t_inst_admission(status);
CREATE INDEX idx_admission_creator ON t_inst_admission(create_user_id);

-- 6. 准入申请-材料关联表
CREATE TABLE IF NOT EXISTS t_inst_admission_material (
    id bigserial PRIMARY KEY,
    admission_id bigint NOT NULL REFERENCES t_inst_admission(id),
    material_category varchar(50) NOT NULL,
    material_name varchar(200),
    file_id bigint NOT NULL,
    sort_order int DEFAULT 0,
    create_user_id bigint,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_inst_admission_material IS '准入申请-材料关联';
CREATE INDEX idx_am_admission ON t_inst_admission_material(admission_id);

-- 7. 准入操作日志表
CREATE TABLE IF NOT EXISTS t_inst_admission_log (
    id bigserial PRIMARY KEY,
    admission_id bigint NOT NULL REFERENCES t_inst_admission(id),
    action varchar(50) NOT NULL,
    operator_id bigint NOT NULL,
    operator_name varchar(100),
    detail text,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_inst_admission_log IS '准入操作日志审计';
CREATE INDEX idx_al_admission ON t_inst_admission_log(admission_id);
