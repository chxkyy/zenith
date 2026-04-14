# project-rules.md（Trae IDE API请求路径与参数规范）

---

alwaysApply: true

description: 本规则为项目API请求路径与参数传递的强制规范，适用于项目内所有API接口开发，Trae IDE生成、编写接口相关代码时需严格遵循，确保接口路径统一、参数传递规范，提升团队协作效率与接口可维护性。

globs: ["src/**/*.ts", "src/**/*.js", "api/**/*.md"]

---

# 一、核心规则说明

本规则为项目级强制规则，优先级高于个人规则，所有API相关开发（包括接口编写、请求调用、文档生成）均需严格遵守，无特殊情况不得偏离本规范。

# 二、API请求路径规范（强制）

## 2.1 路径大小写要求

路径所有字符必须为全小写，禁止使用大写字母、驼峰命名、帕斯卡命名，这是接口路径统一的基础要求。

✅ 正确示例：/api/user/order、/api/product-categories

❌ 错误示例：/api/User/Order、/api/userOrder、/api/ProductCategories

## 2.2 路径分隔与命名

1. 单词间统一使用连字符（-）分隔，禁止使用下划线（_）或直接拼接；

2. 路径仅用于标识资源，禁止包含动词（如get、add、delete），操作类型通过HTTP方法区分；

3. 资源命名统一使用名词复数形式，标识资源集合；

4. 路径层级控制在3层以内，复杂资源关联通过查询参数实现，避免深层嵌套。

✅ 正确示例：/api/users、/api/orders、/api/product-categories

❌ 错误示例：/api/product_categories、/api/getUser、/api/users/1/orders/2/items

## 2.3 版本规范

API版本号统一放在路径前缀，格式为/api/v{版本号}/xxx，便于版本迭代与兼容管理。

✅ 正确示例：/api/v1/users、/api/v2/orders

# 三、参数传递规范（强制）

## 3.1 核心禁令

绝对禁止将任何参数（包括ID、筛选条件、分页参数等）写入path路径中，仅允许通过查询字符串（?后拼接）或POST请求体（JSON格式）传递参数。

**【强制】仅使用 GET 和 POST 方法，禁止使用 PUT、PATCH、DELETE 等其他HTTP方法**

❌ 禁止示例：

GET /api/users/123、DELETE /api/orders/456、GET /api/users/123/orders、PUT /api/users/456

## 3.2 GET/DELETE请求参数传递

GET、DELETE请求的所有参数（ID、筛选、分页、排序等），必须通过查询字符串（?后拼接）传递，参数之间用&分隔。

✅ 正确示例：

GET /api/v1/users?userId=123&status=active&page=1&size=20

DELETE /api/v1/users?userId=123

GET /api/v1/orders?orderId=789&includeItems=true&startTime=2024-01-01

## 3.3 POST/PUT/PATCH请求参数传递

1. 简单参数（单个ID、状态等）可通过查询字符串传递；

2. 复杂参数（对象、数组、多字段组合等）必须通过JSON格式的请求体传递，同时需设置请求头Content-Type: application/json；

3. 避免混合使用查询参数与请求体传递复杂参数，保证参数传递方式统一。

✅ 正确示例：

POST /api/v1/users

{

"username": "zhangsan",

"age": 25,

"roles": ["admin", "user"],

"departmentId": 2

}

PUT /api/v1/users?userId=123

{

"nickname": "张三",

"phone": "13800138000",

"status": "active"

}

# 四、参数命名规范

所有参数（查询参数、JSON请求体参数）统一使用小驼峰命名法（camelCase），禁止使用下划线、全大写、帕斯卡命名。

✅ 正确示例：userId、userStatus、pageSize、departmentId

❌ 错误示例：user_id、USERID、UserStatus

# 五、HTTP方法与路径匹配规范

**【强制】仅使用 GET 和 POST 方法，禁止使用 PUT、PATCH、DELETE 等其他HTTP方法**

HTTP方法需与路径、操作类型严格匹配，路径仅标识资源，操作由HTTP方法决定，具体对应关系如下：

1. GET：查询资源（参数全通过查询字符串传递）；

2. POST：创建资源、更新资源、删除资源（所有参数通过POST请求体传递）。

# 六、Trae IDE使用说明

1. 本规则文件需放在项目根目录的.trae/rules目录下，命名为project-rules.md，确保Trae IDE能自动识别并生效；

2. 规则生效方式已设置为“始终生效”，Trae IDE在生成、编写接口相关代码时，会自动遵循本规范，无需手动触发；

3. 若需修改本规则，需修改该文件后保存，新规则会立即生效；若规则未生效，可检查文件路径是否正确、规则格式是否符合要求，或重启Trae IDE尝试；

4. 团队成员需统一遵循本规则，提交代码时需检查接口路径与参数传递是否符合规范，确保团队代码风格统一。
> （注：文档部分内容可能由 AI 生成）