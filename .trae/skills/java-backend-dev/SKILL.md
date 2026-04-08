---
name: "java-backend-dev"
description: "Java后端开发专家，基于Spring Boot + MyBatis-Plus + COLA架构，提供编码规范、设计模式、性能优化等技术指导。Invoke when user needs help with Java backend development, code review, or best practices."
---

# Java 后端开发专家

基于互联网最佳实践和官方文档的 Java 后端开发指南。

## 核心技术栈

- **Spring Boot 3.x** - 应用框架
- **MyBatis-Plus** - ORM 框架  
- **COLA 架构** - 整洁面向对象分层架构
- **MapStruct** - 对象映射
- **H2 Database** - 开发测试数据库

## 编码规范 (基于 Google Java Style Guide)

### 1. 代码格式
- 使用 4 空格缩进，不使用 Tab
- 每行代码长度不超过 120 个字符
- 方法长度建议 10-20 行，保持专注和单一职责
- 类大小适中，避免过长的类

### 2. 命名规范
- 类名使用 PascalCase（如：`UserService`）
- 方法名和变量名使用 camelCase（如：`getUserById`）
- 常量使用全大写，单词间用下划线分隔（如：`MAX_PAGE_SIZE`）

### 3. 代码组织
- 每个类应该有单一的职责
- 避免深层继承，优先使用组合
- 使用接口定义行为，实现类提供具体实现

## 设计模式与原则 (SOLID)

### 1. 单一职责原则 (SRP)
每个类应该只有一个引起变化的原因。

### 2. 开闭原则 (OCP)
对扩展开放，对修改关闭。

### 3. 里氏替换原则 (LSP)
子类应该能够替换父类而不影响程序正确性。

### 4. 接口隔离原则 (ISP)
客户端不应该依赖它不需要的接口。

### 5. 依赖倒置原则 (DIP)
高层模块不应该依赖低层模块，两者都应该依赖抽象。

### 常用设计模式
- **单例模式** - 确保一个类只有一个实例
- **工厂模式** - 创建对象而不暴露创建逻辑
- **策略模式** - 定义算法族，分别封装起来
- **依赖注入** - 降低组件间的耦合度

## Spring Boot 最佳实践

### 1. 项目结构
```
com.zenith.admin
├── adapter/          # 适配器层，处理 HTTP 请求
├── app/              # 应用层，处理业务逻辑
├── domain/           # 领域层，定义核心业务模型
├── dto/              # 数据传输对象
├── infrastructure/   # 基础设施层
└── AdminApplication.java
```

### 2. RESTful API 设计
- 使用标准 HTTP 方法（GET, POST, PUT, DELETE）
- 使用清晰的 HTTP 状态码
- 统一的请求和响应格式
- 使用 Swagger 或 OpenAPI 生成 API 文档

### 3. 依赖注入
```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    // 或者使用构造器注入（推荐）
    private final UserMapper userMapper;
    
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
```

## MyBatis-Plus 最佳实践

### 1. 实体类定义
```java
@Data
@TableName("sys_user")
public class UserDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_name")
    private String userName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

### 2. 自动填充配置
```java
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

### 3. 查询优化
- 单表查询使用 Wrapper
- 多表查询使用自定义 XML
- 复杂 SQL 抽离到 XML 或单独方法
- Service 层封装业务逻辑，不在 Controller 直接调用 Mapper

### 4. 分页查询
```java
public PageResponse<UserDTO> page(UserPageQuery query) {
    PageHelper.startPage(query.getPageIndex(), query.getPageSize());
    LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
    
    if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
        wrapper.like(UserDO::getUserName, query.getKeyword());
    }
    
    List<UserDO> userDOS = userMapper.selectList(wrapper);
    PageInfo<UserDO> pageInfo = new PageInfo<>(userDOS);
    
    return PageResponse.of(
        convertor.toDTOList(pageInfo.getList()),
        (int) pageInfo.getTotal(),
        query.getPageSize(),
        query.getPageIndex()
    );
}
```

## 性能优化

### 1. 代码层面
- 在循环中使用 StringBuilder/StringBuffer 进行字符串拼接
- 优先使用集合而非数组，提高灵活性
- 选择合适的算法和数据结构

### 2. JVM 层面
- 选择合适的垃圾回收器
- 合理的内存管理
- 使用性能分析工具（VisualVM、JProfiler）定位瓶颈

### 3. 数据库层面
- 使用合适的索引
- 避免 N+1 查询问题
- 批量操作减少数据库连接次数
- 合理使用缓存

## 日志记录

### 1. 日志框架
使用 SLF4J + Logback/Log4j2

### 2. 日志级别
- **DEBUG** - 详细的调试信息
- **INFO** - 重要的业务信息
- **WARN** - 警告信息
- **ERROR** - 错误信息

### 3. 日志规范
- 敏感操作记录足够信息以便追踪
- 避免记录过多造成性能负担
- 不要记录敏感信息（密码、token 等）

## 安全措施

### 1. 常见安全威胁防护
- **SQL 注入** - 使用参数化查询
- **XSS 攻击** - 输入验证和输出编码
- **CSRF 攻击** - 使用 CSRF Token
- **敏感数据** - 加密存储和传输

### 2. 认证与授权
- 使用 Spring Security
- JWT Token 机制
- 基于角色的访问控制（RBAC）

## 单元测试

### 1. 测试框架
- **JUnit 5** - 单元测试框架
- **Mockito** - 模拟对象
- **PowerMock** - 增强模拟功能

### 2. 测试驱动开发 (TDD)
- 先编写测试用例，再编写功能代码
- 确保代码的可测试性
- 促进更加细致的设计

## CI/CD 最佳实践

### 1. 持续集成
- 使用 Jenkins、GitLab CI、GitHub Actions
- 自动化构建和测试
- 代码质量检查（SonarQube）

### 2. 持续部署
- 自动化部署流程
- 环境一致性（Docker）
- 蓝绿部署或滚动部署

## 容器化与微服务

### 1. Docker
- 容器化应用程序
- 环境一致性
- 快速部署和扩展

### 2. Kubernetes
- 容器编排
- 自动扩展和负载均衡
- 服务发现和配置管理

## 参考资源

1. **Google Java Style Guide** - https://google.github.io/styleguide/javaguide.html
2. **Spring Boot 官方文档** - https://docs.spring.io/spring-boot/docs/current/reference/html/
3. **MyBatis-Plus 官方文档** - https://baomidou.com/
4. **阿里云 Java 后台开发最佳实践** - https://developer.aliyun.com/article/1596714
5. **JetBrains Java Best Practices** - https://blog.jetbrains.com/idea/2024/02/java-best-practices/
