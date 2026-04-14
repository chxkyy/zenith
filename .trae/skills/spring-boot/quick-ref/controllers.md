# Spring Boot Controllers

> **Knowledge Base:** Read `knowledge/spring-boot/basics.md` for complete documentation.

> **【强制】仅使用 GET 和 POST 方法，禁止使用 PUT、PATCH、DELETE**
> **【强制】必须返回 COLA Response 类（Response/SingleResponse/MultiResponse/PageResponse）**
> **【强制】禁止使用 @PathVariable，所有参数通过 @RequestParam 或 @RequestBody 传递**

## REST Controller

```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserService userService;

    @GetMapping
    public MultiResponse<UserDTO> list() {
        return MultiResponse.of(userService.list());
    }

    @PostMapping("/page")
    public PageResponse<UserDTO> findPage(@RequestBody @Valid UserPageQry qry) {
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

    @PostMapping("/update")
    public SingleResponse<UserDTO> update(@Valid @RequestBody UpdateUserCmd cmd) {
        return SingleResponse.of(userService.update(cmd));
    }

    @PostMapping("/delete")
    public Response delete(@RequestParam Long id) {
        userService.delete(id);
        return Response.buildSuccess();
    }
}
```

## Request Mapping

```java
// HTTP Methods - 【强制】仅使用 GET 和 POST
@GetMapping
@PostMapping

// Query Parameters
@GetMapping
public SingleResponse<UserDTO> findById(@RequestParam Long id) {}

// Query Parameters for list
@GetMapping
public MultiResponse<UserDTO> search(
    @RequestParam(required = false) String keyword,
    @RequestParam(defaultValue = "10") int limit) {}

// Request Body
@PostMapping
public SingleResponse<UserDTO> create(@RequestBody @Valid CreateUserCmd cmd) {}
```

## Response Handling

**【强制】返回 COLA Response**

```java
// SingleResponse - 单个对象
@GetMapping
public SingleResponse<UserDTO> findById(@RequestParam Long id) {
    return SingleResponse.of(userService.findById(id));
}

// MultiResponse - 列表
@GetMapping
public MultiResponse<UserDTO> list() {
    return MultiResponse.of(userService.list());
}

// PageResponse - 分页
@PostMapping("/page")
public PageResponse<UserDTO> findPage(@RequestBody @Valid UserPageQry qry) {
    return PageResponseUtils.of(userService.findPage(qry));
}

// Response - 无数据返回
@PostMapping("/delete")
public Response delete(@RequestParam Long id) {
    userService.delete(id);
    return Response.buildSuccess();
}
```

## Validation

```java
@PostMapping
public SingleResponse<UserDTO> create(@Valid @RequestBody CreateUserCmd cmd) {
    // Validation errors handled by GlobalExceptionHandler
}

// DTO
@Data
public class CreateUserCmd {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Email(message = "邮箱格式不正确")
    private String email;
}
```

## OpenAPI Documentation

```java
@Operation(summary = "Get user by ID")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "User found"),
    @ApiResponse(responseCode = "400", description = "User not found")
})
@GetMapping
public SingleResponse<UserDTO> findById(
        @Parameter(description = "User ID") @RequestParam Long id) {
    return SingleResponse.of(userService.findById(id));
}
```

## File Upload

```java
@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public SingleResponse<String> uploadFile(
        @RequestParam("file") MultipartFile file) {
    String filename = storageService.store(file);
    return SingleResponse.of(filename);
}
```

## PageResponseUtils

**位置**：`com.zenith.admin.common.utils.PageResponseUtils`

```java
public final class PageResponseUtils {
    private PageResponseUtils() {}

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

**Official docs:** https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html
