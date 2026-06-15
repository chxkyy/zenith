# 业务数据权限控制 - 技术规格说明

## Context

### 当前系统架构

系统采用 COLA 分层架构，后端技术栈为 Spring Boot 3.5 + MyBatis-Plus + PostgreSQL + Redis。模块结构如下：

```
backend-java/
├── project-api/          # API 接口定义、注解、DTO/Query/Cmd
├── project-model/        # 数据对象 (DO)、Mapper 接口、XML 映射文件
├── project-service/      # Service 实现、Executor（CmdExe/QryExe）、AOP 切面
├── project-web/          # Controller、配置类（Security、Redis、MVC）
└── start/                # 启动入口、application.yml
```

请求处理链路：`Controller → Service(编排) → Executor(执行) → Mapper → DB`，AOP 切面在 Service 层拦截返回值做后置处理。

### 与本功能相关的现有代码

| 文件 | 作用 | 与本功能的关系 |
|------|------|---------------|
| [`UserContext.java`](../backend-java/project-service/src/main/java/com/zenith/admin/context/UserContext.java) | ThreadLocal 存储当前登录用户 ID | 权限过滤的数据来源 |
| [`AuthInterceptor.java`](../backend-java/project-web/src/main/java/com/zenith/admin/interceptor/AuthInterceptor.java) | 拦截 `/api/**` 请求，设置 UserContext | 在 preHandle 中设置 userId |
| [`TranslateAspect.java`](../backend-java/project-service/src/main/java/com/zenith/admin/aspect/TranslateAspect.java) | AOP 切面，@AfterReturning 拦截 Service 返回值 | **参考模式**：权限切面采用类似思路但在查询前拦截 |
| [`DataPermissionService.java`](../backend-java/project-api/src/main/java/com/zenith/admin/api/system/DataPermissionService.java) | 已有的基于角色 dataScope 的数据权限接口 | **需改造**：现有实现中组织相关逻辑（dataScope 3/4）将被新策略一（ORG）统一替换 |
| [`DataScopeQryExe.java`](../backend-java/project-service/src/main/java/com/zenith/admin/service/system/executor/qry/DataScopeQryExe.java) | 现有数据范围计算逻辑 | **需改造**：`getChildOrgIds()` 递归方法移植到新的 `DataPermissionScopeQryExe`；本类中 org 相关逻辑废弃 |
| [`OrgDO.java`](../backend-java/project-model/src/main/java/com/zenith/admin/dataobject/OrgDO.java) / [`OrgMapper.java`](../backend-java/project-model/src/main/java/com/zenith/admin/mapper/OrgMapper.java) | 组织架构数据对象和 Mapper | 策略一的核心依赖 |
| [`UserDO.java`](../backend-java/project-model/src/main/java/com/zenith/admin/dataobject/UserDO.java) / [`UserMapper.java`](../backend-java/project-model/src/main/java/com/zenith/admin/mapper/UserMapper.java) | 用户数据和 Mapper | 策略二的核心依赖 |
| [`OperLogDO.java`](../backend-java/project-model/src/main/java/com/zenith/admin/dataobject/OperLogDO.java) | 操作日志数据对象 | 审计日志的写入目标 |
| [`RedisConfig.java`](../backend-java/project-web/src/main/java/com/zenith/admin/config/RedisConfig.java) | StringRedisTemplate 配置 | 缓存下级列表的基础设施 |

### 现有数据权限实现的差异与改造策略

当前系统已有一套基于角色 `dataScope` 字段（值 1-5）的数据权限实现：
- `DATA_SCOPE_ALL(1)` — 全部数据
- `DATA_SCOPE_CUSTOM(2)` — 自定义部门（通过 `t_sys_role_org` 关联）
- `DATA_SCOPE_DEPT_AND_CHILD(3)` — **本部门及下级 → 被新策略一（ORG）替换**
- `DATA_SCOPE_DEPT(4)` — **仅本部门 → 被新策略一（ORG）替换**
- `DATA_SCOPE_SELF(5)` — 仅本人

**改造策略——策略一（ORG）统一替换现有组织相关逻辑**：

| 现有 dataScope | 对应关系 | 处理方式 |
|---------------|---------|---------|
| 3（本部门及下级） | 等价于 `@DataPermission(strategy = ORG)` + 用户所在组织含下级 | **替换为新框架** |
| 4（仅本部门） | 等价于 `@DataPermission(strategy = ORG)` + 仅用户所在组织（不含下级） | **替换为新框架** |
| 1（全部数据） | 超级管理员场景，保留在 `@DataPermissionIgnore` 或特殊处理中 | **保留** |
| 2（自定义部门） | 通过 `t_sys_role_org` 关联，属于角色维度控制 | **暂保留**，后续评估是否迁移 |
| 5（仅本人） | 属于角色维度控制 | **暂保留**，可作为未来策略扩展 |

具体改造动作：
1. 新建 `DataPermissionScopeQryExe`，移植并增强 `DataScopeQryExe.getChildOrgIds()` 方法
2. `DataPermissionService.getAccessibleOrgIds()` 和 `getDataScope()` 中 dataScope=3/4 的分支改为调用新的 `DataPermissionScopeService`
3. 已使用 `DataPermissionService` 做组织范围过滤的模块，逐步迁移到 `@DataPermission(strategy = ORG)` 注解方式
4. `DataScopeQryExe` 中 dataScope=3/4 的逻辑标记为 `@Deprecated`，引导使用新接口

## Proposed Changes

### 一、总体设计

采用 **MyBatis-Plus 拦截器 + 自定义注解** 的方案，在 SQL 执行前自动改写 SQL 追加权限过滤条件。

```
请求进入
  → AuthInterceptor 设置 UserContext
    → Controller 调用 Service
      → @DataPermission 注解读取策略
        → DataPermissionInterceptor 拦截 MyBatis-Plus Executor
          → 根据 UserContext.getUserId() 计算权限范围
            → 改写 SQL 追加 WHERE 条件（缓存优化）
              → 执行原始查询（已带过滤条件）
                → 返回过滤后的结果
```

### 二、新增/修改的模块与文件

#### 2.1 新增注解（project-api）

**文件**: `com.zenith.admin.annotation.DataPermission`

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {
    /** 权限控制策略 */
    DataPermissionStrategy strategy() default DataPermissionStrategy.OWNER_ORG;
}
```

**文件**: `com.zenith.admin.annotation.DataPermissionIgnore`

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermissionIgnore {
}
```

**文件**: `com.zenith.admin.annotation.DataPermissionStrategy`（枚举）

```java
public enum DataPermissionStrategy {
    /** 纯组织架构控制：按 org_id 过滤 */
    ORG,
    /** 人员-数据绑定 + 组织架构混合控制：通过 t_data_permission 绑定表过滤 */
    OWNER_ORG
}
```

#### 2.2 核心拦截器（project-service）

**文件**: `com.zenith.admin.aspect.DataPermissionAspect`

- AOP 切面，拦截标注了 `@DataPermission` 的 Mapper 方法或 Service 方法
- 在方法执行前，从 `UserContext` 获取当前用户 ID
- 调用 `DataPermissionHelper` 构建过滤条件
- 将过滤条件通过 `ThreadLocal` 传递给 MyBatis-Plus 拦截器

**替代方案评估**：

| 方案 | 优点 | 缺点 | 选择 |
|------|------|------|------|
| AOP + MyBatis-Plus Interceptor | 与现有 TranslateAspect 模式一致；SQL 层面拦截，对上层透明 | 需要理解 MyBatis-Plus 拦截器链 | **采用** |
| 手动在每个 Query 中拼接 | 简单直接 | 侵入业务代码，易遗漏 | 不采用 |
| MyBatis XML `<if>` 动态 SQL | 灵活 | 每个 Mapper XML 都要改，维护成本高 | 不采用 |

#### 2.3 权限计算服务（project-service）

**文件**: `com.zenith.admin.service.system.executor.qry.DataPermissionScopeQryExe`（新建，区别于现有的 DataScopeQryExe）

职责：
- **策略一（ORG）**：根据 userId 查询用户所属组织 → 递归获取所有下级组织 ID → 返回 `List<Long> orgIds`
- **策略二（OWNER_ORG）**：根据 userId 查询用户所属组织 → 递归获取所有下级组织的所有用户 ID → 返回 `List<Long> userIds`
- 结果缓存到 Redis（TTL = 5 分钟）
- 提供缓存清除接口（组织变更时调用）

复用现有代码：
- `getChildOrgIds()` 方法从 [`DataScopeQryExe.java:120-130`](../backend-java/project-service/src/main/java/com/zenith/admin/service/system/executor/qry/DataScopeQryExe.java#L120-L130) 直接移植并增强（增加环检测）

**文件**: `com.zenith.admin.service.system.DataPermissionScopeService`（新建接口）

```java
public interface DataPermissionScopeService {
    /** 获取策略一所需的下级组织ID列表（含自身） */
    List<Long> getAccessibleOrgIds(Long userId);

    /** 获取策略二所需的下级用户ID列表（含自身） */
    List<Long> getAccessibleUserIds(Long userId);

    /** 清除指定用户的缓存（组织变更时调用） */
    void clearCache(Long userId);
}
```

#### 2.4 数据库变更

**迁移脚本**: `database/migration/data_permission.sql`

```sql
-- 1. 创建人员-数据权限绑定表（仅策略二使用）
CREATE TABLE IF NOT EXISTS t_data_permission (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL,           -- 负责人（用户ID）
    data_type varchar(50) NOT NULL,     -- 业务数据类型标识（如 customer、product）
    data_id bigint NOT NULL,            -- 业务数据记录的主键ID
    create_user_id bigint,
    created_time timestamp DEFAULT CURRENT_TIMESTAMP,
    update_user_id bigint,
    update_time timestamp DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, data_type, data_id)
);

-- 2. 为 t_data_permission 创建索引
CREATE INDEX idx_dp_data ON t_data_permission(data_type, data_id);
CREATE INDEX idx_dp_user ON t_data_permission(user_id);

-- 3. 为 OrgMapper 新增递归查询子组织的方法（CTE 方式，PostgreSQL 支持）
-- 此方法用于策略一的高效组织树遍历（替代 Java 递归）
```

注释说明：
- `t_data_permission` 表仅在策略二（OWNER_ORG）中使用，策略一不依赖此表
- `(data_type, data_id)` 和 `(user_id)` 的联合索引满足 PRODUCT.md #50 要求

#### 2.5 数据模型层（project-model）

**新增文件**:

| 文件 | 说明 |
|------|------|
| `DataPermissionDO.java` | `t_data_permission` 对应的 DO |
| `DataPermissionMapper.java` | 继承 BaseMapper<DataPermissionDO> |
| `DataPermissionMapper.xml` | 包含批量查询、存在性检查等 SQL |

**修改文件**:

| 文件 | 变更内容 |
|------|---------|
| `OrgMapper.java` | 新增 `selectChildOrgIdsRecursive(Long orgId)` 方法（使用 PostgreSQL CTE 递归查询） |
| `OrgMapper.xml` | 对应的 CTE SQL 实现 |

#### 2.6 API / DTO 层（project-api）

**新增文件**:

| 文件 | 说明 |
|------|------|
| `DataPermissionAssignCmd.java` | 分配数据权限命令对象（userId, dataType, dataId） |
| `DataPermissionRevokeCmd.java` | 回收数据权限命令对象（userId, dataType, dataId） |
| `DataPermissionBatchAssignCmd.java` | 批量分配命令对象（userId, dataType, List\<Long\> dataIds） |
| `DataPermissionDTO.java` | 数据权限记录的 DTO |
| `DataPermissionQuery.java` | 查询某用户负责的数据 / 某数据的负责人 |

**新增/修改接口**:

| 接口 | 说明 |
|------|------|
| `DataPermissionManageService` | 管理员操作接口：分配、回收、批量分配、查看分布 |

#### 2.7 Web 层（project-web）

**新增文件**: `DataPermissionController.java`

```java
@RestController
@RequestMapping("/api/data-permissions")
@RequiredArgsConstructor
public class DataPermissionController {

    // POST /assign          — 分配数据权限
    // POST/revoke           — 回收数据权限
    // POST/batch-assign     — 批量分配
    // POST?userId=xx        — 查询某用户负责的数据
    // POST?dataType=xx&dataId=yy  — 查询某数据的负责人
}
```

#### 2.8 缓存层（project-service）

**文件**: `com.zenith.admin.service.system.DataPermissionCacheService`（新建）

- 使用已有的 `StringRedisTemplate`（[`RedisConfig.java:13`](../backend-java/project-web/src/main/java/com/zenith/admin/config/RedisConfig.java#L13)）
- 缓存 Key 设计：
  - 策略一：`dp:org:{userId}` → `List<Long>` （orgId 列表的 JSON）
  - 策略二：`dp:user:{userId}` → `List<Long>` （userId 列表的 JSON）
- TTL = 300 秒（5 分钟）
- 组织架构变更时（增删改组织、调整用户组织归属），主动调用 `clearCache(userId)`

#### 2.9 操作日志审计

复用现有的 [`OperLogDO`](../backend-java/project-model/src/main/java/com/zenith/admin/dataobject/OperLogDO.java) 和 `OperLogSaveCmdExe`。

审计日志格式：
- module: `"数据权限管理"`
- content: `"分配数据权限 | 目标用户:张三(xxx) | 类型:customer | 数据ID:123"` 或 `"回收数据权限 | ..."`
- operator: 当前登录用户姓名
- result: `"成功"` 或 `"失败:原因"`

### 三、数据流详解

#### 策略一（ORG）— 查询流程

```
1. 开发者在 XxxQryExe 的 Mapper 查询方法上标注 @DataPermission(strategy = ORG)
2. 用户请求 → AuthInterceptor 设置 UserContext.userId = 100
3. Service 调用 Executor → Executor 调用 Mapper.selectPage(queryWrapper)
4. DataPermissionAspect 拦截：
   a. 读取 @DataPermission(strategy = ORG)
   b. 从 UserContext 取 userId = 100
   c. 调用 DataPermissionScopeService.getAccessibleOrgIds(100)
   d. 缓存命中 → 返回 [1, 5, 8, 12]（自身组织+下级组织）
   e. 在 queryWrapper 上追加 AND org_id IN (1, 5, 8, 12)
5. MyBatis-Plus 执行改写后的 SQL → 返回过滤结果
```

#### 策略二（OWNER_ORG）— 查询流程

```
1. 开发者标注 @DataPermission(strategy = OWNER_ORG)，并在实体表名参数中指定表名
2. 用户请求 → UserContext.userId = 100
3. DataPermissionAspect 拦截：
   a. 读取 @DataPermission(strategy = OWNER_ORG)
   b. 调用 DataPermissionScopeService.getAccessibleUserIds(100)
   c. 缓存命中 → 返回 [100, 101, 105, 108]（自身+下级用户）
   d. 改写 SQL：追加 INNER JOIN t_data_permission dp ON {table}.id = dp.data_id
                    AND dp.data_type = '{dataType}'
                    AND dp.user_id IN (100, 101, 105, 108)
4. 执行改写后的 SQL → 返回过滤结果
```

### 四、关键设计决策

#### 决策 1：SQL 拦截层级选择

**选择 MyBatis-Plus QueryWrapper 拦截（AOP + ThreadLocal），而非原生 MyBatis Interceptor**

理由：
- 项目中所有数据库访问均通过 MyBatis-Plus 的 `BaseMapper` 或自定义 `Mapper`，统一入口便于拦截
- QueryWrapper 是链式 API，AOP 可在 Executor 层注入条件，无需解析原始 SQL 字符串
- 与项目现有的 AOP 模式（TranslateAspect）保持一致

#### 决策 2：与现有 DataScopeQryExe 的关系

**策略一（ORG）统一替换现有 DataScopeQryExe 中组织相关的 dataScope（3/4），不并存**

理由：
- 现有 `dataScope=3`（本部门及下级）和 `dataScope=4`（仅本部门）与新策略一（ORG）功能完全重叠
- 维护两套等价的组织范围计算逻辑会导致混乱和不同步
- 新框架的注解方式更符合「按模块声明策略」的产品设计意图
- `dataScope=1/2/5` 属于角色维度控制，与模块维度的策略模型定位不同，暂保留不改动

改造路径：
1. **新建替代**：`DataPermissionScopeQryExe` 承接组织树计算职责，增强缓存和环检测
2. **内部改接**：`DataPermissionServiceImpl` / `DataScopeQryExe` 中 dataScope=3/4 的分支改为委托调用新服务
3. **逐步迁移**：各使用方从 `DataPermissionService.getAccessibleOrgIds()` 迁移到 `@DataPermission(strategy = ORG)` 注解
4. **最终清理**：待所有调用方迁移完毕后，废弃 DataScopeQryExe 中 org 相关方法

#### 决策 3：循环依赖检测

**在 `getChildOrgIds()` 递归方法中加入 visited Set 做环检测**

```java
private List<Long> getChildOrgIds(Long parentId, Set<Long> visited) {
    if (visited.contains(parentId)) {
        log.warn("检测到组织架构循环引用，parentId={}", parentId);
        return Collections.emptyList();  // 终止遍历
    }
    visited.add(parentId);
    // ... 正常递归逻辑
}
```

对应 PRODUCT.md 行为不变量 #43。

### 五、实施步骤（按顺序）

| 步骤 | 内容 | 涉及模块 | 依赖 |
|------|------|---------|------|
| **Step 1** | 创建数据库迁移脚本 `data_permission.sql` | database | 无 |
| **Step 2** | 新增枚举和注解（DataPermissionStrategy, @DataPermission, @DataPermissionIgnore） | project-api | Step 1 |
| **Step 3** | 新增 DataPermissionDO、DataPermissionMapper 及 XML | project-model | Step 1 |
| **Step 4** | 新增 DataPermissionScopeService 接口及 DataPermissionScopeQryExe 实现（含 Redis 缓存） | project-service | Step 2, 3 |
| **Step 5** | 新增 DataPermissionAspect（核心拦截器） | project-service | Step 2, 4 |
| **Step 5b** | 改造 DataScopeQryExe：dataScope=3/4 分支委托调用新 DataPermissionScopeService，标记 @Deprecated | project-service | Step 4, 5 |
| **Step 6** | 新增管理员操作相关 DTO/Cmd/Query | project-api | Step 1 |
| **Step 7** | 新增分配/回收/查看权限分布的 Executor | project-service | Step 3, 6 |
| **Step 8** | 新增 DataPermissionController | project-web | Step 7 |
| **Step 9** | OrgMapper 增加 CTE 递归查询方法（性能优化） | project-model | 无（可与 Step 3 并行） |
| **Step 10** | 编写单元测试和集成测试 | project-service (test) | Step 5 |

## Testing and Validation

### 单元测试

| 测试类 | 验证的 PRODUCT.md 不变量 |
|--------|--------------------------|
| `DataPermissionScopeQryExeTest` | #15-19（组织层级计算）、#39-41（边界情况） |
| `DataPermissionAspectTest` | #20-26（查询过滤行为）、#44（循环检测） |
| `DataPermissionCacheServiceTest` | #48（缓存 TTL）、缓存清除逻辑 |
| `DataPermissionManageTest` | #31-35（管理员配置权限） |

### 集成测试场景

| 场景 | 步骤 | 预期结果 | 对应不变量 |
|------|------|---------|-----------|
| **策略一：上级看下级组织数据** | 用户 A（组织1）查询 → 组织1 有子组织 2、3 → 组织2 下有数据记录 D1 | A 能看到 D1 | #22 |
| **策略一：平级不可见** | 用户 B（组织2）查询 → 数据记录 D1 属于组织3 | B 不能看到 D1 | #22, #18 |
| **策略二：本人数据可见** | 用户 U1 负责客户 C1 → U1 查询客户列表 | 返回 C1 | #24 |
| **策略二：上级看下级负责的数据** | U1 的上级 U2 查询 → U1 是 U2 的下级 | U2 能看到 C1 | #24 |
| **策略二：无绑定返回空集** | 新用户 U3 无任何绑定 → U3 查询 | 返回空列表（非报错） | #39 |
| **未登录访问** | 清除 session → 调用受控接口 | 返回 401 | #26 |
| **无权访问详情** | 用户 U4 尝试查看 U1 负责的客户 C1（U4 非 U1 上级） | 返回 403 | #25 |
| **缓存生效** | 查询一次 → 修改组织树 → 再次查询（5分钟内） | 仍返回旧数据（缓存期） | #48 |
| **缓存失效** | 清除缓存 → 再次查询 | 返回新数据 | #48 |
| **循环引用防护** | 构造组织 A→B→A 循环 → 查询权限 | 不无限循环，返回部分结果+告警日志 | #43 |
| **审计日志验证** | 管理员分配权限 → 检查 oper_log 表 | 有对应记录 | #36-38 |

### 手动验证步骤

1. 执行 `database/migration/data_permission.sql` 创建表结构
2. 启动应用，确认无启动报错
3. 通过 Swagger 或 Postman 测试各接口
4. 使用 Redis CLI 检查缓存 Key 是否正确生成和过期

## Parallelization

### 可并行的工作项

以下步骤之间无强依赖，可通过并行 sub-agent 加速：

```
Step 1 (DB migration) ──────────────────────────────┐
                                                   ├──→ Step 4 (Scope service)
Step 2 (Annotations) ──────────────────────────────┤         │
                                                   │         ├──→ Step 5 (Aspect) ──→ Step 10 (Tests)
Step 3 (DO/Mapper) ────────────────────────────────┘         │
                                                             │
Step 9 (OrgMapper CTE) ──────────────────────────────────────┤
                                                             │
Step 6 (DTOs) ──────────────────────────┐                   │
                                       ├──→ Step 7 (Executors)
                                       │         │
                                       │         └──→ Step 8 (Controller)
                                       │
Step 9 ─────────────────────────────────┘ (可与 Step 6 并行)
```

**建议并行分组**：

| Agent | 工作内容 | 依赖 | 预计工作量 |
|-------|---------|------|-----------|
| **Agent-A: 基础设施** | Step 1 + Step 2 + Step 3 + Step 9 | 无 | 较大 |
| **Agent-B: 核心逻辑** | Step 4 + Step 5 | Agent-A 完成 | 最大 |
| **Agent-C: 管理功能** | Step 6 + Step 7 + Step 8 | Agent-A 完成 | 中等 |
| **Agent-D: 测试** | Step 10 | Agent-B 完成 | 中等 |

Agent-B 必须等待 Agent-A（需要注解定义和 Mapper 就绪）。Agent-C 只需 Agent-A 的 DO/DTO 定义即可开始。Agent-D 最后执行。

**结论**：由于 Agent-B 和 Agent-C 之间存在部分重叠依赖（都需要 Agent-A 的基础层），且总代码量适中（预估新增 ~15 个文件），并行化的收益有限。**建议顺序执行**，按 Step 1 → 10 依次推进，减少协调成本。

## Risks and Mitigations

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|---------|
| SQL 改写导致原有查询逻辑被破坏 | 功能回归 | 中 | 全量回归测试；@DataPermission 默认不启用（需显式标注） |
| 组织树过深导致递归性能问题 | 查询超时 | 低 | 使用 PostgreSQL CTE 替代 Java 递归（Step 9）；缓存结果 |
| Redis 缓存与数据库不一致 | 权限范围错误 | 中 | TTL 5 分钟短缓存；组织变更时主动清缓存 |
| 大量 IN 子句导致 SQL 性能下降 | 慢查询 | 中 | IN 列表上限控制（超过 500 时拆分查询）；确保索引覆盖 |
| 并发分配导致数据不一致 | 权限错误 | 低 | 最终一致性可接受（PRODUCT.md #44）；无需加锁 |
| 现有 DataScopeQryExe 迁移期间的不一致 | 权限范围错误 | 中 | 分阶段迁移：先改接内部调用（透明替换），再逐步迁移注解方式；每步回归验证 |

## Follow-ups

1. **完成 DataScopeQryExe 迁移**：本阶段完成 dataScope=3/4 的替换后，下一步评估 dataScope=2（自定义部门）和 dataScope=5（仅本人）是否也迁移到新策略框架
2. **废弃旧接口**：待所有调用方迁移到 `@DataPermission` 注解方式后，正式废弃 `DataPermissionService` 和 `DataScopeQryExe`
3. **策略扩展点落地**：实现「仅本人数据」（SELF）和「自定义规则」（CUSTOM）策略枚举值
4. **前端管理界面**：为管理员提供数据权限分配/回收的前端页面（当前仅后端 API）
5. **CUD 权限控制**：本阶段仅实现 Read 权限，后续迭代增加 Create/Update/Delete 的权限校验
6. **数据导出权限**：数据导出接口的权限过滤
