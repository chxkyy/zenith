import React, { useState } from 'react';
import { Table, Button, Space, Tag, Popconfirm, App, Card, Input, Modal, Form, Select, Switch } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, UndoOutlined, SafetyOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { usePermission } from '../lib/PermissionContext';
import { usePaginatedQuery, useCrudModal, useCrudOperations, createAuditColumns } from '../lib/useCrudTable';
import { post, del } from '../lib/apiClient';
import PermissionAssignModal from './PermissionAssignModal';

interface Role {
  id: number;
  name: string;
  status: number;
  description: string;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

export default function RoleManagement() {
  const { message } = App.useApp();
  const { hasPermission } = usePermission();
  const [form] = Form.useForm();
  const [isPermModalOpen, setIsPermModalOpen] = useState(false);
  const [permRole, setPermRole] = useState<{ id: number; name: string } | null>(null);

  // 分页查询
  const {
    data: roles,
    loading,
    currentPage,
    pageSize,
    totalCount,
    keyword: searchKeyword,
    setKeyword: setSearchKeyword,
    goToPage,
    search: handleSearch,
    refresh,
  } = usePaginatedQuery<Role>({
    apiUrl: '/api/roles/page',
    initialPageSize: 20,
    buildQuery: (page, size, kw) => ({
      pageIndex: page,
      pageSize: size,
      ...(kw ? { keyword: kw } : {}),
    }),
  });

  // 模态框状态
  const { modalOpen, modalMode, editingRecord, openAddModal, openEditModal, closeModal } =
    useCrudModal<Role>();

  // CRUD 操作
  const { save: crudSave, remove: handleDeleteRole, submitting } = useCrudOperations<Role>(
    {
      createUrl: '/api/roles',
      updateUrl: '/api/roles/update',
      deleteUrl: '/api/roles/delete',
      beforeCreate: (values) => ({
        name: values.name,
        status: values.status,
        description: values.description,
      }),
      beforeUpdate: (values) => ({
        id: (editingRecord as Role | null)?.id,
        name: values.name,
        status: values.status,
        description: values.description,
      }),
      successMessages: { create: '角色新增成功', update: '角色编辑成功', delete: '角色删除成功' },
    },
    { refresh },
  );

  const handleSaveRole = async () => {
    try {
      const values = await form.validateFields();
      await crudSave(values, modalMode, editingRecord);
      closeModal();
    } catch {
      // useCrudOperations already handles message.error
    }
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await post('/api/roles/status', { id, status: checked ? 1 : 0 });
      message.success(checked ? '角色已启用' : '角色已禁用');
      refresh();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : '状态切换失败';
      message.error(msg);
    }
  };

  const columns: ColumnsType<Role> = [
    { title: '角色ID', dataIndex: 'id', key: 'id', width: 80 },
    {
      title: '角色名称', dataIndex: 'name', key: 'name', width: 140,
      render: (name) => (
        <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <SafetyOutlined style={{ color: '#9333ea' }} />
          <span style={{ fontWeight: 500 }}>{name}</span>
        </span>
      ),
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 100,
      render: (v, record) => (
        <Switch
          checked={v === 1}
          onChange={(checked) => handleStatusChange(record.id, checked)}
          checkedChildren="启用"
          unCheckedChildren="禁用"
          disabled={record.id === 1}
        />
      ),
    },
    { title: '备注', dataIndex: 'description', key: 'description', width: 160, ellipsis: true, render: (v) => v || '-' },
    ...createAuditColumns<Role>(),
    {
      title: '操作', key: 'action', width: 200, fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          {hasPermission('sys:role:edit') && (
            <Button type="link" size="small" icon={<EditOutlined />}
              onClick={() => { openEditModal(record); }}>
              编辑
            </Button>
          )}
          {hasPermission('sys:role:permission') && (
            <Button type="link" size="small" icon={<SafetyOutlined />}
              onClick={() => { setPermRole({ id: record.id, name: record.name }); setIsPermModalOpen(true); }}>
              权限
            </Button>
          )}
          {hasPermission('sys:role:delete') && record.id !== 1 && (
            <Popconfirm title="确定删除该角色吗？删除后不可恢复" onConfirm={() => handleDeleteRole(record.id)} okText="确定" cancelText="取消">
              <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <Card size="small" title="角色管理"
        extra={
          <Space>
            <Input.Search
              size="small"
              placeholder="搜索角色名称..."
              prefix={<SearchOutlined />}
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              onSearch={handleSearch}
              onPressEnter={handleSearch}
              style={{ width: 200 }}
              allowClear
              onClear={() => { setSearchKeyword(''); goToPage(1); refresh(); }}
            />
            <Button type="primary" icon={<PlusOutlined />} onClick={openAddModal}
              style={{ display: hasPermission('sys:role:add') ? undefined : 'none' }}>
              新增角色
            </Button>
            <Button icon={<UndoOutlined />} onClick={refresh}>刷新</Button>
          </Space>
        }
      >
        <Table<Role>
          columns={columns}
          dataSource={roles}
          rowKey="id"
          size="small"
          loading={loading}
          scroll={{ x: 1200 }}
          pagination={{
            current: currentPage,
            pageSize: pageSize,
            total: totalCount,
            showTotal: (total) => `共 ${total} 条`,
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '50', '100'],
            size: 'middle' as const,
            onChange: goToPage,
          }}
        />
      </Card>

      <Modal
        title={modalMode === 'add' ? '新增角色' : '编辑角色'}
        open={modalOpen}
        onCancel={closeModal}
        onOk={handleSaveRole}
        okText={modalMode === 'add' ? '保存角色' : '更新角色'}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" preserve={false}
          initialValues={{
            name: (editingRecord as Role | null)?.name || '',
            status: (editingRecord as Role | null)?.status ?? 1,
            description: (editingRecord as Role | null)?.description || '',
          }}
        >
          <Form.Item label="角色名称" name="name" rules={[{ required: true, message: '请输入角色名称' }]}>
            <Input placeholder="请输入角色名称" />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={[{ value: 1, label: '启用' }, { value: 0, label: '禁用' }]} />
          </Form.Item>
          <Form.Item label="备注" name="description">
            <Input.TextArea rows={3} placeholder="请输入备注信息" />
          </Form.Item>
        </Form>
      </Modal>

      {permRole && (
        <PermissionAssignModal
          isOpen={isPermModalOpen}
          onClose={() => { setIsPermModalOpen(false); setPermRole(null); }}
          roleId={permRole.id}
          roleName={permRole.name}
        />
      )}
    </div>
  );
}
