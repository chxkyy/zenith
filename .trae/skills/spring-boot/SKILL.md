---
name: spring-boot
description: |
  Spring Boot 3 Java framework with enterprise patterns. Covers REST controllers,
  services, repositories, JPA entities, MapStruct mappers, Lombok, JWT security,
  Flyway migrations, and global exception handling.

  USE WHEN: user mentions "Spring Boot", "REST API", "enterprise Java", asks about "controller patterns", "service layer", "repository", "DTO mapping", "JWT auth", "Flyway", "MapStruct"

  DO NOT USE FOR: Spring Data JPA (use `spring-data-jpa`), Spring Security (use `spring-security`), Spring WebFlux (use `spring-webflux`), Spring WebSocket (use `spring-websocket`)
allowed-tools: Read, Grep, Glob, Write, Edit
---
# Spring Boot 3 Enterprise Patterns

> **Full Reference**: See [production.md](production.md) for configuration profiles, health checks, logging, graceful shutdown, and caching.

## Controller with DTOs

**【强制】仅使用 GET 和 POST 方法，禁止使用 PUT、PATCH、DELETE 等其他HTTP方法**
**【强制】必须返回 COLA Response 类（Response/SingleResponse/MultiResponse/PageResponse）**

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping
    public MultiResponse<UserDTO> list() {
        return MultiResponse.of(userService.list());
    }

    @PostMapping("/page")  // 分页查询使用 POST
    public PageResponse<UserDTO> findPage(@RequestBody UserPageQry qry) {
        return PageResponseUtils.of(userService.findPage(qry));
    }

    @GetMapping
    public SingleResponse<UserDTO> findById(@RequestParam Long id) {
        return SingleResponse.of(userService.findById(id));
    }

    @PostMapping
    public SingleResponse<UserDTO> create(@Valid @RequestBody CreateUserCmd cmd) {
        return SingleResponse.of(userService.create(cmd));
    }

    @PostMapping("/update")  // 更新使用 POST
    public SingleResponse<UserDTO> update(@Valid @RequestBody UpdateUserCmd cmd) {
        return SingleResponse.of(userService.update(cmd));
    }

    @PostMapping("/delete")  // 删除使用 POST
    public Response delete(@RequestParam Long id) {
        userService.delete(id);
        return Response.buildSuccess();
    }
}
```

## Service with MapStruct

**【强制】使用 COLA 规范命名：DTO / Cmd / DO**

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> list() {
        return userRepository.findAll().stream()
            .map(userMapper::toDTO)
            .toList();
    }

    @Override
    public PageInfo<UserDTO> findPage(UserPageQry qry) {
        Page<User> page = userRepository.findAll(PageRequest.of(qry.getPageIndex(), qry.getPageSize()));
        PageInfo<User> pageInfo = new PageInfo<>(page.getContent());
        PageInfo<UserDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(page.getContent().stream().map(userMapper::toDTO).toList());
        return result;
    }

    @Override
    public UserDTO findById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toDTO)
            .orElseThrow(() -> new BizException("USER_NOT_FOUND", "用户不存在"));
    }

    @Override
    @Transactional
    public UserDTO create(CreateUserCmd cmd) {
        if (userRepository.existsByEmail(cmd.getEmail())) {
            throw new BizException("USER_EMAIL_EXISTS", "邮箱已被注册");
        }
        UserDO userDO = userMapper.toDO(cmd);
        userDO.setPassword(passwordEncoder.encode(cmd.getPassword()));
        return userMapper.toDTO(userRepository.save(userDO));
    }
}
```

## MapStruct Mapper

**【强制】转换方法命名：toDTO / toDO / toDTOList**

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDTO toDTO(UserDO entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserDO toDO(CreateUserCmd cmd);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(UpdateUserCmd cmd, @MappingTarget UserDO entity);
}
```

## Entity with Lombok & Auditing

```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

## DTOs with Validation

**【强制】使用 COLA 命名规范：Cmd / Qry / DTO**

```java
@Data
public class CreateUserCmd {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码至少8位")
    private String password;
}

@Data
public class UpdateUserCmd {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 100)
    private String name;
}

@Data
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
}
```

## Global Exception Handler

**【强制】返回 COLA Response，禁止使用 ResponseEntity**

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        log.warn("参数校验失败: {}", errors);
        return Response.buildFailure("PARAM_ERROR", "参数校验失败");
    }

    @ExceptionHandler(Exception.class)
    public Response handleUnexpected(Exception ex) {
        log.error("未知异常", ex);
        return Response.buildFailure("UNKNOWN_ERROR", "系统繁忙，请稍后重试");
    }
}
```

## JWT Security Configuration

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## Flyway Migration

```sql
-- V1__create_users_table.sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

## Key Annotations

| Annotation | Purpose |
|------------|---------|
| `@RestController` | REST controller |
| `@RequiredArgsConstructor` | Lombok constructor injection |
| `@Transactional` | Transaction management |
| `@Valid` | Bean validation |
| `@Mapper` | MapStruct mapper |
| `@EntityListeners` | JPA auditing |

---

## Anti-Patterns

| Anti-Pattern | Why It's Bad | Correct Approach |
|--------------|--------------|------------------|
| Manual constructor injection | Verbose, error-prone | Use `@RequiredArgsConstructor` |
| Manual DTO mapping | Boilerplate code | Use MapStruct |
| Try-catch in every controller | Code duplication | Use `@ControllerAdvice` |
| Forget `@Transactional` | Data inconsistency | Always use for write operations |
| Manual schema changes | Migration chaos | Use Flyway or Liquibase |

## Quick Troubleshooting

| Problem | Likely Cause | Solution |
|---------|--------------|----------|
| LazyInitializationException | Open-in-view disabled | Fetch data in transaction |
| 401 Unauthorized | Security misconfigured | Check SecurityFilterChain |
| Validation not working | Missing `@Valid` | Add `@Valid` on `@RequestBody` |
| Mapper not found | MapStruct not processed | Run `mvn compile` |
| Flyway migration fails | Checksum mismatch | Fix migration or use repair |

> **Deep Knowledge**: Use `mcp__documentation__fetch_docs` with technology: `spring-boot` for comprehensive documentation.

> **Note:** For JPA and Security, use dedicated skills `spring-data-jpa` and `spring-security`.
