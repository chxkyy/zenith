---
name: lombok
description: Lombok 注解使用规范。当创建或修改 Java 类、使用 Lombok 注解简化代码时使用
when_to_use: 创建实体类、DTO、Service 类，或需要使用 Lombok 注解简化样板代码时
paths: "**/*.java"
---

# Lombok 注解使用规范

编写 Java 代码时使用 Lombok 注解必须遵循以下规范。

## 一、核心注解

### 1. @Data - 综合注解

适用于 POJO/DTO 类，组合了以下注解：
- `@ToString`
- `@EqualsAndHashCode`
- `@Getter`（所有字段）
- `@Setter`（非 final 字段）
- `@RequiredArgsConstructor`

```java
// ✅ 推荐：DTO/POJO 类使用 @Data
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
}
```

### 2. @Getter / @Setter - 单独控制

```java
// ✅ 推荐：需要细粒度控制时使用
@Entity
@Getter
@Setter
public class User {
    @Id
    private Long id;
    
    private String username;
    
    @Setter(AccessLevel.NONE)  // 只读字段
    private LocalDateTime createTime;
}
```

### 3. @Value - 不可变类

适用于值对象、配置对象：

```java
// ✅ 推荐：不可变值对象
@Value
@Builder
public class UserVO {
    String name;
    String email;
}
```

## 二、构造器注解

### 1. @NoArgsConstructor - 无参构造器

```java
// ✅ 推荐：JPA 实体必需无参构造器
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    private Long id;
}
```

### 2. @AllArgsConstructor - 全参构造器

```java
// ✅ 推荐：配合 @Builder 使用
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
}
```

### 3. @RequiredArgsConstructor - 依赖注入

```java
// ✅ 推荐：Service 层构造器注入
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    // 自动生成包含 final 字段的构造器
}
```

## 三、@Builder - 建造者模式

```java
// ✅ 推荐：复杂对象构建
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer age;
}

// 使用示例
UserDTO user = UserDTO.builder()
    .id(1L)
    .username("test")
    .email("test@example.com")
    .build();
```

## 四、日志注解

### 支持的日志框架

| 注解 | 生成的日志对象 |
|------|----------------|
| `@Slf4j` | `private static final Logger log = LoggerFactory.getLogger(Xxx.class);` |
| `@Log4j2` | `private static final Logger log = LogManager.getLogger(Xxx.class);` |
| `@Log` | `private static final Logger log = Logger.getLogger(Xxx.class);` |

```java
// ✅ 推荐：使用 @Slf4j
@Slf4j
@Service
public class UserService {
    public void process() {
        log.info("Processing...");
        log.debug("Debug info: {}", someValue);
    }
}
```

## 五、其他常用注解

### 1. @NonNull - 空值检查

```java
// ✅ 推荐：防止 NPE
public void createUser(@NonNull String username) {
    // 自动生成 null 检查
}

// 字段级别
@Data
public class UserDTO {
    @NonNull
    private String username;
}
```

### 2. @ToString - 自定义输出

```java
// ✅ 推荐：排除敏感字段
@Data
@ToString(exclude = {"password", "salt"})
public class User {
    private String username;
    private String password;  // 不会出现在 toString 中
    private String salt;
}
```

### 3. @EqualsAndHashCode - 自定义比较

```java
// ✅ 推荐：仅比较业务关键字段
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    private Long id;
    
    private String username;
}
```

### 4. @With - 不可变修改

```java
// ✅ 推荐：不可变对象的"setter"
@Value
@With
public class UserVO {
    String name;
    String email;
}

// 使用示例：创建新对象，仅修改指定字段
UserVO updated = userVO.withName("newName");
```

## 六、场景最佳实践

### 1. JPA 实体类

```java
// ✅ 推荐
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"password"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    
    private String password;
    
    @CreationTimestamp
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
```

### 2. DTO 类

```java
// ✅ 推荐：简单 DTO 使用 @Data
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
}

// ✅ 推荐：复杂 DTO 使用 @Builder
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateCmd {
    @NotBlank
    private String username;
    
    @NotBlank
    @Size(min = 6, max = 20)
    private String password;
    
    @Email
    private String email;
}
```

### 3. Service 类

```java
// ✅ 推荐：构造器注入
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public void process() {
        log.info("Processing...");
    }
}
```

### 4. Controller 类

```java
// ✅ 推荐
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
}
```

## 七、配置文件

项目根目录创建 `lombok.config` 统一配置：

```properties
# 停止向上查找配置
config.stopBubbling = true

# 链式调用
lombok.accessors.chain = true

# 日志字段名
lombok.log.fieldName = log

# 生成 ConstructorProperties 注解
lombok.anyConstructor.addConstructorProperties = true

# 添加 @lombok.Generated 注解（JaCoCo 覆盖率）
lombok.addLombokGeneratedAnnotation = true
```

## 八、常见问题

### 1. @Data 与 JPA 实体

```java
// ❌ 不推荐：JPA 实体直接使用 @Data
@Entity
@Data
public class User { ... }

// ✅ 推荐：JPA 实体分开使用各注解
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"password"})
public class User { ... }
```

### 2. @Builder 与无参构造器

```java
// ❌ 错误：仅有 @Builder 时无无参构造器
@Data
@Builder
public class UserDTO { ... }

// ✅ 正确：同时添加无参构造器
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO { ... }
```

### 3. 循环引用

```java
// ❌ 可能导致 StackOverflowError
@Data
public class User {
    private List<Order> orders;
}

@Data
public class Order {
    private User user;  // 循环引用
}

// ✅ 解决方案：排除循环引用字段
@Data
@ToString(exclude = {"orders"})
public class User {
    private List<Order> orders;
}
```

## 九、注解使用决策表

| 场景 | 推荐注解组合 |
|------|-------------|
| POJO/DTO | `@Data` |
| JPA 实体 | `@Getter` + `@Setter` + `@NoArgsConstructor` |
| 不可变值对象 | `@Value` + `@Builder` |
| Service/Controller | `@RequiredArgsConstructor` + `@Slf4j` |
| 复杂构建对象 | `@Data` + `@Builder` + `@NoArgsConstructor` + `@AllArgsConstructor` |
| 敏感数据类 | `@Data` + `@ToString(exclude = {...})` |

## 十、与 MapStruct 集成

确保 pom.xml 中处理器顺序正确：

```xml
<annotationProcessorPaths>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
    </path>
    <path>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>${mapstruct.version}</version>
    </path>
    <path>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok-mapstruct-binding</artifactId>
        <version>0.2.0</version>
    </path>
</annotationProcessorPaths>
```
