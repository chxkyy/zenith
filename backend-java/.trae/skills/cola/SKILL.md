---
name: cola
description: COLA 架构规范。当使用 COLA（整洁面向对象分层架构）进行 Java/Spring Boot 项目开发时使用
when_to_use: 创建 COLA 项目、定义分层结构、使用 COLA 组件、或进行 DDD 分层架构设计时
paths: "**/*.java"
---

# COLA 架构规范

使用 COLA 架构进行项目开发时必须遵循以下规范。

## 一、COLA 概述

**COLA = Clean Object-Oriented and Layered Architecture（整洁面向对象分层架构）**

由阿里巴巴开源的 Java 应用架构框架，目前版本为 **COLA V5**。

### 核心理念

- 以业务为核心
- 解耦外部依赖
- 分离业务复杂度和技术复杂度

## 二、分层架构

### 架构图

```
┌─────────────────────────────────────┐
Driving Adapter: │ 浏览器 │ 定时器 │ 消息队列 │
└─────────────────────────────────────┘
            ↓
┌─────────────────────────────────────┐
                 │ Web 层             │
                 │ controller │ scheduler │ consumer │
└─────────────────────────────────────┘
            ↓ DTO
┌─────────────────────────────────────┐
                 │ Service 层         │
                 │ service │ executor │
└─────────────────────────────────────┘
            ↓ DO
┌─────────────────────────────────────┐
                 │ Model 层           │
                 │ mapper │ config │
└─────────────────────────────────────┘
            ↓
Driven Adapter: │ DB │ Search │ RPC │
```

### 依赖方向

```
Web → Service → Model
  ↘       ↙
   Api
```

### 各层职责

| 层 | 职责 | 实现 |
|---|------|------|
| **Web** | 接收外部请求，转换为 Response | Controller, Scheduler, Consumer |
| **Service** | 用例编排，返回业务数据 | Service, CmdExe, QryExe |
| **Model** | 技术实现 | Mapper, Config, DO |
| **Api** | DTO 定义 | Command, Query, DTO |

## 三、目录结构

```
project-name/
├── project-web/              # Controller
│   └── com/company/project/web/
│       └── *Controller.java
├── project-service/          # Service 实现
│   └── com/company/project/
│       ├── command/          # *CmdExe.java
│       │   └── query/        # *QryExe.java
│       └── service/          # *Service.java
├── project-api/              # DTO 定义
│   └── com/company/project/
│       └── dto/
│           ├── command/      # *Cmd.java
│           ├── query/        # *Qry.java
│           └── dataobject/   # *DTO.java
├── project-model/            # 数据模型
│   └── com/company/project/
│       ├── mapper/           # *Mapper.java
│       └── dataobject/       # *DO.java（数据库映射）
└── start/                    # 启动模块
```

## 四、命名规范

| 类型 | 后缀 | 位置 | 示例 |
|------|------|------|------|
| 命令 | `Cmd` | api/dto/command | `UserAddCmd` |
| 查询 | `Qry` | api/dto/query | `UserListQry` |
| 命令执行器 | `CmdExe` | service/command | `UserAddCmdExe` |
| 查询执行器 | `QryExe` | service/command/query | `UserListQryExe` |
| 数据传输对象 | `DTO` | api/dto/dataobject | `UserDTO` |
| 数据对象 | `DO` | model/dataobject | `UserDO` |
| 服务 | `Service` | service/service | `UserService` |
| Mapper | `Mapper` | model/mapper | `UserMapper` |

## 五、DTO 与 DO 的用途

### 1. DTO（数据传输对象）- API 响应

```java
// api/dto/dataobject/UserDTO.java
@Data
public class UserDTO implements Serializable {
    private Long id;
    private String name;
    private String email;
    private Long createTime;
}
```

### 2. DO（数据对象）- 数据库映射

```java
// model/dataobject/UserDO.java
@Data
@TableName("sys_user")
public class UserDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createTime;
}
```

## 六、COLA 组件

### 组件总览

| 组件 | Maven ArtifactId | 功能 |
|------|------------------|------|
| DTO 组件 | `cola-component-dto` | Response、Command、Query、PageResponse |
| CatchLog 组件 | `cola-component-catchlog-starter` | @CatchAndLog 异常捕获和日志 |
| StateMachine 组件 | `cola-component-statemachine` | 状态机 |

### Maven BOM

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cola</groupId>
            <artifactId>cola-components-bom</artifactId>
            <version>5.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 七、Response 响应规范

### Response 类型

```java
// 基础响应
Response.buildSuccess();
Response.buildFailure("ERROR_CODE", "错误信息");

// 单对象响应
SingleResponse<UserDTO> response = SingleResponse.of(userDTO);

// 多对象响应
MultiResponse<UserDTO> response = MultiResponse.of(userList);

// 分页响应
PageResponse<UserDTO> response = PageResponse.of(userList, totalCount, pageSize, pageIndex);
```

## 八、Command 与 Query

### Command（写操作）

```java
// api/dto/command/UserAddCmd.java
@Data
public class UserAddCmd extends Command {
    private String name;
    private String email;
}
```

### Query（读操作）

```java
// api/dto/query/UserListQry.java
@Data
public class UserListQry extends Query {
    private String keyword;
}

// api/dto/query/UserPageQry.java
@Data
public class UserPageQry extends PageQuery {
    private String status;
}
```

## 九、分层职责详解

### Controller（Web 层）

**职责**：接收请求、调用 Service、转换为 Response

```java
// web/UserController.java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public Response addUser(@RequestBody @Validated UserAddCmd cmd) {
        userService.addUser(cmd);
        return Response.buildSuccess();
    }

    @GetMapping
    public SingleResponse<UserDTO> getUser(@RequestParam Long id) {
        UserGetQry qry = new UserGetQry();
        qry.setUserId(id);
        UserDTO user = userService.getUser(qry);
        return SingleResponse.of(user);
    }

    @GetMapping
    public MultiResponse<UserDTO> listUsers(UserListQry qry) {
        List<UserDTO> users = userService.listUsers(qry);
        return MultiResponse.of(users);
    }

    @PostMapping("/search")
    public PageResponse<UserDTO> searchUsers(@RequestBody UserPageQry qry) {
        PageInfo<UserDTO> pageInfo = userService.searchUsers(qry);
        return PageResponseUtils.of(pageInfo);
    }
}
```

### Service（Service 层）

**职责**：用例编排，返回业务数据，**不返回 Response**

```java
// service/UserService.java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAddCmdExe userAddCmdExe;
    private final UserGetQryExe userGetQryExe;
    private final UserListQryExe userListQryExe;
    private final UserSearchQryExe userSearchQryExe;

    public void addUser(UserAddCmd cmd) {
        userAddCmdExe.execute(cmd);
    }

    public UserDTO getUser(UserGetQry qry) {
        return userGetQryExe.execute(qry);
    }

    public List<UserDTO> listUsers(UserListQry qry) {
        return userListQryExe.execute(qry);
    }

    public PageInfo<UserDTO> searchUsers(UserPageQry qry) {
        return userSearchQryExe.execute(qry);
    }
}
```

### Executor（Service 层）

**职责**：执行具体业务逻辑，返回业务数据

```java
// service/command/UserAddCmdExe.java
@Component
@RequiredArgsConstructor
public class UserAddCmdExe {
    private final UserMapper userMapper;

    public void execute(UserAddCmd cmd) {
        // 参数校验
        if (userMapper.countByEmail(cmd.getEmail()) > 0) {
            throw new BizException(ErrorCode.EMAIL_EXISTS);
        }
        
        // 创建数据对象
        UserDO userDO = new UserDO();
        userDO.setName(cmd.getName());
        userDO.setEmail(cmd.getEmail());
        
        // 持久化
        userMapper.insert(userDO);
    }
}

// service/command/query/UserGetQryExe.java
@Component
@RequiredArgsConstructor
public class UserGetQryExe {
    private final UserMapper userMapper;
    private final UserConverter userConverter;

    public UserDTO execute(UserGetQry qry) {
        UserDO userDO = userMapper.selectById(qry.getUserId());
        if (userDO == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        return userConverter.toDTO(userDO);
    }
}

// service/command/query/UserSearchQryExe.java
@Component
@RequiredArgsConstructor
public class UserSearchQryExe {
    private final UserMapper userMapper;
    private final UserConverter userConverter;

    public PageInfo<UserDTO> execute(UserPageQry qry) {
        PageHelper.startPage(qry.getPageIndex(), qry.getPageSize());
        List<UserDO> list = userMapper.selectByCondition(qry);
        PageInfo<UserDO> pageInfo = new PageInfo<>(list);

        // DO → DTO 转换
        PageInfo<UserDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(userConverter.toDTOList(pageInfo.getList()));
        return result;
    }
}
```

## 十、异常处理

### 错误码枚举

```java
// common/enums/ErrorCode.java
@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),
    EMAIL_EXISTS("EMAIL_EXISTS", "邮箱已存在"),
    PARAM_ERROR("PARAM_ERROR", "参数错误");

    private final String code;
    private final String message;
}
```

### 自定义异常类

```java
// common/exception/BizException.java
@Getter
public class BizException extends RuntimeException {
    private final String code;
    private final String message;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}

// common/exception/SysException.java
@Getter
public class SysException extends RuntimeException {
    private final String code;
    private final String message;

    public SysException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
```

### 使用示例

```java
// 业务异常
throw new BizException(ErrorCode.USER_NOT_FOUND);

// 或直接传参
throw new BizException("USER_NOT_FOUND", "用户不存在");

// 系统异常
throw new SysException("DB_ERROR", "数据库连接失败");
```

### 全局异常处理

```java
// web/handler/GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Response handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Response.buildFailure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(SysException.class)
    public Response handleSysException(SysException e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Response.buildFailure(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e) {
        log.error("未知异常: {}", e.getMessage(), e);
        return Response.buildFailure("SYSTEM_ERROR", "系统繁忙，请稍后重试");
    }
}
```

## 十一、状态机组件

### 定义状态和事件

```java
public enum OrderState {
    INIT, PAID, SHIPPED, RECEIVED, CANCELLED
}

public enum OrderEvent {
    PAY, SHIP, RECEIVE, CANCEL
}
```

### 构建状态机

```java
@Configuration
public class OrderStateMachineConfig {

    @Bean
    public StateMachine<OrderState, OrderEvent, OrderDTO> orderStateMachine() {
        StateMachineBuilder<OrderState, OrderEvent, OrderDTO> builder = 
            StateMachineBuilderFactory.create();

        // INIT -> PAID (on PAY)
        builder.externalTransition()
            .from(OrderState.INIT)
            .to(OrderState.PAID)
            .on(OrderEvent.PAY)
            .when(this::checkPayCondition)
            .perform(this::doPayAction);

        return builder.build("orderStateMachine");
    }
}
```

### 使用状态机

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final StateMachine<OrderState, OrderEvent, OrderDTO> orderStateMachine;

    public void payOrder(OrderDTO order) {
        OrderState newState = orderStateMachine.fireEvent(
            OrderState.valueOf(order.getState()),
            OrderEvent.PAY,
            order
        );
        order.setState(newState.name());
    }
}
```

## 十二、快速创建项目

### Web 应用

```bash
mvn archetype:generate \
    -DgroupId=com.company.project \
    -DartifactId=my-web-app \
    -Dversion=1.0.0-SNAPSHOT \
    -Dpackage=com.company.project \
    -DarchetypeArtifactId=cola-framework-archetype-web \
    -DarchetypeGroupId=com.alibaba.cola \
    -DarchetypeVersion=5.0.0
```

### 纯后端服务

```bash
mvn archetype:generate \
    -DgroupId=com.company.project \
    -DartifactId=my-service \
    -Dversion=1.0.0-SNAPSHOT \
    -Dpackage=com.company.project \
    -DarchetypeArtifactId=cola-framework-archetype-service \
    -DarchetypeGroupId=com.alibaba.cola \
    -DarchetypeVersion=5.0.0
```

## 十三、Maven 模块依赖

```xml
<dependencies>
    <!-- start 模块依赖 -->
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>project-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>project-service</artifactId>
    </dependency>
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>project-api</artifactId>
    </dependency>
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>project-model</artifactId>
    </dependency>

    <!-- service 模块依赖 -->
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>project-api</artifactId>
    </dependency>
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>project-model</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cola</groupId>
        <artifactId>cola-component-catchlog-starter</artifactId>
    </dependency>

    <!-- web 模块依赖 -->
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>project-api</artifactId>
    </dependency>
    <dependency>
        <groupId>com.company</groupId>
        <artifactId>project-service</artifactId>
    </dependency>
</dependencies>
```

## 十四、组件选择建议

| 场景 | 推荐组件 |
|------|----------|
| 基础项目 | dto + catchlog |
| 复杂状态流转 | + statemachine |

## 十五、重构检查清单

1. 创建 web/service/api/model/start 模块
2. 配置 Maven 依赖关系
3. 定义 Command、Query、DTO 类
4. 创建 CmdExe/QryExe 和 Service
5. 迁移 Controller 到 web/

## 十六、分层职责总结

| 层 | 返回类型 | 示例 |
|---|---------|------|
| Controller | `Response` / `SingleResponse<T>` / `MultiResponse<T>` / `PageResponse<T>` | `return SingleResponse.of(userDTO);` |
| Service | 业务数据（void、DTO、List、PageInfo 等） | `return userDTO;` |
| Executor | 业务数据（void、DTO、List、PageInfo 等） | `return userConverter.toDTO(userDO);` |

**关键原则**：
- **Service 层返回业务数据，不返回 Response**
- **Controller 层负责将业务数据转换为 Response**
- **DTO 用于 API 响应，DO 用于数据库映射**
- **自定义异常类，不依赖 COLA 异常组件**

**PageResponseUtils 工具类**：`com.zenith.admin.common.utils.PageResponseUtils`

```java
// Controller 层使用示例
public PageResponse<UserDTO> searchUsers(@RequestBody UserPageQry qry) {
    PageInfo<UserDTO> pageInfo = userService.searchUsers(qry);
    return PageResponseUtils.of(pageInfo);
}
```
