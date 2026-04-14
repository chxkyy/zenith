---
name: java
description: |
  Java language (17+). Covers modern features, patterns, and best practices.
  Use when writing Java applications, Spring Boot backends, or enterprise systems.

  USE WHEN: user mentions "java", "records", "sealed classes", "streams", asks about
  "pattern matching", "switch expressions", "Optional", "collections", "generics"

  DO NOT USE FOR: Spring Boot specifics - use `backend-spring-boot` skill instead
  DO NOT USE FOR: Lombok annotations - use `lombok` skill instead
  DO NOT USE FOR: MapStruct - use `mapstruct` skill instead
allowed-tools: Read, Grep, Glob, Write, Edit
---

# Java Core Knowledge

## Modern Java Features (17+)

```java
// Records (immutable data classes)
public record User(Long id, String name, String email) {}

// Sealed classes
public sealed interface Shape permits Circle, Rectangle {}
public final class Circle implements Shape { }
public final class Rectangle implements Shape { }

// Pattern matching for instanceof
if (obj instanceof String s) {
    System.out.println(s.toUpperCase());
}

// Switch expressions
String result = switch (status) {
    case ACTIVE -> "Active";
    case INACTIVE -> "Inactive";
    default -> "Unknown";
};

// Text blocks
String json = """
    {
        "name": "John",
        "age": 30
    }
    """;
```

## Collections & Streams

```java
// Stream operations
List<String> names = users.stream()
    .filter(u -> u.isActive())
    .map(User::getName)
    .sorted()
    .collect(Collectors.toList());

// Grouping
Map<Status, List<User>> byStatus = users.stream()
    .collect(Collectors.groupingBy(User::getStatus));

// Optional handling
Optional<User> user = findById(id);
String name = user.map(User::getName).orElse("Unknown");
```

## Common Patterns

```java
// Builder pattern
User user = User.builder()
    .name("John")
    .email("john@example.com")
    .build();

// Factory method
public static User of(String name, String email) {
    return new User(null, name, email);
}

// Dependency Injection (constructor)
@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

## Exception Handling

```java
// Custom exception
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}

// Try-with-resources
try (var reader = new BufferedReader(new FileReader(file))) {
    return reader.lines().collect(Collectors.toList());
}
```

---

## Static Analysis & Linting

### Official Rules References

| Tool | Rules Count | Documentation |
|------|-------------|---------------|
| **SonarJava** | 733 | https://rules.sonarsource.com/java/ |
| **Checkstyle** | 200+ | https://checkstyle.org/checks.html |
| **PMD** | 400+ | https://pmd.github.io/latest/pmd_rules_java.html |
| **SpotBugs** | 400+ | https://spotbugs.readthedocs.io/en/latest/bugDescriptions.html |

### Style Guides

| Guide | Link |
|-------|------|
| **Google Java Style** | https://google.github.io/styleguide/javaguide.html |
| **Oracle Code Conventions** | https://www.oracle.com/java/technologies/javase/codeconventions-contents.html |

### Key Rules Categories

```xml
<!-- pom.xml - Maven Checkstyle Plugin -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.1</version>
    <configuration>
        <configLocation>google_checks.xml</configLocation>
    </configuration>
</plugin>
```

### Critical Rules to Enable

| Category | Rule | Tool |
|----------|------|------|
| Bug | NullPointerException risks | SonarJava S2259 |
| Bug | Resource leaks | SonarJava S2095 |
| Security | SQL Injection | SonarJava S3649 |
| Security | Hardcoded credentials | SonarJava S2068 |
| Maintainability | Cognitive complexity | SonarJava S3776 |
| Maintainability | Too many parameters | Checkstyle |

---

## Production Readiness

### Error Handling

**【强制】必须使用 COLA 异常体系，禁止自定义异常类**

```java
// 使用 COLA 提供的 BizException 和 SysException
// BizException - 业务异常（如：用户不存在、参数错误）
// SysException - 系统异常（如：数据库错误、RPC 调用失败）

// 抛出业务异常示例
throw new BizException("USER_NOT_FOUND", "用户不存在");

// 抛出系统异常示例
throw new SysException("DB_ERROR", "数据库连接失败");

// 全局异常处理器（使用 COLA Response）
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public Response handleBizException(BizException ex) {
        log.warn("业务异常: errCode={}, errMessage={}", ex.getErrCode(), ex.getMessage());
        return Response.buildFailure(ex.getErrCode(), ex.getMessage());
    }

    @ExceptionHandler(SysException.class)
    public Response handleSysException(SysException ex) {
        log.error("系统异常: errCode={}, errMessage={}", ex.getErrCode(), ex.getMessage(), ex);
        return Response.buildFailure(ex.getErrCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Response handleUnexpected(Exception ex) {
        log.error("未知异常", ex);
        return Response.buildFailure("UNKNOWN_ERROR", "系统繁忙，请稍后重试");
    }
}
```

**COLA 异常说明**：

| 异常类型 | 使用场景 | 错误码示例 |
|---------|---------|-----------|
| `BizException` | 业务异常（参数错误、业务规则违反等） | `USER_NOT_FOUND`、`PARAM_ERROR` |
| `SysException` | 系统异常（数据库错误、RPC 失败等） | `DB_ERROR`、`RPC_TIMEOUT` |
| `Exception` | 未知异常，统一返回友好提示 | `UNKNOWN_ERROR` |

### Null Safety

```java
// Use Optional properly
public Optional<User> findById(Long id) {
    return repository.findById(id);
}

// Never return null from Optional
public User getById(Long id) {
    return findById(id)
        .orElseThrow(() -> new BizException("USER_NOT_FOUND", "用户不存在"));
}

// Use @Nullable and @NonNull annotations
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public User updateUser(@NonNull Long id, @Nullable String email) {
    User user = getById(id);
    if (email != null) {
        user.setEmail(email);
    }
    return repository.save(user);
}

// Validation
import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank @Size(min = 2, max = 100) String name,
    @NotBlank @Email String email,
    @NotNull @Min(0) Integer age
) {}
```

### Logging

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User createUser(CreateUserRequest request) {
        MDC.put("operation", "createUser");
        MDC.put("email", request.email());

        log.info("Creating user");

        try {
            User user = userRepository.save(User.from(request));
            log.info("User created successfully: {}", user.getId());
            return user;
        } catch (Exception e) {
            log.error("Failed to create user", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
```

### Testing

```java
// Unit test with Mockito
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void shouldCreateUser() {
        // Given
        var request = new CreateUserRequest("John", "john@example.com", 30);
        var expected = new User(1L, "John", "john@example.com", 30);
        when(repository.save(any())).thenReturn(expected);

        // When
        var result = service.createUser(request);

        // Then
        assertThat(result).isEqualTo(expected);
        verify(repository).save(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> service.getById(1L))
            .isInstanceOf(BizException.class)
            .hasMessageContaining("用户不存在");
    }
}

// Integration test
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateUser() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "John", "email": "john@example.com", "age": 30}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }
}
```

### Performance

```java
// Connection pooling (HikariCP)
// application.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000

// Caching
@Cacheable(value = "users", key = "#id")
public User findById(Long id) {
    return repository.findById(id).orElseThrow();
}

@CacheEvict(value = "users", key = "#user.id")
public User save(User user) {
    return repository.save(user);
}
```

### Monitoring Metrics

| Metric | Target |
|--------|--------|
| Test coverage | > 80% |
| Cyclomatic complexity | < 10 |
| SonarQube bugs | 0 |
| Security vulnerabilities | 0 |

### Checklist

- [ ] Custom exception hierarchy
- [ ] Global exception handler
- [ ] Bean validation on DTOs
- [ ] Optional for nullable returns
- [ ] @NonNull/@Nullable annotations
- [ ] Structured logging with MDC
- [ ] Unit tests with Mockito
- [ ] Integration tests with Testcontainers
- [ ] Connection pool configured
- [ ] Caching for hot data

---

## When NOT to Use This Skill

| Scenario | Use Instead |
|----------|-------------|
| Spring Boot framework | `backend-spring-boot` skill |
| Lombok annotations | `lombok` skill |
| MapStruct mapping | `mapstruct` skill |
| JPA/Hibernate | ORM-specific skills |
| Testing with JUnit | `testing-junit` skill |

---

## Anti-Patterns

| Anti-Pattern | Why It's Bad | Correct Approach |
|--------------|--------------|------------------|
| Returning null | NullPointerException | Use Optional |
| Catching generic Exception | Hides specific errors | Catch specific exceptions |
| Not closing resources | Resource leaks | Use try-with-resources |
| Mutable static fields | Thread-safety issues | Use immutable or synchronized |
| String concatenation in loops | O(n²) performance | Use StringBuilder |
| Empty catch blocks | Silent failures | Log or rethrow |
| Using `==` for strings | Compares references | Use .equals() |
| Not overriding equals/hashCode | Broken collections | Override both or neither |

---

## Quick Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| NullPointerException | Accessing null reference | Check for null, use Optional |
| ClassCastException | Wrong type cast | Use instanceof before casting |
| ConcurrentModificationException | Modifying while iterating | Use Iterator.remove() |
| OutOfMemoryError | Heap exhausted | Increase heap, fix memory leaks |
| StackOverflowError | Infinite recursion | Add base case, use iteration |
| "Cannot find symbol" | Compilation error | Check imports, spelling |
| Deadlock | Circular lock dependency | Use consistent lock ordering |
| Resource leak | Not closing streams | Use try-with-resources |

---

## Reference Documentation
- [Spring Boot](../../backend-frameworks/spring-boot/SKILL.md)
- [Lombok](../lombok/SKILL.md)
- [Quality Principles](../../quality/common/SKILL.md)
