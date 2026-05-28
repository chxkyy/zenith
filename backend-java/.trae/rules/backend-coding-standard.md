JDK21路径在C:\java\jdk-21.0.9
# 后端编码规范

## 2. 命名规范

### 2.1 包命名
- 采用域名倒序的形式，如 `com.zenith.admin`
- 包名使用小写字母，单词之间用点分隔
- 避免使用下划线或其他特殊字符

### 2.2 类命名
- 使用 PascalCase 命名法，如 `UserController`、`RoleService`
- 类名应该是名词或名词短语，清晰表达类的职责
- 控制器类以 `Controller` 结尾
- 服务类以 `Service` 结尾
- 实现类以 `Impl` 结尾
- 数据传输对象以 `DTO` 结尾
- 数据对象以 `DO` 结尾
- 转换器类以 `Convertor` 结尾
- 命令对象（用于修改操作）以 `Cmd` 结尾，如 `NoticeStatusUpdateCmd`
- 查询对象（用于读取操作）以 `Query` 结尾，如 `NoticePageQuery`

### 2.3 方法命名
- 使用 camelCase 命名法，如 `getUserById`、`saveRole`
- 方法名应该是动词或动词短语，清晰表达方法的功能
- 获取数据的方法使用 `get` 或 `find` 前缀
- 保存数据的方法使用 `save` 前缀
- 更新数据的方法使用 `update` 前缀
- 删除数据的方法使用 `delete` 前缀
- 批量操作的方法使用 `batch` 前缀
- 统计操作的方法使用 `count` 前缀

### 2.4 变量命名
- 使用 camelCase 命名法，如 `userDTO`、`roleId`
- 变量名应该清晰表达变量的用途
- 避免使用单个字母的变量名（除了循环变量）
- 常量使用全大写，单词之间用下划线分隔，如 `MAX_PAGE_SIZE`
- 枚举值使用全大写，单词之间用下划线分隔，如 `SUCCESS`、`FAILED`

### 2.5 接口命名
- 使用 PascalCase 命名法，如`RoleService`
- 接口名应该是名词或名词短语，清晰表达接口的职责
- 服务接口可以直接使用功能名称，如 `UserService`

## 4. 类和方法设计

### 4.1 类设计
- 每个类应该有单一的职责
- 类的大小应该适中，避免过长的类
- 避免深层继承，优先使用组合
- 使用接口定义行为，实现类提供具体实现

### 4.2 方法设计
- 方法应该短小精悍，专注于单一功能
- 方法参数不宜过多，建议不超过 5 个
- 方法返回值应该清晰表达方法的结果
- 避免使用 void 返回类型，考虑使用 Response 或 Optional
- 方法应该抛出有意义的异常，而不是捕获后返回错误码

### 4.3 参数验证
- 在控制器层进行参数验证，使用 `@Valid` 或 `@Validated` 注解
- 对于复杂的业务规则验证，在服务层进行
- 验证失败时抛出异常，由全局异常处理器处理

## 5. 异常处理

### 5.1 异常分类
- **RuntimeException**：业务异常和运行时异常，如参数错误、业务规则违反、空指针异常等
- **Exception**：通用异常，如数据库错误、网络错误等

### 5.2 异常处理策略
- 业务异常：在服务层抛出，由全局异常处理器处理
- 系统异常：由全局异常处理器处理，返回通用错误信息
- 运行时异常：尽量避免，通过代码质量控制减少发生

### 5.3 异常抛出规范
- 抛出异常时，应该提供清晰的错误信息
- 错误信息应该简洁明了，不包含敏感信息

### 5.4 全局异常处理器
- 使用 `@RestControllerAdvice` 注解定义全局异常处理器
- 处理 `MethodArgumentNotValidException`、`BindException` 和 `Exception`
- 返回 COLA 标准响应格式，包含错误码和错误信息

## 6. 日志记录

### 6.1 日志级别
- **DEBUG**：详细的调试信息，仅在开发环境使用
- **INFO**：重要的业务信息，如操作成功、系统启动等
- **WARN**：警告信息，如参数不合法、资源不足等
- **ERROR**：错误信息，如系统异常、业务逻辑错误等

### 6.2 日志使用规范
- 使用 SLF4J 作为日志门面
- 在类的顶部定义 logger，如 `private static final Logger logger = LoggerFactory.getLogger(UserService.class);`
- 日志消息应该清晰表达事件的内容和上下文
- 避免在循环中记录大量日志，影响性能
- 避免记录敏感信息，如密码、token 等

### 6.3 日志格式
- 包含时间戳、日志级别、类名、方法名和消息内容
- 对于错误日志，应该包含异常堆栈信息
- 对于业务操作日志，应该包含操作人、操作类型、操作结果等信息

## 7. 测试规范

### 7.1 测试类型
- **单元测试**：测试单个类或方法的功能
- **集成测试**：测试多个组件之间的交互
- **端到端测试**：测试完整的业务流程

### 7.2 测试覆盖率
- 单元测试覆盖率目标：80% 以上
- 重点测试核心业务逻辑和边界情况
- 测试代码应该与生产代码保持同步更新

### 7.3 测试命名规范
- 测试类名：被测试类名 + `Test`，如 `UserServiceTest`
- 测试方法名：`test` + 被测试方法名 + 测试场景，如 `testGetUserById_Success`

### 7.4 测试数据
- 使用测试数据构建器或工厂方法创建测试数据
- 测试数据应该覆盖正常情况和边界情况
- 避免使用硬编码的测试数据，使用常量或配置文件

## 8. 代码审查

### 8.1 代码审查流程
- 提交代码前进行自我审查
- 代码合并前由其他团队成员进行审查
- 审查重点：代码风格、命名规范、业务逻辑、异常处理、性能优化

### 8.2 代码审查标准
- 代码是否符合编码规范
- 代码是否清晰易读
- 业务逻辑是否正确
- 是否存在潜在的性能问题
- 是否存在安全漏洞
- 测试是否覆盖关键路径

### 8.3 代码审查工具
- 使用 IDE 的代码检查工具
- 使用静态代码分析工具，如 SonarQube
- 使用版本控制工具的代码审查功能，如 GitHub 的 Pull Request

## 9. 性能优化

### 9.1 数据库优化
- 使用合适的索引
- 避免全表扫描
- 使用分页查询，避免一次查询过多数据
- 批量操作，减少数据库连接次数
- 合理使用缓存，减少数据库访问

### 9.2 代码优化
- 避免在循环中创建对象
- 合理使用集合类型，如使用 HashMap 进行快速查找
- 避免递归调用，考虑使用迭代
- 合理使用线程池，避免频繁创建线程

### 9.3 API 优化
- 减少 API 调用次数，合并相关操作
- **【强制】仅使用 GET 和 POST 方法，禁止使用 PUT、PATCH、DELETE 等其他HTTP方法**
- 所有操作均通过 POST 请求体传递参数（即使删除操作也使用 POST）
- 使用缓存，减少重复计算
- 合理设置 HTTP 缓存头，减少重复请求

### 9.4 日期时间字段格式
- **【强制】** 所有涉及日期及时间的字段，必须返回 timestamp 格式（即 Unix 时间戳，如 1776691562）
- 前端负责根据需要将 timestamp 解析为不同的日期时间格式进行展示
- 后端避免返回 ISO 格式或其他字符串格式的日期时间

## 10. 安全规范

### 10.1 输入验证
- 对所有用户输入进行验证
- 防止 SQL 注入、XSS 攻击、CSRF 攻击
- 使用参数化查询，避免拼接 SQL 语句

### 10.2 认证与授权
- 使用 Spring Security 进行认证和授权
- 实现基于角色的访问控制（RBAC）
- 保护敏感信息，如密码使用加密存储
- 合理设置会话超时时间

### 10.3 日志安全
- 避免记录敏感信息，如密码、token、个人信息等
- 对日志中的敏感信息进行脱敏处理
- 限制日志文件的访问权限

## 11. 版本控制

### 11.1 分支管理
- **main**：主分支，包含稳定的生产代码
- **develop**：开发分支，包含最新的开发代码
- **feature**：特性分支，用于开发新功能
- **hotfix**：修复分支，用于修复生产环境的 bug

### 11.2 提交规范
- 提交信息应该清晰表达提交的内容
- 提交信息格式：`[类型] 描述`，如 `[feat] 添加用户管理功能`
- 类型包括：feat（新功能）、fix（修复 bug）、docs（文档）、style（代码风格）、refactor（重构）、test（测试）、chore（构建/依赖）
- 提交应该是原子性的，一个提交对应一个功能或修复

### 11.3 代码合并
- 合并前进行代码审查
- 合并前运行测试，确保代码质量
- 避免直接推送代码到 main 分支，使用 Pull Request

## 12. 文档规范

### 12.1 项目文档
- 提供 README.md 文件，描述项目的功能、架构和使用方法
- 提供 API 文档，描述接口的参数、返回值和使用方法
- 提供部署文档，描述项目的部署步骤和配置方法

### 12.2 代码文档
- 使用 Javadoc 注释，生成 API 文档
- 为复杂的业务逻辑提供详细的注释
- 为配置文件提供注释，解释配置项的含义

## 13. 依赖管理

### 13.1 依赖版本
- 使用统一的依赖版本管理，在 pom.xml 中定义版本属性
- 定期更新依赖版本，修复安全漏洞
- 避免使用过时的依赖

### 13.2 依赖范围
- 合理使用依赖范围，如 compile、test、provided
- 避免不必要的依赖，减少项目体积
- 避免依赖冲突，使用 dependencyManagement 统一管理版本

## 14. 构建与部署

### 14.1 构建工具
- 使用 Maven 进行构建
- 配置合理的构建参数，如编译版本、编码格式
- 使用 Maven 插件，如 jacoco（代码覆盖率）、sonar（代码质量）

### 14.2 部署方式
- 使用 Docker 容器化部署
- 配置 CI/CD 流水线，实现自动化构建和部署
- 提供健康检查端点，监控应用状态

## 15. Service 层返回类型规范

### 15.1 核心原则

| 层级 | 职责 | 返回类型 |
|------|------|----------|
| **Controller 层** | HTTP 接口适配、响应格式化、参数校验 | ✅ COLA 响应对象（`MultiResponse`/`SingleResponse`/`PageResponse`/`Response`）|
| **Service 层** | 业务逻辑处理、数据查询与转换 | ❌ **禁止**返回 COLA 响应对象<br>✅ 应返回原始数据类型 |

### 15.2 Service 层允许的返回类型

#### ✅ 推荐返回类型

| 返回类型 | 适用场景 | 示例 |
|---------|---------|------|
| `List<DTO>` | 列表查询（不分页） | `List<UserDTO> listAll()` |
| `DTO` | 单对象查询 | `UserDTO getById(Long id)` |
| `PageInfo<DTO>` | 分页查询（MyBatis PageHelper） | `PageInfo<UserDTO> page(query)` |
| `void` | 增删改操作 | `void save(UserDTO dto)` |
| `boolean` / 基本类型 | 简单判断逻辑 | `boolean hasPermission(...)` |

#### ❌ 禁止返回类型

| 违规类型 | 错误示例 | 正确示例 |
|---------|---------|---------|
| `MultiResponse<T>` | ❌ `MultiResponse<UserDTO> listAll()` | ✅ `List<UserDTO> listAll()` |
| `SingleResponse<T>` | ❌ `SingleResponse<UserDTO> getById()` | ✅ `UserDTO getById(Long id)` |
| `PageResponse<T>` | ❌ `PageResponse<UserDTO> page()` | ✅ `PageInfo<UserDTO> page(query)` |
| `Response` | ❌ `Response save(UserDTO dto)` | ✅ `void save(UserDTO dto)` |

### 15.3 代码示例对比

#### ❌ 错误写法

```java
// Service 接口 - 违反分层原则
public interface UserService {
    MultiResponse<UserDTO> listAll();  // ❌ 返回COLA响应对象
}

// Service 实现 - 违反职责边界
@Service
public class UserServiceImpl implements UserService {
    @Override
    public MultiResponse<UserDTO> listAll() {  // ❌ 返回类型错误
        List<UserDO> users = userMapper.selectList(null);
        List<UserDTO> dtos = convertor.toDTOList(users);
        return MultiResponse.of(dtos);  // ❌ 在Service层包装响应
    }
}

// Controller - 直接透传
@RestController
public class UserController {
    @GetMapping("/list")
    public MultiResponse<UserDTO> list() {
        return userService.listAll();  // ❌ 直接返回Service结果，跳过AOP处理
    }
}
```

**后果**：
- ❌ AOP 切面（如字段翻译）无法正确拦截和处理
- ❌ `@UserName` 注解失效，createUserName/updateUserName 为 null
- ❌ 违反架构分层原则，Service层承担了Controller的职责

#### ✅ 正确写法

```java
// Service 接口 - 只关注业务数据
public interface UserService {
    List<UserDTO> listAll();  // ✅ 返回原始List
}

// Service 实现 - 只负责业务逻辑
@Service
public class UserServiceImpl implements UserService {
    @Override
    public List<UserDTO> listAll() {  // ✅ 返回List
        List<UserDO> users = userMapper.selectList(null);
        List<UserDTO> dtos = convertor.toDTOList(users);
        return dtos;  // ✅ 直接返回数据，不包装响应
    }
}

// Controller - 负责HTTP响应格式化
@RestController
public class UserController {
    @GetMapping("/list")
    public MultiResponse<UserDTO> list() {
        List<UserDTO> list = userService.listAll();  // ✅ 先接收数据
        return MultiResponse.of(list);               // ✅ 再包装成COLA响应
    }
}
```

**优势**：
- ✅ AOP 切面能正确拦截 `List` 类型并执行字段翻译
- ✅ `@UserName` / `@RoleName` / `@DictName` 等注解自动生效
- ✅ 职责清晰：Service管业务，Controller管响应格式

### 15.4 架构流程图

```
┌─────────────────────────────────────────────────────────────┐
│                    Controller 层                             │
│  职责：接收请求 → 调用Service → 包装响应 → 返回给前端         │
│                                                             │
│  @GetMapping("/list")                                       │
│  public MultiResponse<UserDTO> list() {                     │
│      List<UserDTO> data = userService.listAll();            │
│      return MultiResponse.of(data);  ← 在此包装响应          │
│  }                                                          │
└──────────────────────────┬──────────────────────────────────┘
                           │ 返回 List<UserDTO>
                           ▼
┌─────────────────────────────────────────────────────────────┐
│              TranslateAspect (AOP切面)                       │
│  职责：拦截Service返回值 → 执行字段翻译                       │
│                                                             │
│  result instanceof List<?> → 遍历每个DTO                     │
│      → fieldTranslateProcessor.process(dto)                │
│      → UserNameTranslateConverter.translate(dto, field)     │
│      → 查询数据库填充 createUserName/updateUserName         │
└──────────────────────────┬──────────────────────────────────┘
                           │ 返回处理后的 List<UserDTO>
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     Service 层                               │
│  职责：业务逻辑 → 数据查询 → DTO转换                          │
│                                                             │
│  public List<UserDTO> listAll() {                           │
│      1. 查询数据库 (List<UserDO>)                            │
│      2. 转换为DTO   (List<UserDTO>)                         │
│      3. 返回数据    (return dtos)                           │
│      // 不关心响应格式，只关心业务数据                        │
│  }                                                          │
└─────────────────────────────────────────────────────────────┘
```

### 15.5 强制规则清单

#### 【强制】Service 层禁止事项

- ❌ **禁止**导入和使用 `com.alibaba.cola.dto.*` 相关类
- ❌ **禁止**方法签名返回 `MultiResponse<T>` / `SingleResponse<T>` / `PageResponse<T>` / `Response`
- ❌ **禁止**在实现类中调用 `MultiResponse.of()` / `SingleResponse.of()` 等静态方法
- ❌ **禁止**在 Service 层进行 HTTP 响应格式化

#### 【强制】Service 层必须遵守

- ✅ 方法签名使用纯数据类型（`List<T>` / `T` / `PageInfo<T>` / `void`）
- ✅ 实现类直接返回转换后的数据对象
- ✅ 将响应包装的责任交给 Controller 层

#### 【强制】Controller 层必须遵守

- ✅ 所有接口必须返回 COLA 响应对象
- ✅ 在调用 Service 后立即包装成对应的 COLA 响应
- ✅ 使用标准构造方法：`MultiResponse.of(data)` / `SingleResponse.of(data)` 等

### 15.6 异常处理说明

| 场景 | 处理方式 |
|------|---------|
| **业务异常** | Service 层抛出 `BizException`，由全局异常处理器捕获并转换为失败响应 |
| **系统异常** | Service 层抛出 `SysException` 或运行时异常，由全局异常处理器统一处理 |
| **正常返回** | Service 返回数据对象，Controller 包装成成功响应 |

```java
// Service 层 - 抛出业务异常
public void delete(Long id) {
    if (id == null) {
        throw new BizException("USER_001", "用户ID不能为空");
    }
    userMapper.deleteById(id);
}

// Controller 层 - 无需try-catch，异常由全局处理器处理
@DeleteMapping("/{id}")
public Response delete(@PathVariable Long id) {
    userService.delete(id);
    return Response.buildSuccess();
}
```

### 15.7 编译检查与验证

可通过以下正则表达式扫描代码库，检测违规情况：

```regex
# 检测 Service 接口中违规的返回类型
(public|private|protected)\s+(MultiResponse|SingleResponse|PageResponse|Response)<[^>]+>\s+\w+\(

# 检测 ServiceImpl 中违规的 return 语句
return\s+(MultiResponse|SingleResponse|PageResponse|Response)\.(of|buildSuccess|buildFailure)
```

建议在项目中添加 Code Inspection 规则：
- 当在 `*ServiceImpl.java` 中发现 `return MultiResponse.of(...)` 时显示警告
- 当在 `*Service.java` 接口中发现返回类型包含 `Response` 时显示错误

### 15.8 违规后果与影响

| 影响维度 | 说明 |
|---------|------|
| **功能缺陷** | AOP 切面（字段翻译）失效，导致 `@UserName` 等注解不生效 |
| **数据缺失** | 前端展示时 createUserName/updateUserName 等字段为 null |
| **架构混乱** | Service 层承担 Controller 职责，违反单一职责原则 |
| **维护困难** | 新开发者容易效仿错误模式，导致问题扩散 |
| **测试复杂度增加** | Service 层单元测试需要模拟 HTTP 响应结构 |

## 16. Executor 层规范

### 16.1 Executor 层职责

Executor 层位于 Service 层和 Model 层之间，负责执行具体的业务逻辑。遵循 COLA 架构的分层设计，将复杂的业务逻辑从 Service 层下沉到 Executor 层。

```
┌─────────────────────────────────────────────────────────────┐
│                    Controller 层                             │
│  职责：接收请求 → 调用Service → 包装响应 → 返回给前端         │
└──────────────────────────┬──────────────────────────────────┘
                           │ DTO
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Service 层                                │
│  职责：用例编排，调用 Executor                               │
└──────────────────────────┬──────────────────────────────────┘
                           │ DTO / 基本类型
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Executor 层                               │
│  职责：执行具体业务逻辑，DO 操作，DTO 转换                    │
└──────────────────────────┬──────────────────────────────────┘
                           │ DO
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Model 层                                  │
│  职责：数据库操作                                             │
└─────────────────────────────────────────────────────────────┘
```

### 16.2 Executor 分类

| 类型 | 后缀 | 职责 | 示例 |
|------|------|------|------|
| **命令执行器** | `CmdExe` | 执行写操作（增、删、改） | `UserAddCmdExe`、`UserDeleteCmdExe` |
| **查询执行器** | `QryExe` | 执行读操作（查询） | `UserGetQryExe`、`UserListQryExe` |

### 16.3 命名规范

#### 类命名

| 操作类型 | 命名格式 | 示例 |
|---------|---------|------|
| 新增 | `{Entity}AddCmdExe` | `UserAddCmdExe` |
| 修改 | `{Entity}UpdateCmdExe` | `UserUpdateCmdExe` |
| 删除 | `{Entity}DeleteCmdExe` | `UserDeleteCmdExe` |
| 单对象查询 | `{Entity}GetQryExe` | `UserGetQryExe` |
| 列表查询 | `{Entity}ListQryExe` | `UserListQryExe` |
| 分页查询 | `{Entity}PageQryExe` | `UserPageQryExe` |

#### 方法命名

Executor 层统一使用 `execute` 方法名：

```java
public void execute(UserAddCmd cmd) { }           // 写操作，无返回值
public UserDTO execute(UserGetQry qry) { }        // 读操作，返回 DTO
public List<UserDTO> execute(UserListQry qry) { } // 读操作，返回 List
```

### 16.4 分层职责对比

| 层级 | 职责 | 返回类型 | 是否操作 DO |
|------|------|----------|-------------|
| **Controller** | HTTP 适配、响应格式化 | `Response` / `SingleResponse` / `MultiResponse` | ❌ 禁止 |
| **Service** | 用例编排、调用 Executor | DTO / List / void / boolean | ❌ 禁止 |
| **Executor** | 具体业务逻辑、数据转换 | DTO / List / void / boolean | ✅ 允许 |
| **Mapper** | 数据库操作 | DO / List\<DO\> | ✅ 允许 |

### 16.5 代码示例

#### ❌ 错误写法：Service 层直接操作 DO

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserConvertor userConvertor;

    @Override
    public UserDTO getById(Long id) {
        UserDO userDO = userMapper.selectById(id);  // ❌ Service 层直接操作 DO
        return userConvertor.toDTO(userDO);
    }

    @Override
    public void add(UserAddCmd cmd) {
        UserDO userDO = new UserDO();               // ❌ Service 层直接操作 DO
        userDO.setName(cmd.getName());
        userMapper.insert(userDO);
    }
}
```

#### ✅ 正确写法：Service 层编排，Executor 层执行

```java
// Service 接口
public interface UserService {
    UserDTO getById(Long id);
    void add(UserAddCmd cmd);
}

// Service 实现 - 只做编排
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserGetQryExe userGetQryExe;
    private final UserAddCmdExe userAddCmdExe;

    @Override
    public UserDTO getById(Long id) {
        UserGetQry qry = new UserGetQry();
        qry.setUserId(id);
        return userGetQryExe.execute(qry);  // ✅ 调用 Executor
    }

    @Override
    public void add(UserAddCmd cmd) {
        userAddCmdExe.execute(cmd);  // ✅ 调用 Executor
    }
}

// 查询 Executor
@Component
@RequiredArgsConstructor
public class UserGetQryExe {

    private final UserMapper userMapper;
    private final UserConvertor userConvertor;

    public UserDTO execute(UserGetQry qry) {
        UserDO userDO = userMapper.selectById(qry.getUserId());  // ✅ Executor 层操作 DO
        if (userDO == null) {
            throw new BizException("USER_NOT_FOUND", "用户不存在");
        }
        return userConvertor.toDTO(userDO);  // ✅ Executor 层转换 DTO
    }
}

// 命令 Executor
@Component
@RequiredArgsConstructor
public class UserAddCmdExe {

    private final UserMapper userMapper;

    public void execute(UserAddCmd cmd) {
        if (userMapper.countByEmail(cmd.getEmail()) > 0) {
            throw new BizException("EMAIL_EXISTS", "邮箱已存在");
        }
        
        UserDO userDO = new UserDO();  // ✅ Executor 层操作 DO
        userDO.setName(cmd.getName());
        userDO.setEmail(cmd.getEmail());
        userMapper.insert(userDO);
    }
}
```

### 16.6 目录结构

```
project-service/src/main/java/com/zenith/admin/
├── executor/                              # Executor 层
│   ├── UserAddCmdExe.java                 # 新增用户命令
│   ├── UserUpdateCmdExe.java              # 修改用户命令
│   ├── UserDeleteCmdExe.java              # 删除用户命令
│   ├── UserGetQryExe.java                 # 获取单个用户查询
│   ├── UserListQryExe.java                # 获取用户列表查询
│   └── UserPageQryExe.java                # 分页查询用户
├── service/
│   ├── UserService.java                   # Service 接口
│   └── impl/
│       └── UserServiceImpl.java           # Service 实现（只做编排）
├── UserConvertor.java                     # DO ↔ DTO 转换器
└── ...
```

### 16.7 强制规则清单

#### 【强制】Service 层禁止事项

- ❌ **禁止**直接操作 DO（`UserDO`、`RoleDO` 等）
- ❌ **禁止**直接调用 Mapper 进行数据库操作
- ❌ **禁止**在 Service 层进行 DO → DTO 转换

#### 【强制】Service 层必须遵守

- ✅ 只做用例编排，调用 Executor
- ✅ 将业务逻辑下沉到 Executor 层
- ✅ 返回 Executor 的执行结果

#### 【强制】Executor 层必须遵守

- ✅ 负责具体的业务逻辑实现
- ✅ 可以操作 DO 和调用 Mapper
- ✅ 负责 DO → DTO 的转换
- ✅ 返回 DTO 或基本类型，**禁止返回 DO**

### 16.8 Domain Service 规范

对于领域服务（如 `WorkflowDomainService`），同样需要遵循分层规范：

#### ❌ 错误写法：接口中使用 DO

```java
public interface WorkflowDomainService {
    List<Long> resolveApprovers(NodeTemplateDO node, Long initiatorId);  // ❌ 参数使用 DO
    void createTasksForNode(ProcessInstanceDO instance, NodeTemplateDO node);  // ❌ 参数使用 DO
    List<TaskDO> getTasksByProcessInstanceAndNode(Long processInstanceId, Integer nodeOrder);  // ❌ 返回 DO
}
```

#### ✅ 正确写法：接口中使用 DTO 或基本类型

```java
public interface WorkflowDomainService {
    List<Long> resolveApprovers(Long nodeTemplateId, Long initiatorId);  // ✅ 使用 ID
    void createTasksForNode(Long processInstanceId, Long nodeTemplateId);  // ✅ 使用 ID
    List<TaskDTO> getTasksByProcessInstanceAndNode(Long processInstanceId, Integer nodeOrder);  // ✅ 返回 DTO
}
```

#### 实现层结构

```
project-service/src/main/java/com/zenith/admin/
├── executor/
│   ├── WorkflowResolveApproversQryExe.java    # 解析审批人
│   ├── WorkflowCreateTaskCmdExe.java          # 创建任务
│   ├── WorkflowCreateApprovalRecordCmdExe.java # 创建审批记录
│   ├── WorkflowGetTasksQryExe.java            # 查询任务
│   ├── WorkflowCheckApprovalQryExe.java       # 检查审批状态
│   └── WorkflowTerminateTaskCmdExe.java       # 终止任务
├── service/
│   └── impl/
│       └── WorkflowDomainServiceImpl.java     # 只做编排
└── TaskConvertor.java                         # DO ↔ DTO 转换器
```

### 16.9 架构优势

| 优势 | 说明 |
|------|------|
| **职责清晰** | Service 编排、Executor 执行、Mapper 持久化 |
| **易于测试** | Executor 可独立单元测试，无需模拟整个 Service |
| **代码复用** | 多个 Service 可复用同一个 Executor |
| **易于维护** | 业务逻辑集中在 Executor，修改影响范围小 |
| **符合 COLA** | 遵循整洁架构分层原则 |

## 17. 总结

本编码规范旨在提高代码质量和可维护性，确保团队成员在开发过程中遵循一致的标准。所有团队成员应该熟悉并遵守本规范，共同维护高质量的代码库。

规范会根据项目的发展和技术的进步不断更新，团队成员可以提出建议和改进意见，使规范更加完善和实用。