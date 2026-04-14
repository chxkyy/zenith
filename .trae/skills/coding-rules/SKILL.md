# 项目代码规范

编写代码时必须遵循以下规范。

## 项目架构

采用 DDD 分层架构：
- `adapter/controller` - 适配层，对外API接口
- `application/service` - 应用层，业务服务
- `domain/entity` - 领域层，实体对象
- `infrastructure/persistence` - 基础设施层，数据访问
- `common/dto` - 数据传输对象
- `common/mapper` - MapStruct 对象转换

## 命名规范

### 类命名
| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 实体类 | {表名}DO | SysUserDO |
| DTO | {表名}DTO | SysUserDTO |
| Mapper接口 | {表名}Mapper | SysUserMapper |
| Service | {表名}Service | SysUserService |
| Controller | {表名}Controller | SysUserController |

### 方法命名
| 操作 | 命名规则 |
|------|----------|
| 查询单个 | findById |
| 查询列表 | findAll / search |
| 新增 | create |
| 更新 | update |
| 删除 | delete |
| 批量删除 | batchDelete |

## API 规范

### URL 规范
- **URL 必须全部小写**：`/api/system/user`（禁止使用大写或驼峰）
- 多单词用连字符：`/api/system/user-role`
- URL 中禁止包含动态参数，如 `/api/user/{id}` ❌

### 参数传递规范

- **禁止在 URL 路径中传递参数**（路径参数）
- GET 请求：参数通过 Query String 传递 `?id=1&name=test`
- POST 请求：参数通过 Request Body（JSON）传递

| 方法 | 参数位置 | 示例 |
|------|----------|------|
| GET | Query String | `GET /api/system/user?id=1` ✅ |
| POST | Request Body | `POST /api/system/user` + Body ✅ |
| POST (更新) | Request Body | `POST /api/system/user/update` + Body ✅ |
| POST (删除) | Request Body | `POST /api/system/user/delete` + Body ✅ |

**错误示例**：
```
GET /api/system/user/{id}           ❌ 路径参数
DELETE /api/system/user/{id}        ❌ 使用了 DELETE 方法
PUT /api/system/user/{id}           ❌ 使用了 PUT 方法
POST /api/system/user/{id}          ❌ 路径参数
```

**正确示例**：
```
GET /api/system/user?id=1                ✅ GET + Query 参数
POST /api/system/user                    ✅ POST + Body（创建）
POST /api/system/user/update             ✅ POST + Body（更新，Body含id）
POST /api/system/user/delete             ✅ POST + Body（删除，Body含id）
POST /api/system/user/batch-delete       ✅ POST + Body（批量删除）
```

### 多参数请求规范

**当请求参数超过 2 个时，必须使用 POST + @RequestBody + @Validated**

- 参数超过 2 个：必须封装为请求 DTO，使用 `@RequestBody` 接收，`@Validated` 校验
- 参数 ≤ 2 个：可使用 `@RequestParam` 接收

```java
// ❌ 错误：参数超过2个，仍用 @RequestParam
@GetMapping("/search")
public MultiResponse<SysUserDTO> search(
        @RequestParam String username,
        @RequestParam String name,
        @RequestParam String phone,
        @RequestParam Long deptId) { ... }

// ✅ 正确：参数超过2个，封装为请求DTO
@PostMapping("/search")
public PageResponse<SysUserDTO> search(@RequestBody @Validated SysUserQry qry) { ... }
```

### DTO 规范

**请求 DTO 和响应 DTO 必须分开定义，不能共用同一个类**

| 类型 | 命名规则 | 用途 | 示例 |
|------|----------|------|------|
| 操作请求 DTO | `{实体}{操作}Cmd` | 创建、更新、删除等操作 | `SysUserCreateCmd`、`SysUserModifyCmd` |
| 查询请求 DTO | `{实体}Qry` | 查询条件 | `SysUserQry`、`SysUserListQry` |
| 响应 DTO | `{实体}DTO` 或 `{实体}Response` | 返回响应数据 | `SysUserDTO`、`SysUserDetailResponse` |

```java
// ❌ 错误：请求和响应共用一个类
@PostMapping
public SingleResponse<SysUserDTO> create(@RequestBody @Validated SysUserDTO request) { ... }

// ✅ 正确：请求和响应分开
@PostMapping
public SingleResponse<SysUserDTO> create(@RequestBody @Validated SysUserCreateCmd cmd) { ... }
```

#### 操作请求 Cmd 示例

```java
@Data
@ApiModel(value = "用户管理-创建用户")
public class SysUserCreateCmd implements Serializable {
    @Serial
    private static final long serialVersionUID = -1L;

    @ApiModelProperty("用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    private String password;

    @ApiModelProperty("姓名")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @ApiModelProperty("手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty(value = "操作人", hidden = true)
    private Long operateUserId;
}
```

#### 修改请求 Cmd 示例

```java
@Data
@ApiModel(value = "用户管理-修改用户")
public class SysUserModifyCmd implements Serializable {
    @Serial
    private static final long serialVersionUID = -1L;

    @ApiModelProperty("用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @ApiModelProperty("姓名")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty(value = "操作人", hidden = true)
    private Long operateUserId;
}
```

#### 查询请求 Qry 示例

**分页查询 Qry 必须继承 `PageQuery`**，自动获得 `pageIndex` 和 `pageSize` 分页参数。

```java
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "用户管理-查询条件")
public class SysUserQry extends PageQuery {
    @Serial
    private static final long serialVersionUID = -1L;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("状态")
    private String status = "1";

    @ApiModelProperty("批量删除主键id（多个逗号隔开）")
    private String ids;
}
```
```

### HTTP 方法

**项目只允许使用 GET 和 POST 两种 HTTP 方法，禁止使用 PUT、DELETE、PATCH 等其他方法。**

| 方法 | 用途 | 说明 |
|------|------|------|
| GET | 查询资源 | 参数通过 Query String 传递 |
| POST | 创建/更新/删除资源 | 参数通过 Request Body 传递 |

**方法映射规则：**
- 查询操作 → GET
- 创建操作 → POST
- 更新操作 → POST（URL 添加 `/update` 后缀）
- 删除操作 → POST（URL 添加 `/delete` 后缀）
- 批量删除 → POST（URL 添加 `/batch-delete` 后缀）

### 统一响应格式

**必须使用 COLA 框架的响应类**，禁止使用其他响应格式。

#### 响应类型

| 类型 | 用途 | 示例 |
|------|------|------|
| `SingleResponse<T>` | 单个对象响应 | 查询详情 |
| `MultiResponse<T>` | 列表响应 | 查询列表 |
| `PageResponse<T>` | 分页响应 | 分页查询 |

#### SingleResponse - 单个对象

```java
@GetMapping
public SingleResponse<SysUserDTO> findById(@RequestParam Long id) {
    SysUserDTO user = sysUserService.findById(id);
    return SingleResponse.of(user);
}
```

#### MultiResponse - 列表

```java
@GetMapping
public MultiResponse<SysUserDTO> findAll() {
    List<SysUserDTO> list = sysUserService.findAll();
    return MultiResponse.of(list);
}
```

#### PageResponse - 分页

**分页查询使用 POST 方法，Qry 继承 `PageQuery`**

```java
@Operation(summary = "分页查询用户")
@PostMapping("/search")
public PageResponse<SysUserDTO> search(@RequestBody @Validated SysUserQry qry) {
    PageInfo<SysUserDTO> pageInfo = sysUserService.search(qry);
    return PageResponse.of(pageInfo.getList(), pageInfo.getTotal());
}
```

**Service 层示例：**

```java
public PageInfo<SysUserDTO> search(SysUserQry qry) {
    PageInfo<SysUserDO> pageInfo = PageHelper.startPage(qry.getPageIndex(), qry.getPageSize())
            .doSelectPageInfo(() -> sysUserMapper.selectByCondition(qry));
    return SysUserConverter.INSTANCE.toDTOPage(pageInfo);
}
```

#### 错误响应

```java
// 业务异常
return SingleResponse.buildFailure("USER_NOT_FOUND", "用户不存在");

// 系统异常
return SingleResponse.buildFailure("SYSTEM_ERROR", "系统异常");
```

**禁止使用**：
- `ResponseEntity<T>` ❌
- `Result<T>` ❌
- `Map<String, Object>` ❌
- 直接返回 DTO/List ❌

#### 响应结构

SingleResponse / MultiResponse：
```json
{
  "code": "0",
  "message": "success",
  "data": { ... }
}
```

PageResponse：
```json
{
  "code": "0",
  "message": "success",
  "data": [...],
  "total": 100,
  "pageSize": 20,
  "pageIndex": 1
}
```

## 并发处理规范

### 线程池创建

**禁止使用 `Executors` 创建线程池**，应使用 `ThreadPoolExecutor` 手动创建，明确各参数含义。

| 参数 | 说明 |
|------|------|
| `corePoolSize` | 核心线程数 |
| `maximumPoolSize` | 最大线程数 |
| `keepAliveTime` | 空闲线程存活时间 |
| `workQueue` | 工作队列 |
| `threadFactory` | 线程工厂（建议自定义线程名） |
| `handler` | 拒绝策略 |

```java
// ❌ 错误：使用Executors创建线程池（可能导致OOM）
ExecutorService executor = Executors.newFixedThreadPool(10);

// ✅ 正确：手动创建ThreadPoolExecutor
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5,                      // corePoolSize
    10,                     // maximumPoolSize
    60L,                    // keepAliveTime
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100),
    new ThreadFactoryBuilder().setNamePrefix("user-pool-").build(),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

### SimpleDateFormat 线程安全

`SimpleDateFormat` 是线程不安全的类，**禁止定义为 static 变量**。

```java
// ❌ 错误：SimpleDateFormat定义为static（线程不安全）
private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

// ✅ 正确：使用DateTimeFormatter（线程安全）
private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

// ✅ 正确：使用ThreadLocal
private static final ThreadLocal<SimpleDateFormat> sdf = 
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
```

### 锁使用规范

| 场景 | 推荐方案 |
|------|----------|
| 一般场景 | `synchronized` 代码块 |
| 高并发场景 | `ReentrantLock` |
| 分布式场景 | 分布式锁（Redis/Zookeeper） |

**加锁顺序（性能从低到高）：**
1. Lock > synchronized 代码块 > synchronized 方法

```java
// ✅ 正确：一般场景使用synchronized
public void process() {
    synchronized (this) {
        // 临界区代码
    }
}

// ✅ 正确：高并发场景使用ReentrantLock
private final ReentrantLock lock = new ReentrantLock();

public void process() {
    lock.lock();
    try {
        // 临界区代码
    } finally {
        lock.unlock();
    }
}
```

### 并发集合

高并发场景下，使用 `ConcurrentHashMap` 替代 `HashMap`。

```java
// ❌ 错误：高并发场景使用HashMap
private static final Map<String, Object> cache = new HashMap<>();

// ✅ 正确：使用ConcurrentHashMap
private static final Map<String, Object> cache = new ConcurrentHashMap<>();
```

## 数据库规范

### 表命名
- 使用下划线命名
- 统一使用 `sys_` 前缀
- 示例：`sys_user`、`sys_role`、`sys_menu`

### 字段命名
- 主键：`id`
- 创建时间：`create_time`
- 更新时间：`update_time`
- 逻辑删除：`deleted`

### Mapper 方法命名规范

**Mapper 接口方法前缀必须使用以下四种：**

| 前缀 | 用途 | 示例 |
|------|------|------|
| `select` | 查询操作 | `selectById`、`selectList`、`selectCount` |
| `insert` | 新增操作 | `insert`、`insertBatch` |
| `update` | 更新操作 | `update`、`updateStatus`、`updateById` |
| `delete` | 删除操作 | `delete`、`deleteById`、`deleteBatch` |

**禁止使用的前缀：**
- ❌ `find`（应改为 `select`）
- ❌ `get`（应改为 `select`）
- ❌ `query`（应改为 `select`）
- ❌ `count`（应改为 `selectCount`）
- ❌ `clear`（应改为 `delete` 或 `update`）
- ❌ `save`（应改为 `insert`）
- ❌ `search`（应改为 `select`）
- ❌ `list`（应改为 `select`）

**示例对比：**

```java
// ❌ 错误命名
SysUserDO findById(Long id);
SysUserDO findByUsername(String username);
List<SysUserDO> findAll();
List<SysUserDO> search(String name);
Integer countByDeptId(Long deptId);
void save(SysUserDO user);
void clearDeptByDeptId(Long deptId);

// ✅ 正确命名
SysUserDO selectById(Long id);
SysUserDO selectByUsername(String username);
List<SysUserDO> selectAll();
List<SysUserDO> selectByCondition(SysUserQry qry);
Integer selectCountByDeptId(Long deptId);
void insert(SysUserDO user);
void deleteDeptIdByDeptId(Long deptId);
```

## 代码规范

### 注入方式
使用构造器注入，配合 `@RequiredArgsConstructor`：
```java
@Service
@RequiredArgsConstructor
public class SysUserService {
    private final SysUserMapper sysUserMapper;
}
```

### Controller 规范
```java
@RestController
@RequestMapping("/api/system/user")
@Tag(name = "用户管理", description = "用户管理API")
@RequiredArgsConstructor
public class SysUserController {
    private final SysUserService sysUserService;

    @Operation(summary = "获取用户详情")
    @GetMapping
    public SingleResponse<SysUserDTO> findById(@RequestParam Long id) {
        SysUserDTO user = sysUserService.findById(id);
        return SingleResponse.of(user);
    }

    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public MultiResponse<SysUserDTO> findAll() {
        List<SysUserDTO> list = sysUserService.findAll();
        return MultiResponse.of(list);
    }

    @Operation(summary = "分页查询用户")
    @PostMapping("/search")
    public PageResponse<SysUserDTO> search(@RequestBody @Validated SysUserQry qry) {
        PageInfo<SysUserDTO> pageInfo = sysUserService.search(qry);
        return PageResponse.of(pageInfo.getList(), pageInfo.getTotal());
    }

    @Operation(summary = "创建用户")
    @PostMapping
    public SingleResponse<String> create(@RequestBody @Validated SysUserCreateCmd cmd) {
        sysUserService.create(cmd);
        return SingleResponse.of("创建成功");
    }

    @Operation(summary = "更新用户")
    @PostMapping("/update")
    public SingleResponse<String> update(@RequestBody @Validated SysUserModifyCmd cmd) {
        sysUserService.update(cmd);
        return SingleResponse.of("更新成功");
    }

    @Operation(summary = "删除用户")
    @PostMapping("/delete")
    public SingleResponse<String> delete(@RequestBody @Validated SysUserDeleteCmd cmd) {
        sysUserService.delete(cmd.getId());
        return SingleResponse.of("删除成功");
    }

    @Operation(summary = "批量删除用户")
    @PostMapping("/batch-delete")
    public SingleResponse<String> batchDelete(@RequestBody @Validated SysUserBatchDeleteCmd cmd) {
        sysUserService.batchDelete(cmd.getIds());
        return SingleResponse.of("删除成功");
    }
}
```

### 异常处理
- 业务异常抛出 `RuntimeException`
- 错误信息使用中文描述
- Controller 不捕获异常，统一全局处理

## Git 提交规范

格式：`<type>(<scope>): <subject>`

| 类型 | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug修复 |
| docs | 文档更新 |
| refactor | 代码重构 |
| test | 测试用例 |
| chore | 构建/工具 |

示例：`feat(user): 新增用户搜索功能`

## 前端规范

### 时间格式化规范

**后端返回时间字段为毫秒时间戳（timestamp），前端负责格式化展示。**

| 展示场景 | 格式 | 示例 |
|----------|------|------|
| 日期时间 | `YYYY-MM-DD HH:mm:ss` | `2026-04-07 11:41:13` |
| 仅日期 | `YYYY-MM-DD` | `2026-04-07` |
| 仅时间 | `HH:mm:ss` | `11:41:13` |

**格式化函数：**

```javascript
/**
 * 格式化时间戳为日期时间字符串
 * @param {number} timestamp - 毫秒时间戳
 * @param {string} format - 格式，默认 'YYYY-MM-DD HH:mm:ss'
 * @returns {string} 格式化后的字符串
 */
function formatTimestamp(timestamp, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}
```

**在表格列中使用：**

```jsx
{
  title: '创建时间',
  dataIndex: 'createTime',
  key: 'createTime',
  width: 160,
  render: (timestamp) => formatTimestamp(timestamp)
}
```

**在详情中使用：**

```jsx
<p><strong>创建时间：</strong>{formatTimestamp(record.createTime)}</p>
```
