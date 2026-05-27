---
name: mybatis
description: MyBatis/MyBatis-Plus 开发规范
when_to_use: 编写 Mapper 接口、Mapper XML 文件、使用 MyBatis-Plus BaseMapper 或 Wrapper 时
paths: "**/*Mapper.java,**/*Mapper.xml"
---

# MyBatis 开发规范

## 一、Mapper 接口规范

### 1.1 方法命名规范

**Mapper 接口方法前缀必须使用以下四种：**

| 前缀 | 用途 | 示例 |
|------|------|------|
| `select` | 查询操作 | `selectById`、`selectList`、`selectCount` |
| `insert` | 新增操作 | `insert`、`insertBatch` |
| `update` | 更新操作 | `update`、`updateStatus`、`updateById` |
| `delete` | 删除操作 | `delete`、`deleteById`、`deleteBatch` |

**禁止使用的前缀：**
- ❌ `find`（应改为 `select`）
- ❌ `get`（应改为 `select`）
- ❌ `query`（应改为 `select`）
- ❌ `count`（应改为 `selectCount`）
- ❌ `clear`（应改为 `delete` 或 `update`）
- ❌ `save`（应改为 `insert`）
- ❌ `search`（应改为 `select`）
- ❌ `list`（应改为 `select`）

### 1.2 接口定义示例

```java
// ✅ 正确示例
@Mapper
public interface SysUserMapper {

    // 单条查询
    SysUserDO selectById(Long id);

    // 列表查询
    List<SysUserDO> selectByCondition(SysUserCondition condition);

    // 统计查询
    Integer selectCountByDeptId(Long deptId);

    // 新增
    void insert(SysUserDO user);

    // 批量新增
    void insertBatch(@Param("list") List<SysUserDO> users);

    // 更新
    void update(SysUserDO user);

    // 删除
    void deleteById(Long id);

    // 批量删除
    void deleteBatch(@Param("ids") List<Long> ids);
}

// ❌ 错误示例
@Mapper
public interface SysUserMapper {
    SysUserDO findById(Long id);           // 错误：应使用 select 前缀
    List<SysUserDO> listAll();             // 错误：应使用 select 前缀
    Integer countByDeptId(Long deptId);    // 错误：应使用 selectCount 前缀
    void save(SysUserDO user);             // 错误：应使用 insert
}
```

### 1.3 参数规范

**参数类型限制：**
- 查询参数：使用 `Condition` 结尾的查询条件类，如 `SysUserCondition`
- 实体参数：使用 `DO` 结尾的数据对象，如 `SysUserDO`
- 简单参数：使用 `@Param` 注解

**参数超过 2 个时，必须封装为 Condition 或使用 @Param 注解：**

```java
// ✅ 正确：参数 <= 2 个，使用 @Param
void updateStatus(@Param("id") Long id, @Param("status") Integer status);

// ✅ 正确：查询条件使用 Condition
List<SysUserDO> selectByCondition(SysUserCondition condition);

// ✅ 正确：实体操作使用 DO
void insert(SysUserDO user);
void update(SysUserDO user);

// ❌ 错误：参数超过 2 个且未封装
void updateStatus(Long id, Integer status, String updateBy, LocalDateTime updateTime);

// ❌ 错误：Mapper 层使用 DTO
void insert(SysUserDTO dto);
```

### 1.4 返回类型规范

**Mapper 层返回类型限制：**
- 只能返回 `DO` 或 `List<DO>`
- 禁止返回 `DTO`、`Map<String, Object>`

| 操作类型 | 返回类型 | 说明 |
|----------|----------|------|
| 单条查询 | `SysUserDO` | 返回 DO |
| 列表查询 | `List<SysUserDO>` | 返回 DO 列表 |
| 统计查询 | `Integer` 或 `Long` | 返回数量 |
| 新增/更新/删除 | `void` 或 `Integer` | Integer 表示影响行数 |

```java
// ✅ 正确：返回 DO
SysUserDO selectById(Long id);
List<SysUserDO> selectByCondition(SysUserCondition condition);

// ❌ 错误：返回 DTO
SysUserDTO selectById(Long id);

// ❌ 错误：返回 Map
List<Map<String, Object>> selectUserList();
```

---

## 二、Mapper XML 规范

### 2.1 文件位置

```
src/main/resources/mapper/
├── SysUserMapper.xml
├── SysRoleMapper.xml
└── ...
```

### 2.2 基本结构

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.model.mapper.SysUserMapper">

    <!-- 字段列表（可复用） -->
    <sql id="columns">
        id, username, password, name, email, phone, status, dept_id, create_time, update_time, deleted
    </sql>

    <!-- 结果映射 -->
    <resultMap id="SysUserResultMap" type="com.example.model.dataobject.SysUserDO">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <result property="deptId" column="dept_id"/>
    </resultMap>

    <!-- 查询语句 -->
    <select id="selectById" resultMap="SysUserResultMap">
        SELECT <include refid="columns"/>
        FROM sys_user
        WHERE id = #{id} AND deleted = 0
    </select>

</mapper>
```

### 2.3 元素定义顺序

```xml
<mapper namespace="...">
    1. <cache>          <!-- 缓存配置 -->
    2. <cache-ref>      <!-- 缓存引用 -->
    3. <resultMap>      <!-- 结果映射 -->
    4. <sql>            <!-- SQL 片段 -->
    5. <insert>         <!-- 插入语句 -->
    6. <update>         <!-- 更新语句 -->
    7. <delete>         <!-- 删除语句 -->
    8. <select>         <!-- 查询语句 -->
</mapper>
```

### 2.4 SQL 片段复用

```xml
<!-- 定义 SQL 片段 -->
<sql id="userColumns">
    id, username, password, name, email, phone, status, dept_id
</sql>

<!-- 使用 SQL 片段 -->
<select id="selectById" resultMap="SysUserResultMap">
    SELECT <include refid="userColumns"/>
    FROM sys_user
    WHERE id = #{id} AND deleted = 0
</select>

<!-- 带参数的 SQL 片段 -->
<sql id="userColumnsWithAlias">
    ${alias}.id, ${alias}.username, ${alias}.name
</sql>

<select id="selectUserWithDept" resultMap="SysUserResultMap">
    SELECT <include refid="userColumnsWithAlias"><property name="alias" value="u"/></include>
    FROM sys_user u
    WHERE u.id = #{id}
</select>
```

### 2.5 ResultMap 规范

```xml
<!-- 基本 ResultMap -->
<resultMap id="SysUserResultMap" type="com.example.model.dataobject.SysUserDO">
    <id property="id" column="id"/>
    <result property="username" column="username"/>
    <result property="deptId" column="dept_id"/>
</resultMap>

<!-- 关联查询 - 嵌套结果 -->
<resultMap id="UserWithDeptResultMap" type="com.example.model.dataobject.SysUserDO">
    <id property="id" column="id"/>
    <association property="dept" javaType="com.example.model.dataobject.SysDeptDO">
        <id property="id" column="dept_id"/>
        <result property="name" column="dept_name"/>
    </association>
</resultMap>

<!-- 集合关联 -->
<resultMap id="UserWithRolesResultMap" type="com.example.model.dataobject.SysUserDO">
    <id property="id" column="id"/>
    <collection property="roles" ofType="com.example.model.dataobject.SysRoleDO">
        <id property="id" column="role_id"/>
        <result property="name" column="role_name"/>
    </collection>
</resultMap>
```

---

## 三、动态 SQL 规范

### 3.1 标签说明

| 标签 | 用途 | 示例场景 |
|------|------|----------|
| `<if>` | 条件判断 | 可选查询条件 |
| `<choose>` | 多选一 | 互斥条件 |
| `<where>` | 智能处理 WHERE | 多条件动态查询 |
| `<set>` | 智能处理 SET | 动态更新字段 |
| `<trim>` | 前后缀处理 | 自定义格式化 |
| `<foreach>` | 集合遍历 | IN 查询、批量操作 |
| `<bind>` | 变量绑定 | 模糊查询拼接 |

### 3.2 条件查询

```xml
<select id="selectByCondition" resultMap="SysUserResultMap">
    SELECT <include refid="columns"/>
    FROM sys_user
    <where>
        <if test="username != null and username != ''">
            AND username LIKE CONCAT('%', #{username}, '%')
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
        <if test="deptId != null">
            AND dept_id = #{deptId}
        </if>
    </where>
    ORDER BY create_time DESC
</select>
```

### 3.3 动态更新

```xml
<update id="update">
    UPDATE sys_user
    <set>
        <if test="username != null">username = #{username},</if>
        <if test="name != null">name = #{name},</if>
        <if test="email != null">email = #{email},</if>
        update_time = NOW()
    </set>
    WHERE id = #{id}
</update>
```

### 3.4 批量操作

```xml
<!-- 批量插入 -->
<insert id="insertBatch" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO sys_user (username, name, email, status)
    VALUES
    <foreach collection="list" item="item" separator=",">
        (#{item.username}, #{item.name}, #{item.email}, #{item.status})
    </foreach>
</insert>

<!-- IN 查询 -->
<select id="selectByIds" resultMap="SysUserResultMap">
    SELECT <include refid="columns"/>
    FROM sys_user
    WHERE id IN
    <foreach collection="ids" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
    AND deleted = 0
</select>
```

### 3.5 多条件择一

```xml
<select id="selectActiveUsers" resultMap="SysUserResultMap">
    SELECT <include refid="columns"/>
    FROM sys_user
    WHERE deleted = 0
    <choose>
        <when test="deptId != null">
            AND dept_id = #{deptId}
        </when>
        <when test="roleCode != null">
            AND role_code = #{roleCode}
        </when>
        <otherwise>
            AND status = 1
        </otherwise>
    </choose>
</select>
```

---

## 四、INSERT 规范

### 4.1 基本插入

```xml
<insert id="insert" parameterType="com.example.model.dataobject.SysUserDO"
        useGeneratedKeys="true" keyProperty="id">
    INSERT INTO sys_user (username, password, name, email, status, create_time, deleted)
    VALUES (#{username}, #{password}, #{name}, #{email}, #{status}, NOW(), 0)
</insert>
```

### 4.2 主键生成

| 数据库 | 推荐方式 |
|--------|----------|
| MySQL | `useGeneratedKeys="true"` |
| PostgreSQL | `useGeneratedKeys="true"` + 指定 `keyColumn` |
| Oracle | `<selectKey>` 序列 |
| H2 | `useGeneratedKeys="true"` |

```xml
<!-- MySQL 自增主键 -->
<insert id="insert" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO sys_user (username, name) VALUES (#{username}, #{name})
</insert>

<!-- Oracle 序列主键 -->
<insert id="insert">
    <selectKey keyProperty="id" resultType="long" order="BEFORE">
        SELECT seq_user.nextval FROM dual
    </selectKey>
    INSERT INTO sys_user (id, username, name) VALUES (#{id}, #{username}, #{name})
</insert>
```

---

## 五、MyBatis-Plus 规范

### 5.1 Mapper 接口

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserDO> {

    // 自定义方法（复杂查询）
    List<SysUserDO> selectByCondition(SysUserCondition condition);
}
```

### 5.2 BaseMapper 内置方法

| 方法 | 说明 |
|------|------|
| `insert(T entity)` | 插入一条记录 |
| `deleteById(Serializable id)` | 根据 ID 删除 |
| `deleteBatchIds(Collection idList)` | 根据 ID 批量删除 |
| `updateById(T entity)` | 根据 ID 更新 |
| `selectById(Serializable id)` | 根据 ID 查询 |
| `selectBatchIds(Collection idList)` | 根据 ID 批量查询 |
| `selectList(Wrapper<T> queryWrapper)` | 根据 wrapper 查询列表 |
| `selectCount(Wrapper<T> queryWrapper)` | 根据 wrapper 查询总数 |
| `selectPage(Page<T> page, Wrapper<T> queryWrapper)` | 分页查询 |

### 5.3 Wrapper 条件构造器

```java
// LambdaQueryWrapper（推荐）
List<SysUserDO> users = userMapper.selectList(
    new LambdaQueryWrapper<SysUserDO>()
        .eq(SysUserDO::getStatus, 1)
        .like(SysUserDO::getUsername, "admin")
        .orderByDesc(SysUserDO::getCreateTime)
);

// UpdateWrapper
userMapper.update(null,
    new LambdaUpdateWrapper<SysUserDO>()
        .set(SysUserDO::getStatus, 0)
        .eq(SysUserDO::getId, 1L)
);
```

### 5.4 Wrapper 常用方法

| 方法 | SQL | 说明 |
|------|-----|------|
| `eq` | `=` | 等于 |
| `ne` | `<>` | 不等于 |
| `gt` | `>` | 大于 |
| `ge` | `>=` | 大于等于 |
| `lt` | `<` | 小于 |
| `le` | `<=` | 小于等于 |
| `between` | `BETWEEN` | 区间 |
| `like` | `LIKE` | 模糊查询 |
| `isNull` | `IS NULL` | 为空 |
| `isNotNull` | `IS NOT NULL` | 非空 |
| `in` | `IN` | 包含 |
| `notIn` | `NOT IN` | 不包含 |
| `orderByAsc` | `ORDER BY ASC` | 升序 |
| `orderByDesc` | `ORDER BY DESC` | 降序 |

### 5.5 分页查询

```java
Page<SysUserDO> page = new Page<>(pageNum, pageSize);
Page<SysUserDO> result = userMapper.selectPage(page,
    new LambdaQueryWrapper<SysUserDO>()
        .eq(SysUserDO::getStatus, 1)
);

List<SysUserDO> list = result.getRecords();
long total = result.getTotal();
```

---

## 六、最佳实践

### 6.1 避免 N+1 问题

```xml
<!-- ❌ 错误：嵌套 Select 导致 N+1 问题 -->
<resultMap id="UserWithRolesResultMap" type="com.example.model.dataobject.SysUserDO">
    <collection property="roles" column="id" select="selectRolesByUserId"/>
</resultMap>

<!-- ✅ 正确：使用关联查询一次查出 -->
<resultMap id="UserWithRolesResultMap" type="com.example.model.dataobject.SysUserDO">
    <id property="id" column="id"/>
    <collection property="roles" ofType="com.example.model.dataobject.SysRoleDO">
        <id property="id" column="role_id"/>
        <result property="name" column="role_name"/>
    </collection>
</resultMap>

<select id="selectUserWithRoles" resultMap="UserWithRolesResultMap">
    SELECT u.*, r.id as role_id, r.name as role_name
    FROM sys_user u
    LEFT JOIN sys_user_role ur ON u.id = ur.user_id
    LEFT JOIN sys_role r ON ur.role_id = r.id
    WHERE u.id = #{id}
</select>
```

### 6.2 参数安全

```xml
<!-- ✅ 正确：使用 #{} 防止 SQL 注入 -->
<select id="selectByUsername" resultMap="SysUserResultMap">
    SELECT * FROM sys_user WHERE username = #{username}
</select>

<!-- ⚠️ 危险：${} 可能导致 SQL 注入，仅用于动态表名、排序列 -->
<select id="selectByOrder" resultMap="SysUserResultMap">
    SELECT * FROM sys_user ORDER BY ${orderColumn}
</select>
```

### 6.3 逻辑删除

```xml
<!-- ✅ 正确：查询时过滤已删除数据 -->
<select id="selectById" resultMap="SysUserResultMap">
    SELECT <include refid="columns"/>
    FROM sys_user
    WHERE id = #{id} AND deleted = 0
</select>

<!-- ✅ 正确：软删除 -->
<update id="deleteById">
    UPDATE sys_user SET deleted = 1, update_time = NOW()
    WHERE id = #{id}
</update>
```

### 6.4 GROUP BY 兼容性（H2 数据库）

```xml
<!-- ❌ 错误：H2 不支持 a.* -->
SELECT a.*, COUNT(r.id) as read_count
FROM announcement a
LEFT JOIN read r ON a.id = r.announcement_id
GROUP BY a.id

<!-- ✅ 正确：列出所有非聚合列 -->
SELECT a.id, a.title, a.content, COUNT(r.id) as read_count
FROM announcement a
LEFT JOIN read r ON a.id = r.announcement_id
GROUP BY a.id, a.title, a.content
```

### 6.5 数据库兼容的时间函数

```xml
<!-- ❌ 错误：NOW() 在某些数据库不兼容 -->
<insert id="insert">
    INSERT INTO sys_user (username, create_time) VALUES (#{username}, NOW())
</insert>

<!-- ✅ 正确：使用 Java 传入时间 -->
<insert id="insert">
    INSERT INTO sys_user (username, create_time) VALUES (#{username}, #{createTime})
</insert>

<!-- ✅ 或使用 CURRENT_TIMESTAMP（H2/MySQL/PostgreSQL 兼容） -->
<insert id="insert">
    INSERT INTO sys_user (username, create_time) VALUES (#{username}, CURRENT_TIMESTAMP)
</insert>
```

---

## 七、检查清单

### Mapper 接口检查

- [ ] 方法前缀是否为 `select`/`insert`/`update`/`delete`
- [ ] 参数类型是否为 `DO` 或 `Condition`
- [ ] 参数是否使用 `@Param` 注解（简单参数）
- [ ] 参数数量是否超过 2 个（超过需封装）
- [ ] 返回类型是否为 `DO` 或 `List<DO>`（禁止 DTO、Map）
- [ ] 是否添加了 `@Mapper` 注解

### Mapper XML 检查

- [ ] 文件位置是否正确（`resources/mapper/`）
- [ ] namespace 是否与接口全限定名一致
- [ ] 是否定义了可复用的 `<sql>` 片段
- [ ] 是否使用了 `<resultMap>` 映射结果
- [ ] 动态 SQL 是否正确使用 `<where>`/`<set>` 标签
- [ ] 是否开启了驼峰转换
- [ ] 查询是否过滤了已删除数据
- [ ] resultMap 的 type 是否为 DO 类型

### SQL 检查

- [ ] 是否使用 `#{}` 防止 SQL 注入
- [ ] 批量操作是否使用 `<foreach>`
- [ ] 是否避免了 N+1 问题
- [ ] 索引列是否避免了函数操作
- [ ] 是否使用软删除而非物理删除
- [ ] GROUP BY 是否包含所有非聚合列（H2 兼容）

---

## 八、配置示例

### 8.1 MyBatis 配置

```yaml
# application.yml
mybatis:
  mapper-locations: classpath/mapper/**/*.xml
  type-aliases-package: com.example.model.dataobject
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 8.2 MyBatis-Plus 配置

```yaml
# application.yml
mybatis-plus:
  mapper-locations: classpath/mapper/**/*.xml
  type-aliases-package: com.example.model.dataobject
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```
