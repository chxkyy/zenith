# 前端分页规范

## 1. 概述

本规范定义了项目中所有 Ant Design Table 组件分页的统一标准，确保所有列表页面分页行为一致、用户体验统一。

## 2. 分页配置标准

### 2.1 强制配置项

所有 Table 组件的 `pagination` 属性**必须**使用以下标准配置：

```tsx
pagination={{
  current: currentPage,
  pageSize: pageSize,
  total: totalCount,
  showTotal: (total) => `共 ${total} 条`,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100'],
  size: 'default',
  onChange: (page, size) => {
    setCurrentPage(page);
    setPageSize(size);
    fetchData(?, page, size);  // 根据实际业务传入参数
  },
}}
```

### 2.2 配置项说明

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `current` | `currentPage` state | 当前页码，从1开始 |
| `pageSize` | `pageSize` state | 每页条数，默认 **20** |
| `total` | `totalCount` state | 总记录数，从接口返回 |
| `showTotal` | ``(total) => \`共 ${total} 条\`` | 总数显示在右侧，固定文案格式 |
| `showSizeChanger` | `true` | 显示每页条数下拉选择器 |
| `pageSizeOptions` | `['10', '20', '50', '100']` | 每页条数选项，固定4个选项 |
| `size` | `'default'` | 分页组件尺寸，使用默认尺寸 |
| `onChange` | 回调函数 | 页码/每页条数变化时触发数据请求 |

### 2.3 分页效果

```
每页 [20 ▼]   ◀ 1  2  3  ...  8  ▶   共 156 条
```

- 左侧：每页条数下拉框（10/20/50/100）
- 中间：页码导航（上一页/下一页）
- 右侧：总条数显示

### 2.4 禁止事项

- ❌ 禁止设置 `pagination={false}`（除非数据量确定极小且永不分页，如 mock 数据 <10 条）
- ❌ 禁止使用自定义分页组件（必须使用 antd Table 内置分页）
- ❌ 禁止添加 `showQuickJumper`（快速跳转）（本项目不需要）
- ❌ 禁止使用非标准的 `pageSizeOptions`（如 `['5','10','20','50']`）
- ❌ 禁止在搜索栏手动添加"共 X 条"文字（与 showTotal 重复）

## 3. State 定义规范

### 3.1 必需的状态变量

每个带分页的列表组件**必须**定义以下 3 个状态变量：

```tsx
const [currentPage, setCurrentPage] = useState(1);      // 当前页码，默认第1页
const [pageSize, setPageSize] = useState(20);            // 每页条数，默认20条
const [totalCount, setTotalCount] = useState(0);          // 总记录数，初始0
```

### 3.2 默认值规定

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `currentPage` | `1` | 从第1页开始 |
| `pageSize` | `20` | 每页默认20条 |
| `totalCount` | `0` | 初始无数据时为0 |

## 4. 数据请求规范

### 4.1 服务端分页（推荐）

当后端支持 `POST /api/xxx/page` 接口时：

```tsx
const fetchData = async (page?: number, size?: number) => {
  setLoading(true);
  try {
    const response = fetch('/api/xxx/page', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        pageIndex: page || currentPage,
        pageSize: size || pageSize,
        // ... 其他查询参数
      }),
    });
    const data = await response.json();
    if (data.success && data.data) {
      setDataList(data.data);
      setTotalCount(data.totalCount || 0);
    }
  } finally {
    setLoading(false);
  }
};
```

**适用场景：**
- 组织用户管理（OrgUserManagement）
- 字典管理（DictTable）— 字典类型 + 字典项双表格
- 通知公告（NoticeTable）
- 文件管理（FileTable）

### 4.2 前端分页（备选）

当后端仅提供全量列表接口（如 GET `/api/xxx/list`）时：

```tsx
// dataSource 使用 slice 切片
<Table
  dataSource={allData.slice((currentPage - 1) * pageSize, currentPage * pageSize)}
  pagination={{
    current: currentPage,
    pageSize: pageSize,
    total: allData.length,        // 使用前端数组长度
    // ... 标准配置
  }}
/>
```

**适用场景：**
- 角色管理（RoleManagement）— `/api/roles/list`
- 操作日志（LogOper）— `/api/oper-logs?...`
- 登录日志（LogLogin）— `/api/login-logs?...`
- 异常日志（LogError）— `/api/error-logs?...`
- 在线用户（OnlineUsersTable）— `/api/online-users/list`

### 4.3 无需分页

以下场景可保持 `pagination={false}` 或不设置：
- Mock 数据且记录数 < 10 条（CacheTable、ConfigTable）
- 子级数据量固定的表格（MenuManagement 的权限表）

## 5. 交互行为规范

### 5.1 搜索/查询归页

用户点击**搜索/查询按钮**、**按回车搜索**、**清空搜索**时，**必须**重置到第1页：

```tsx
// ✅ 正确：搜索时归页
const handleSearch = () => {
  setCurrentPage(1);
  fetchData(undefined, undefined, 1);  // 传1表示第1页
};

// Input 回车搜索
<Input
  onPressEnter={() => { setCurrentPage(1); fetchData(? , 1); }}
  onClear={() => { setSearchKeyword(''); setCurrentPage(1); fetchData(? , 1); }}
/>

// 查询按钮
<Button onClick={() => { setCurrentPage(1); fetchData(? , 1); }}>查询</Button>

// 重置按钮
<Button onClick={() => { setSearchKeyword(''); setCurrentPage(1); fetchData(? , 1); }}>重置</Button>
```

### 5.2 切换节点/分类归页

当切换树节点、Tab 分类等导致数据源变化时，**必须**重置到第1页：

```tsx
// ✅ 正确：切换树节点归页
const handleSelectNode = (keys, info) => {
  setSelectedOrgId(orgId);
  setCurrentPage(1);
  fetchOrgUsers(orgId, 1);
};
```

### 5.3 新增/编辑/删除操作

新增、编辑、删除操作完成后刷新数据时，**保持在当前页**（不要跳回第1页）：

```tsx
// ✅ 正确：增删改后保持当前页
const handleDelete = async (id) => {
  await deleteApi(id);
  message.success('删除成功');
  fetchData(selectedOrgId, currentPage, pageSize);  // 保持当前页
};
```

## 6. 完整代码模板

```tsx
export default function XxxTable() {
  const { message } = App.useApp();
  const [dataList, setDataList] = useState<Xxx[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');

  // ====== 分页状态（强制）=====
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalCount, setTotalCount] = useState(0);

  // ====== 数据请求 ======
  const fetchData = async (page?: number, size?: number) => {
    setLoading(true);
    try {
      const response = await fetch('/api/xxx/page', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          pageIndex: page ?? currentPage,
          pageSize: size ?? pageSize,
          keyword: searchKeyword || undefined,
        }),
      });
      const data = await response.json();
      if (data.success && data.data) {
        setDataList(data.data);
        setTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      message.error('获取数据失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, []);

  // ====== 搜索归页 ======
  const handleSearch = () => { setCurrentPage(1); fetchData(1); };

  return (
    <div>
      {/* 搜索栏 */}
      <Input.Search
        value={searchKeyword}
        onChange={(e) => setSearchKeyword(e.target.value)}
        onSearch={handleSearch}
        onPressEnter={() => { setCurrentPage(1); fetchData(1); }}
      />
      <Button onClick={handleSearch}>查询</Button>

      {/* 表格 - 标准分页配置 */}
      <Table<Xxx>
        columns={columns}
        dataSource={dataList}
        rowKey="id"
        size="small"
        loading={loading}
        pagination={{
          current: currentPage,
          pageSize: pageSize,
          total: totalCount,
          showTotal: (total) => `共 ${total} 条`,
          showSizeChanger: true,
          pageSizeOptions: ['10', '20', '50', '100'],
          size: 'default',
          onChange: (page, size) => {
            setCurrentPage(page);
            setPageSize(size);
            fetchData(page, size);
          },
        }}
      />
    </div>
  );
}
```

## 7. 已实施组件清单

| 组件文件 | 分页模式 | 接口 |
|---------|---------|------|
| OrgUserManagement.tsx | 服务端分页 | POST /api/users/page |
| DictTable.tsx（类型表+项目表） | 服务端分页 | POST /api/dicts/page, POST /api/dict/items/page |
| NoticeTable.tsx | 服务端分页 | POST /api/notices/page |
| FileTable.tsx | 服务端分页 | POST /api/files/page |
| RoleManagement.tsx | 前端分页 | GET /api/roles/list |
| LogOper.tsx | 前端分页 | GET /api/oper-logs?... |
| LogLogin.tsx | 前端分页 | GET /api/login-logs?... |
| LogError.tsx | 前端分页 | GET /api/error-logs?... |
| OnlineUsersTable.tsx | 前端分页 | GET /api/online-users/list |
| CacheTable.tsx | 不分页（mock数据） | - |
| ConfigTable.tsx | 不分页（mock数据） | - |
| MenuManagement.tsx | 不分页（权限子表数据量小） | - |
