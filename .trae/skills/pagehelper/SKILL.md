---
name: pagehelper
description: PageHelper 分页插件使用规范。当使用 MyBatis 进行分页查询时使用
when_to_use: 实现列表分页、分页查询接口、或需要使用 PageHelper 进行数据分页时
paths: "**/*Service*.java,**/*Mapper*.java"
---

# PageHelper 分页插件使用规范

使用 PageHelper 进行分页查询时必须遵循以下规范。

## 一、依赖配置

### Maven 依赖

```xml
<!-- PageHelper -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>${pagehelper.version}</version>
</dependency>
```

### application.yml 配置

```yaml
pagehelper:
  helper-dialect: mysql
  reasonable: true
  support-methods-arguments: true
  params: count=countSql
  default-count: true
```

### 配置参数说明

| 参数 | 说明 | 默认值 | 推荐值 |
|------|------|--------|--------|
| `helper-dialect` | 数据库方言 | 自动检测 | `mysql` |
| `reasonable` | 分页合理化（pageNum<1 查第一页，pageNum>pages 查最后一页） | `false` | `true` |
| `support-methods-arguments` | 支持通过 Mapper 参数传递分页参数 | `false` | `true` |
| `params` | count 查询参数映射 | `count=countSql` | `count=countSql` |
| `default-count` | 默认执行 count 查询 | `true` | `true` |
| `page-size-zero` | pageSize=0 时查询全部 | `false` | `false` |
| `async-count` | 异步执行 count 查询 | `false` | `false` |

## 二、基本用法

### 1. 标准分页查询

```java
// ✅ 推荐：标准分页写法
public PageInfo<UserDTO> search(UserQry qry) {
    PageHelper.startPage(qry.getPageIndex(), qry.getPageSize());
    List<UserDO> list = userMapper.selectByCondition(qry);
    return new PageInfo<>(list);
}
```

### 2. 带排序的分页查询

```java
// ✅ 推荐：带排序
public PageInfo<UserDTO> search(UserQry qry) {
    PageHelper.startPage(qry.getPageIndex(), qry.getPageSize(), "create_time desc");
    List<UserDO> list = userMapper.selectByCondition(qry);
    return new PageInfo<>(list);
}
```

### 3. Lambda 方式（推荐）

```java
// ✅ 推荐：Lambda 方式，代码更简洁
public PageInfo<UserDTO> search(UserQry qry) {
    return PageHelper.startPage(qry.getPageIndex(), qry.getPageSize())
        .doSelectPageInfo(() -> userMapper.selectByCondition(qry));
}
```

### 4. offset 分页

```java
// 从第 0 条开始，查 10 条
PageHelper.offsetPage(0, 10);
List<UserDO> list = userMapper.selectAll();
```

## 三、重要规则

### 1. 紧跟查询原则（最重要）

`PageHelper.startPage()` 必须紧跟 MyBatis 查询方法，中间不能有其他查询操作。

```java
// ✅ 正确：startPage 紧跟查询
PageHelper.startPage(1, 10);
List<UserDO> list = userMapper.selectAll();

// ❌ 错误：中间有其他操作
PageHelper.startPage(1, 10);
log.info("开始查询");           // 允许：非查询操作
otherService.doQuery();         // 错误：中间有其他查询
List<UserDO> list = userMapper.selectAll();  // 这个查询不会被分页！
```

### 2. 安全调用

```java
// ❌ 错误：条件判断导致 startPage 可能不被消费
PageHelper.startPage(1, 10);
if (param != null) {
    list = userMapper.selectByParam(param);  // 可能不被执行
}

// ✅ 正确：先判断再分页
if (param != null) {
    PageHelper.startPage(1, 10);
    list = userMapper.selectByParam(param);
}
```

### 3. 使用 PageInfo 包装结果

```java
// ✅ 推荐：使用 PageInfo 包装
PageHelper.startPage(pageNum, pageSize);
List<UserDO> list = userMapper.selectAll();
PageInfo<UserDO> pageInfo = new PageInfo<>(list);

// 或使用静态方法
PageInfo<UserDO> pageInfo = PageInfo.of(list);
```

## 四、PageInfo 属性说明

```java
PageInfo<UserDO> pageInfo = new PageInfo<>(list);

// 分页信息
pageInfo.getTotal();        // 总记录数
pageInfo.getPages();        // 总页数
pageInfo.getPageNum();      // 当前页码
pageInfo.getPageSize();     // 每页数量
pageInfo.getSize();         // 当前页的实际数量

// 导航信息
pageInfo.getNavigatePages();     // 导航页码数
pageInfo.getNavigatepageNums();  // 导航页码列表

// 边界判断
pageInfo.isFirstPage();     // 是否第一页
pageInfo.isLastPage();      // 是否最后一页
pageInfo.isHasNextPage();   // 是否有下一页
pageInfo.isHasPreviousPage(); // 是否有上一页

// 数据列表
pageInfo.getList();         // 数据列表
```

## 五、查询 DTO 规范

### 分页查询 DTO 基类

```java
@Data
public abstract class PageQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = -1L;

    @ApiModelProperty("页码（从1开始）")
    @Min(value = 1, message = "页码最小为1")
    private Integer pageIndex = 1;

    @ApiModelProperty("每页数量")
    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
    private Integer pageSize = 10;
}
```

### 具体查询 DTO

```java
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "用户管理-查询条件")
public class UserQry extends PageQuery {
    @Serial
    private static final long serialVersionUID = -1L;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("状态")
    private String status;
}
```

## 六、Service 层规范

### 标准分页方法

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserConverter userConverter;

    /**
     * 分页查询用户
     */
    public PageInfo<UserDTO> search(UserQry qry) {
        PageHelper.startPage(qry.getPageIndex(), qry.getPageSize());
        List<UserDO> list = userMapper.selectByCondition(qry);
        PageInfo<UserDO> pageInfo = new PageInfo<>(list);
        
        // 转换为 DTO
        return userConverter.toDTOPage(pageInfo);
    }
}
```

### Converter 支持 PageInfo 转换

```java
@Mapper(componentModel = "spring")
public interface UserConverter {

    UserDTO toDTO(UserDO entity);

    List<UserDTO> toDTOList(List<UserDO> entities);

    /**
     * 转换 PageInfo
     */
    default PageInfo<UserDTO> toDTOPage(PageInfo<UserDO> pageInfo) {
        PageInfo<UserDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(toDTOList(pageInfo.getList()));
        return result;
    }
}
```

## 七、Controller 层规范

**【强制】使用 PageResponseUtils 工具类转换 PageInfo 为 PageResponse**

```java
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户")
    @PostMapping("/search")
    public PageResponse<UserDTO> search(@RequestBody @Validated UserQry qry) {
        PageInfo<UserDTO> pageInfo = userService.search(qry);
        return PageResponseUtils.of(pageInfo);
    }
}
```

### PageResponseUtils 工具类

**位置**：`com.zenith.admin.common.utils.PageResponseUtils`

```java
package com.zenith.admin.common.utils;

import com.alibaba.cola.dto.PageResponse;
import com.github.pagehelper.PageInfo;

import java.util.List;

public final class PageResponseUtils {

    private PageResponseUtils() {
    }

    public static <T> PageResponse<T> of(PageInfo<?> pageInfo) {
        return PageResponse.of(
            (List<T>) pageInfo.getList(),
            (int) pageInfo.getTotal(),
            pageInfo.getPageSize(),
            pageInfo.getPageNum()
        );
    }
}
```

## 八、支持的分页方式对比

| 方式 | 说明 | 推荐场景 |
|------|------|----------|
| `PageHelper.startPage(pageNum, pageSize)` | 基础分页 | 标准分页查询 |
| `PageHelper.startPage(pageNum, pageSize, orderBy)` | 带排序分页 | 需要排序的分页 |
| `PageHelper.offsetPage(offset, limit)` | 偏移量分页 | 瀑布流加载 |
| `PageHelper.startPage(...).doSelectPageInfo(lambda)` | Lambda 方式 | 简洁写法 |
| `new RowBounds(offset, limit)` | RowBounds 方式 | 不推荐 |

## 九、常见问题

### 1. 不支持的场景

| 场景 | 说明 | 解决方案 |
|------|------|----------|
| `for update` 语句 | 不支持带 for update 的分页 | 手动分页 |
| 嵌套结果映射 | 结果集折叠导致分页数量不正确 | 使用简单映射或手动分页 |
| 复杂子查询 | 可能出现 count 不准确 | 测试验证或手动 count |

### 2. 性能优化

```java
// ✅ 大数据量时可关闭 count 查询
PageHelper.startPage(pageNum, pageSize, false);

// ✅ 异步 count 查询（v2.0.0+）
// 配置: pagehelper.async-count=true
```

### 3. 动态表名问题

```java
// 使用 PageHelper.clearPage() 清理分页参数
try {
    PageHelper.startPage(1, 10);
    // 查询操作
} finally {
    PageHelper.clearPage();
}
```

## 十、最佳实践总结

| 规则 | 说明 |
|------|------|
| 紧跟查询 | `startPage` 后必须紧跟 MyBatis 查询方法 |
| 使用 PageInfo | 统一使用 `PageInfo` 包装分页结果 |
| 继承 PageQuery | 分页查询 DTO 继承 `PageQuery` 基类 |
| 合理配置 | 开启 `reasonable=true` 防止越界 |
| 参数校验 | 对 pageIndex、pageSize 进行范围校验 |
| 统一转换 | 使用 Converter 转换 PageInfo |

## 十一、支持的数据库

| 数据库 | 方言 |
|--------|------|
| MySQL / MariaDB / SQLite | `mysql` |
| PostgreSQL / openGauss / 人大金仓 | `postgresql` |
| Oracle / 达梦 | `oracle` |
| SQL Server | `sqlserver` |
| DB2 | `db2` |
| H2 | `h2` |
| ClickHouse | `clickhouse` |
