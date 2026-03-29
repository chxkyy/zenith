# TRAE IDE 阿里巴巴COLA框架返回结果规范（含异常处理器）

# 一、COLA 响应体标准结构（强制）

COLA 提供 4 种标准响应基类（`com.alibaba.cola.dto`），所有接口必须严格使用，禁止自定义响应体，确保接口响应统一可维护，同时便于Swagger文档生成更明确的响应说明，提升团队协作效率。

## 1. Response（基础响应，无数据）

```json
{
  "success": true,      // 布尔，必填：成功true/失败false
  "errCode": null,      // 字符串，失败时必填：错误码
  "errMessage": null    // 字符串，失败时必填：错误信息
}
```

适用：增删改、无返回数据接口，例如用户删除、状态更新等接口。

## 2. SingleResponse<T>（单对象响应）

```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": null          // 对象，成功时必填：业务数据（DTO类型）
}
```

适用：查询单个对象、详情接口，例如用户详情、订单详情查询。

## 3. MultiResponse<T>（列表响应）

```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": [],           // 数组，成功时必填：列表数据（DTO集合）
  "empty": true,        // 布尔，自动计算：列表是否为空
  "notEmpty": false     // 布尔，自动计算：列表是否非空
}
```

适用：查询列表、集合接口；强制要求：空数据返回 `[]`，禁止返回 `null`，避免前端解析异常。

## 4. PageResponse<T>（分页响应）

```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": [],
  "empty": true,
  "notEmpty": false,
  "pageIndex": 1,       // 整数，必填：当前页码（从1开始）
  "pageSize": 10,       // 整数，必填：每页条数
  "totalCount": 0,      // 整数，必填：总记录数
  "totalPages": 0       // 整数，自动计算：总页数
}
```

适用：分页查询接口，需确保分页参数完整，便于前端实现分页渲染。

# 二、TRAE 项目规则（.trae/rules/project_rules.md，可直接复制导入）

```markdown
# COLA 框架返回结果规范（TRAE 强制规则）

## 1. 响应类强制使用规则
- 【强制】所有 Controller/API 接口必须返回 COLA 标准响应类：
  - 无数据 → `Response`
  - 单个对象 → `SingleResponse<T>`（T为DTO类型）
  - 列表 → `MultiResponse<T>`（T为DTO类型）
  - 分页 → `PageResponse<T>`（T为DTO类型）
- 【强制】禁止自定义响应体、禁止返回原生 Object/Map/基本类型，禁止混用自定义Result与COLA Response。
- 【强制】RPC/HTTP 统一使用 `success` 字段判断业务成功与否，HTTP 状态码仅用于网络层状态判断（如404、500），不用于业务逻辑判断。

## 2. 成功响应规范
- 【强制】`success=true`，`errCode=null`，`errMessage=null`，三者必须严格对应。
- 【强制】`data` 字段规范：
  - 单对象：返回实体/DTO，禁止 `null`（空对象返回 `{}`）
  - 列表：返回 `[]`（空列表），禁止 `null`
  - 分页：必须填充 `pageIndex/pageSize/totalCount`，`totalPages` 由框架自动计算，无需手动赋值

## 3. 失败响应规范
- 【强制】`success=false`，必须同时返回 `errCode` + `errMessage`，二者缺一不可。
- 【强制】`errCode` 采用枚举/常量管理，格式：`[模块]_[错误类型]`（如 `USER_NOT_EXIST`、`ORDER_STOCK_INSUFFICIENT`），禁止随意定义字符串错误码。
- 【强制】`errMessage`：表述简短、无敏感信息（如堆栈、SQL、密码）、可直接前端展示，无需前端二次处理。
- 【强制】异常统一捕获，通过 `Response.buildFailure()` 或异常处理器返回标准响应，禁止直接抛出原始异常到前端。

## 4. 构造方法规范（强制）
- 成功响应：必须使用 COLA 提供的静态方法 `buildSuccess()` 或 `of()` 构造
  ```java
  // 无数据成功
  return Response.buildSuccess();
  // 单对象成功
  return SingleResponse.of(userDTO);
  // 列表成功
  return MultiResponse.of(userList);
  // 分页成功
  return PageResponse.of(pageList, totalCount, pageSize, pageIndex);
  ```
- 失败响应：必须使用 `buildFailure(errCode, errMessage)` 构造
  ```java
  return Response.buildFailure("USER_NOT_EXIST", "用户不存在");
  return SingleResponse.buildFailure("USER_NOT_EXIST", "用户不存在");
  ```

## 5. 命名与注释规范
- 【强制】响应泛型必须使用 `DTO` 后缀（`UserDTO`/`OrderDTO`），禁止返回 DO/Entity 实体类，避免暴露数据库字段细节。
- 【强制】所有接口必须添加 Swagger 注解（`@Api`、`@ApiOperation`、`@ApiParam`），标注返回类型与字段说明，便于接口文档生成。

## 6. 禁止行为（红线）
- ❌ 禁止返回 `null` 作为响应体
- ❌ 禁止混用自定义 Result + COLA Response
- ❌ 禁止 `errMessage` 包含堆栈、SQL、密码等敏感信息
- ❌ 禁止列表接口返回 `null`，必须返回 `[]`
- ❌ 禁止手动赋值 `totalPages`（分页响应），由框架自动计算
- ❌ 禁止直接抛出 BizException、SysException 等异常，必须通过异常处理器统一处理

## 7. 异常处理补充规则
- 【强制】引入 COLA 异常组件依赖，统一管理业务异常与系统异常
- 【强制】业务异常使用 `BizException`，系统异常使用 `SysException`，二者区分处理
- 【强制】Service 层可通过 COLA Assert 工具类抛出业务异常，简化异常编写逻辑
- 【推荐】使用 `@CatchAndLog` 注解，实现异常自动捕获与日志记录，减少重复代码

## 8. TRAE IDE 配置步骤（强制落地）
1. 打开项目 → 右上角  设置（⚙️） → 规则
2. 项目规则 → + 创建 project_rules.md
3. 粘贴本规范 → 保存
4. 执行 TRAE 指令：`按 COLA 返回规范扫描所有接口，自动整改不符合项`
5. 配置日志：开启 `com.alibaba.cola.catchlog` 包的 DEBUG 级别日志，便于异常排查
```

# 三、全局异常处理器（适配COLA响应，可直接复制到项目）

结合 COLA 框架异常体系（BizException、SysException），实现全局异常统一捕获，自动返回 COLA 标准响应，同时集成日志记录，适配 `@CatchAndLog` 注解的AOP异常处理机制，减少重复编码。

## 1. 先引入 COLA 异常组件依赖（pom.xml）

```xml
<!-- COLA 异常处理组件 -->
<dependency>
    <groupId>com.alibaba.cola</groupId>
    <artifactId>cola-component-exception</artifactId>
    <version>最新稳定版</version> <!-- 建议使用与COLA框架一致的版本 -->
</dependency>
<!-- COLA 自动异常捕获与日志组件（可选，推荐） -->
<dependency>
    <groupId>com.alibaba.cola</groupId>
    <artifactId>cola-component-catchlog-starter</artifactId>
    <version>最新稳定版</version>
</dependency>
```

## 2. 全局异常处理器代码（GlobalExceptionHandler.java）

```java
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.BizException;
import com.alibaba.cola.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * COLA 框架全局异常处理器（TRAE IDE 适配版）
 * 统一捕获所有异常，返回 COLA 标准响应，区分业务异常、系统异常、未知异常
 */
@Slf4j
@RestControllerAdvice // 全局拦截 Controller 层异常
public class GlobalExceptionHandler {

    /**
     * 处理 COLA 业务异常（BizException）
     * 业务异常：有明确业务语义，无需记录Error日志，Debug模式打印堆栈，不重试
     * 如：用户不存在、订单金额错误、参数校验失败等
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 网络层返回400，业务层返回success=false
    public Response handleBizException(BizException e) {
        // 业务异常仅记录WARN级别日志，避免日志冗余
        log.warn("业务异常：errCode={}, errMessage={}", e.getErrCode(), e.getMessage());
        // Debug模式打印完整堆栈，便于开发调试
        if (log.isDebugEnabled()) {
            log.debug("业务异常详细堆栈：", e);
        }
        // 返回 COLA 标准失败响应，自动匹配接口返回类型
        return buildFailureResponse(e.getErrCode(), e.getMessage());
    }

    /**
     * 处理 COLA 系统异常（SysException）
     * 系统异常：已知系统问题，需要记录Error日志，可重试
     * 如：数据库连接失败、RPC调用失败、缓存异常等
     */
    @ExceptionHandler(SysException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 网络层返回500
    public Response handleSysException(SysException e) {
        // 系统异常记录Error级别日志，包含完整堆栈，便于排查问题
        log.error("系统异常：errCode={}, errMessage={}", e.getErrCode(), e.getMessage(), e);
        // 返回 COLA 标准失败响应
        return buildFailureResponse(e.getErrCode(), e.getMessage());
    }

    /**
     * 处理未知异常（非BizException、非SysException）
     * 未知异常：未预期的异常，记录完整堆栈，统一返回未知错误提示
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 网络层返回500
    public Response handleUnknownException(Exception e) {
        // 未知异常记录Error级别完整日志，便于定位问题
        log.error("未知异常：", e);
        // 统一返回未知错误码和提示，避免暴露敏感信息
        return buildFailureResponse("UNKNOWN_ERROR", "系统繁忙，请稍后再试");
    }

    /**
     * 核心方法：根据接口返回类型，构建对应的 COLA 失败响应
     * 适配 Response、SingleResponse、MultiResponse、PageResponse 四种类型
     */
    private Response buildFailureResponse(String errCode, String errMessage) {
        // 获取当前请求的接口返回类型（通过线程上下文获取）
        Class<?> returnType = getCurrentReturnType();
        if (returnType == null) {
            // 默认返回基础Response
            return Response.buildFailure(errCode, errMessage);
        }

        // 匹配不同响应类型，返回对应失败响应
        if (returnType == Response.class) {
            return Response.buildFailure(errCode, errMessage);
        } else if (returnType.isAssignableFrom(SingleResponse.class)) {
            return SingleResponse.buildFailure(errCode, errMessage);
        } else if (returnType.isAssignableFrom(MultiResponse.class)) {
            return MultiResponse.buildFailure(errCode, errMessage);
        } else if (returnType.isAssignableFrom(PageResponse.class)) {
            return PageResponse.buildFailure(errCode, errMessage);
        } else {
            // 异常情况，返回基础失败响应
            return Response.buildFailure(errCode, errMessage);
        }
    }

    /**
     * 辅助方法：获取当前请求接口的返回类型（简化实现，可根据项目Spring版本调整）
     */
    private Class<?> getCurrentReturnType() {
        try {
            // 通过Spring的RequestAttributes获取当前请求的处理方法，进而获取返回类型
            // 实际项目中可根据Spring版本优化，确保能正确获取接口返回类型
            return org.springframework.web.context.request.RequestContextHolder
                    .getRequestAttributes() != null ?
                    ((org.springframework.web.method.HandlerMethod) ((org.springframework.web.context.request.ServletRequestAttributes)
                            org.springframework.web.context.request.RequestContextHolder.getRequestAttributes())
                            .getRequest().getAttribute(org.springframework.web.servlet.HandlerMapping.BEST_MATCHING_HANDLER))
                            .getMethod().getReturnType() : null;
        } catch (Exception e) {
            log.warn("获取接口返回类型失败", e);
            return null;
        }
    }
}
```

# 四、Java 代码示例（标准写法，TRAE IDE 适配）

## 1. 基础成功/失败（结合异常处理器）

```java
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.Assert;
import com.alibaba.cola.exception.BizException;
import com.alibaba.cola.catchlog.CatchAndLog;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理接口（COLA规范）")
@CatchAndLog // 开启自动异常捕获与日志记录（可选，推荐）
public class UserController {

    // 删除用户（无数据，返回Response）
    @PostMapping("/delete/{id}")
    @ApiOperation("删除用户")
    public Response delete(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        // 方式1：正常返回成功
        userService.delete(id);
        return Response.buildSuccess();

        // 方式2：手动抛出业务异常（将被全局异常处理器捕获，返回标准失败响应）
        // if (id == null || id <= 0) {
        //     throw new BizException("USER_ID_ERROR", "用户ID非法");
        // }

        // 方式3：使用COLA Assert工具类抛出异常（简化写法）
        // Assert.isTrue(id > 0, "USER_ID_ERROR", "用户ID非法");
    }

    // 查询单个用户（返回SingleResponse）
    @GetMapping("/get/{id}")
    @ApiOperation("查询用户详情")
    public SingleResponse<UserDTO> get(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        UserDTO user = userService.getById(id);
        // 校验用户是否存在，抛出异常（全局处理器处理）
        Assert.notNull(user, "USER_NOT_EXIST", "用户不存在");
        return SingleResponse.of(user);
    }
}
```

## 2. 列表与分页（标准写法）

```java
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.PageResponse;
import com.alibaba.cola.catchlog.CatchAndLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理接口（COLA规范）")
@CatchAndLog
public class UserController {

    // 列表查询（返回MultiResponse）
    @GetMapping("/list")
    @ApiOperation("查询用户列表")
    public MultiResponse<UserDTO> list() {
        List<UserDTO> list = userService.listAll();
        // 空列表返回[]，框架自动设置empty=true
        return MultiResponse.of(list);
    }

    // 分页查询（返回PageResponse）
    @GetMapping("/page")
    @ApiOperation("分页查询用户")
    public PageResponse<UserDTO> page(
            @ApiParam(value = "当前页码（从1开始）", required = true) @RequestParam Integer pageIndex,
            @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
        // 业务层返回分页结果（包含数据列表和总记录数）
        PageResult<UserDTO> pageResult = userService.page(pageIndex, pageSize);
        // 构造分页响应，totalPages由框架自动计算
        return PageResponse.of(
                pageResult.getData(),
                pageResult.getTotal(),
                pageSize,
                pageIndex
        );
    }
}
```

# 五、错误码规范（建议，统一维护）

错误码采用“模块_错误类型”格式，统一维护在枚举类中，便于管理和排查，结合COLA异常体系分类定义：

```java
import com.alibaba.cola.exception.ErrorCodeI;

/**
 * COLA 框架错误码枚举（TRAE IDE 适配）
 * 格式：模块_错误类型，区分系统级、业务级错误
 */
public enum ErrorCodeEnum implements ErrorCodeI {

    // 系统级错误（SYS前缀）
    SYS_PARAM_ERROR("SYS_001", "参数校验失败"),
    SYS_LIMIT_ERROR("SYS_002", "接口限流，请稍后再试"),
    SYS_DEGRADE_ERROR("SYS_003", "接口降级，暂时无法访问"),
    SYS_DB_ERROR("SYS_004", "数据库操作异常"),
    SYS_RPC_ERROR("SYS_005", "RPC调用失败"),
    UNKNOWN_ERROR("SYS_999", "系统繁忙，请稍后再试"),

    // 业务级错误（模块前缀）
    // 用户模块（USER）
    USER_NOT_EXIST("USER_001", "用户不存在"),
    USER_ID_ERROR("USER_002", "用户ID非法"),
    USER_NAME_DUPLICATE("USER_003", "用户名已存在"),
    // 订单模块（ORDER）
    ORDER_NOT_EXIST("ORDER_001", "订单不存在"),
    ORDER_STOCK_INSUFFICIENT("ORDER_002", "库存不足"),
    ORDER_STATUS_ERROR("ORDER_003", "订单状态异常");

    private final String errCode;
    private final String errMessage;

    ErrorCodeEnum(String errCode, String errMessage) {
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    @Override
    public String getErrCode() {
        return errCode;
    }

    @Override
    public String getErrMessage() {
        return errMessage;
    }

    // 快速构建BizException
    public BizException buildBizException() {
        return new BizException(this.errCode, this.errMessage);
    }

    // 快速构建SysException
    public SysException buildSysException() {
        return new SysException(this.errCode, this.errMessage);
    }
}
```

# 六、TRAE IDE 配置与使用补充

1. 规范导入：将“二、TRAE 项目规则”中的内容，复制到项目 `.trae/rules/project_rules.md` 文件中，保存后生效。

2. 异常处理器使用：将“三、全局异常处理器”的代码复制到项目对应包下（如 `com.xxx.exception`），无需额外配置，Spring Boot 会自动扫描生效。

3. @CatchAndLog 使用：在 Controller 或 Service 类上添加该注解，即可实现异常自动捕获、请求响应日志记录，无需手动编写 try-catch 代码。

4. 规范校验：执行 TRAE 指令 `按 COLA 返回规范扫描所有接口，自动整改不符合项`，可快速排查并修复接口响应不规范问题。

5. 日志配置：在 logback.xml 或 application.yml 中开启 COLA 捕获日志的 DEBUG 级别，便于异常排查：
            `<!-- logback.xml 配置 -->
<logger name="com.alibaba.cola.catchlog" level="debug" />`
> （注：文档部分内容可能由 AI 生成）