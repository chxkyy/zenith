# 前端通知提示规范

## 1. 概述

本规范定义了项目中前端通知提示的统一使用方式，确保所有页面的用户交互反馈保持一致，提升用户体验。

## 2. Notification 组件使用规范

### 2.1 组件导入

所有需要使用通知提示的组件，必须首先导入 Notification 组件：

```typescript
import Notification from './Notification';
```

### 2.2 状态定义

在组件中定义通知状态（必须包含 key 字段以确保多次通知能正确显示）：

```typescript
const [notification, setNotification] = useState<{
  message: string;
  type: 'success' | 'error' | 'info';
  key: number;
} | null>(null);
```

### 2.3 通知类型

通知支持三种类型：

- **success**：操作成功提示（绿色）
- **error**：操作失败提示（红色）
- **info**：信息提示（蓝色）

### 2.4 显示通知

使用 `setNotification` 函数显示通知（必须包含 key 字段，使用 Date.now() 确保唯一性）：

```typescript
// 成功提示
setNotification({
  message: '操作成功',
  type: 'success',
  key: Date.now()
});

// 失败提示
setNotification({
  message: '操作失败，请重试',
  type: 'error',
  key: Date.now()
});

// 信息提示
setNotification({
  message: '这是一条信息提示',
  type: 'info',
  key: Date.now()
});
```

### 2.5 渲染组件

在组件的 return 语句的最后添加 Notification 组件的渲染（必须使用 key 属性确保重新渲染）：

```typescript
return (
  <div>
    {/* 其他组件内容 */}
    
    {notification && (
      <Notification 
        key={notification.key}
        message={notification.message} 
        type={notification.type} 
      />
    )}
  </div>
);
```

## 3. 禁止行为

### 3.1 禁止使用 alert

**绝对禁止**使用原生的 `alert()` 函数进行用户提示。

```typescript
// ❌ 错误示例
alert('操作成功');

// ✅ 正确示例
setNotification({
  message: '操作成功',
  type: 'success'
});
```

### 3.2 禁止使用 confirm

对于确认操作，使用自定义的模态框组件，而不是原生的 `confirm()` 函数。

```typescript
// ❌ 错误示例
if (window.confirm('确定要删除吗？')) {
  // 删除操作
}

// ✅ 正确示例
// 使用自定义的确认模态框
```

## 4. 使用场景示例

### 4.1 表单提交成功

```typescript
const handleSubmit = async () => {
  try {
    const response = await fetch('/api/xxx', {
      method: 'POST',
      body: JSON.stringify(formData)
    });
    const data = await response.json();
    
    if (data.success) {
      setNotification({
        message: '保存成功',
        type: 'success',
        key: Date.now()
      });
    } else {
      setNotification({
        message: data.errMessage || '保存失败',
        type: 'error',
        key: Date.now()
      });
    }
  } catch (error) {
    setNotification({
      message: '网络错误，请重试',
      type: 'error',
      key: Date.now()
    });
  }
};
```

### 4.2 删除操作成功

```typescript
const handleDelete = async (id: number) => {
  try {
    const response = await fetch(`/api/xxx/${id}`, {
      method: 'DELETE'
    });
    const data = await response.json();
    
    if (data.success) {
      setNotification({
        message: '删除成功',
        type: 'success',
        key: Date.now()
      });
    } else {
      setNotification({
        message: data.errMessage || '删除失败',
        type: 'error',
        key: Date.now()
      });
    }
  } catch (error) {
    setNotification({
      message: '删除失败，请重试',
      type: 'error',
      key: Date.now()
    });
  }
};
```

## 5. 通知消息文案规范

### 5.1 成功消息

- 简洁明了，说明操作已完成
- 格式：`{操作}成功`
- 示例：`保存成功`、`删除成功`、`更新成功`

### 5.2 错误消息

- 清晰说明错误原因
- 优先使用后端返回的错误信息
- 格式：`{操作}失败` 或后端返回的错误信息
- 示例：`保存失败，请重试`、`同类型下字典项值已存在`

### 5.3 信息消息

- 提供有用的信息
- 格式：`{信息内容}`
- 示例：`数据已更新`、`请先选择字典类型`

## 6. 最佳实践

1. **及时反馈**：用户操作后立即显示通知，不要延迟
2. **消息简洁**：通知消息应该简短、清晰、易于理解
3. **状态同步**：显示通知的同时，同步更新相关数据状态
4. **错误优先**：优先显示后端返回的错误信息，而不是硬编码的错误消息
5. **自动消失**：Notification 组件会自动在 3 秒后消失，不需要手动关闭

## 7. 参考示例

已实现 Notification 组件使用的页面：
- `src/components/MenuTable.tsx`
- `src/components/DictTable.tsx`
- `src/components/PermissionTable.tsx`

以上页面可以作为参考，确保新开发的页面遵循相同的规范。
