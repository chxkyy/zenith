# 通用字段规范

## 1. 数据库规范

所有业务表必须包含以下4个字段：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| create_user_id | bigint | 创建人ID |
| created_time | timestamp | 创建时间，默认值 CURRENT_TIMESTAMP |
| update_user_id | bigint | 修改人ID |
| update_time | timestamp | 修改时间，默认值 CURRENT_TIMESTAMP |

### SQL 模板
```sql
ALTER TABLE 表名 
    ADD COLUMN create_user_id bigint NULL,
    ADD COLUMN created_time timestamp DEFAULT CURRENT_TIMESTAMP NULL,
    ADD COLUMN update_user_id bigint NULL,
    ADD COLUMN update_time timestamp DEFAULT CURRENT_TIMESTAMP NULL;
```

## 2. 后端规范

### 2.1 实体类（DO）
- 添加4个字段，使用驼峰命名
- 时间字段使用 `LocalDateTime` 类型

```java
private Long createUserId;
private Long updateUserId;
private LocalDateTime createdTime;
private LocalDateTime updateTime;
```

### 2.2 DTO类
- 添加对应的4个字段
- 时间字段使用 `LocalDateTime` 类型

### 2.3 Service层
- 创建操作时设置 `createUserId` 和 `createdTime`
- 更新操作时设置 `updateUserId` 和 `updateTime`
- 从当前登录用户上下文获取用户ID

## 3. 前端规范

### 3.1 列表展示
所有列表页面必须展示以下4列：
- 创建人（createUserId）
- 创建时间（createdTime）
- 修改人（updateUserId）
- 修改时间（updateTime）

### 3.2 时间格式化
- 使用 `src/lib/utils.ts` 中的 `formatDateTime` 函数
- 输出格式：`yyyy-MM-dd HH:mm:ss`
- 空值显示：`-`

```typescript
import { formatDateTime } from '../lib/utils';

// 使用示例
{formatDateTime(user.createdTime)}
{formatDateTime(user.updateTime)}
```

### 3.3 空值处理
- 人员ID字段空值显示 `-`
- 时间字段空值由 `formatDateTime` 函数自动处理为 `-`

## 4. 时间处理规范

### 4.1 后端处理
- 实体类使用 `LocalDateTime` 类型
- 通过 fastjson2 自动转换为 timestamp 格式返回前端
- 创建/更新时使用 `LocalDateTime.now()` 获取当前时间

### 4.2 前端处理
- 统一使用 `formatDateTime` 函数格式化时间
- 函数位置：`src/lib/utils.ts`
- 支持类型：Date对象、时间戳（数字）、日期字符串
- 自动处理空值，返回 `-`

### 4.3 格式标准
- 标准格式：`yyyy-MM-dd HH:mm:ss`
- 示例：`2026-04-22 14:30:45`

## 5. 命名规范对照表

| 分类 | 创建人ID | 创建时间 | 修改人ID | 修改时间 |
|------|----------|----------|----------|----------|
| 数据库 | create_user_id | created_time | update_user_id | update_time |
| 后端实体 | createUserId | createdTime | updateUserId | updateTime |
| 前端展示 | 创建人 | 创建时间 | 修改人 | 修改时间 |

## 6. 实施要求

### 6.1 新表创建
- 建表时必须包含4个通用字段
- 设置默认值和约束

### 6.2 现有表改造
- 执行 ALTER TABLE 语句添加字段
- 同步更新后端实体类、DTO类
- 同步更新前端列表展示

### 6.3 代码审查
- 检查所有业务表是否包含4个字段
- 检查所有列表页是否展示4个字段
- 检查时间格式化是否使用统一函数
