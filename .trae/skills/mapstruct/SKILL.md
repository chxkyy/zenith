---
name: mapstruct
description: MapStruct 对象映射规范。当创建或修改 Mapper 接口、DTO 转换、Entity-DTO 映射时使用
when_to_use: 创建 Converter、Mapper 接口，或需要进行对象转换映射时
paths: "**/converter/*.java,**/mapper/*.java"
---

# MapStruct 对象映射规范

编写 Mapper 接口时必须遵循以下规范。

## 基本规范

### 1. Converter 接口定义

```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);
    
    // DO -> DTO
    UserDTO toDTO(UserDO entity);
    
    // DTO -> DO
    UserDO toDO(UserDTO dto);
    
    // 批量转换
    List<UserDTO> toDTOList(List<UserDO> entities);
}
```

### 2. 命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| Converter 接口 | `{实体}Converter` | `UserConverter` |
| DO -> DTO 方法 | `toDTO` | `UserDTO toDTO(UserDO entity)` |
| DTO -> DO 方法 | `toDO` | `UserDO toDO(UserDTO dto)` |
| 批量转换方法 | `toDTOList` / `toDOList` | `List<UserDTO> toDTOList(List<UserDO> entities)` |

### 3. 属性映射规则

| 场景 | 处理方式 |
|------|----------|
| 同名属性 | 自动映射 |
| 异名属性 | 使用 `@Mapping(target = "xxx", source = "yyy")` |
| 忽略属性 | `@Mapping(target = "password", ignore = true)` |
| 嵌套属性 | `@Mapping(target = "deptName", source = "dept.name")` |
| 日期格式化 | `@Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")` |

### 4. 常用注解

```java
@Mapper(
    componentModel = "spring",           // Spring 组件模型
    uses = {DateConverter.class},        // 引用其他 Converter
    unmappedTargetPolicy = ReportingPolicy.IGNORE  // 未映射属性策略
)
public interface UserConverter {
    
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deptName", source = "dept.name")
    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UserDTO toDTO(UserDO entity);
}
```

## 高级特性

### 1. 自定义映射方法

```java
@Mapper(componentModel = "spring")
public interface OrderConverter {
    
    @Mapping(target = "status", source = "statusCode")
    OrderDTO toDTO(OrderDO entity);
    
    // 自定义映射逻辑
    default String mapStatus(Integer statusCode) {
        if (statusCode == null) return null;
        return statusCode == 1 ? "ACTIVE" : "INACTIVE";
    }
}
```

### 2. 更新现有对象

```java
@Mapper(componentModel = "spring")
public interface UserConverter {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    void updateFromDTO(UserDTO dto, @MappingTarget UserDO entity);
}
```

### 3. 表达式与默认值

```java
@Mapping(target = "createTime", expression = "java(new java.util.Date())")
@Mapping(target = "name", defaultValue = "Unknown")
UserDTO toDTO(UserDO entity);
```

### 4. Before/After Mapping

```java
@Mapper(componentModel = "spring")
public abstract class OrderConverter {
    
    @BeforeMapping
    protected void beforeMapping(OrderDO entity) {
        // 映射前逻辑
    }
    
    @AfterMapping
    protected void afterMapping(@MappingTarget OrderDTO dto) {
        // 映射后逻辑
    }
}
```

## 最佳实践

| 场景 | 建议 |
|------|------|
| 组件模型 | 使用 `componentModel = "spring"` 配合依赖注入 |
| 单例访问 | 定义 `INSTANCE` 常量或使用 Spring 注入 |
| 复杂映射 | 使用 `default` 方法或 `@Named` 方法 |
| 集合映射 | 定义单个对象映射后，List 映射自动生成 |
| 空值处理 | 使用 `nullValuePropertyMappingStrategy` 配置 |
| 多个 Converter | 使用 `@Mapper(uses = {xxx.class})` 组合 |

## Maven 配置

确保 pom.xml 中正确配置 MapStruct 处理器：

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

## 常见问题

### 1. 属性未映射警告

```java
// 设置未映射属性策略
@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)  // 警告
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE) // 忽略
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)  // 报错（默认）
```

### 2. 循环依赖（Spring）

```java
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
```

### 3. Lombok 集成问题

确保处理器顺序正确：Lombok → lombok-mapstruct-binding → mapstruct-processor
