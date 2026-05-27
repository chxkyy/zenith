import React, { useState, useRef } from 'react';
import { Table, Button, Space, Tag, Popconfirm, App, Card, Input, Modal, Form, Select, Switch } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, UndoOutlined, SafetyOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { usePermission } from '../lib/PermissionContext';
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
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedRole, setSelectedRole] = useState<Role | null>(null);
  const [isPermModalOpen, setIsPermModalOpen] = useState(false);
  const [permRole, setPermRole] = useState<{ id: number; name: string } | null>(null);
  const hasFetchedRoles = useRef(false);
  const [form] = Form.useForm();
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalCount, setTotalCount] = useState(0);

  const fetchRoles = async (page?: number, size?: number, keyword?: string) => {
    setLoading(true);
    try {
      const response = await fetch('/api/roles/page', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          pageIndex: page ?? currentPage,
          pageSize: size ?? pageSize,
          keyword: keyword ?? searchKeyword ?? undefined,
        }),
      });
      if (!response.ok) throw new Error('Failed to fetch roles');
      const data = await response.json();
      if (data.success) {
        setRoles(data.data || []);
        setTotalCount(data.totalCount || 0);
      } else {
        message.error(data.errMessage || '获取角色列表失败');
      }
    } catch (error: any) {
      console.error('Error fetching roles:', error);
      message.error(error.message || '获取角色列表失败');
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    if (hasFetchedRoles.current) return;
    hasFetchedRoles.current = true;
    fetchRoles();
  }, []);

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      const response = await fetch('/api/roles/status', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id, status: checked ? 1 : 0 }),
      });
      if (!response.ok) throw new Error('Failed to change status');
      const data = await response.json();
      if (data.success) {
        message.success(checked ? '角色已启用' : '角色已禁用');
        fetchRoles(currentPage, pageSize);
      } else {
        throw new Error(data.errMessage || '状态切换失败');
      }
    } catch (error: any) {
      console.error('Error changing status:', error);
      message.error(error.message || '状态切换失败');
    }
  };

  const handleSaveRole = async () => {
    try {
      const values = await form.validateFields();
      const roleDTO = {
        id: selectedRole?.id,
        name: values.name,
        status: values.status,
        description: values.description
      };
      const url = modalMode === 'add' ? '/api/roles' : '/api/roles/update';
      const response = await fetch(url, {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(roleDTO)
      });
      if (!response.ok) throw new Error('Failed to save role');
      const data = await response.json();
      if (data.success) {
        setIsModalOpen(false);
        message.success(modalMode === 'add' ? '角色新增成功' : '角色编辑成功');
        fetchRoles(currentPage, pageSize);
      } else {
        throw new Error(data.errMessage || '保存失败');
      }
    } catch (error: any) {
      console.error('Error saving role:', error);
      message.error(error.message || '保存角色失败');
    }
  };

  const handleDeleteRole = async (id: number) => {
    try {
      const response = await fetch(`/api/roles/delete`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id })
      });
      if (!response.ok) throw new Error('Failed to delete role');
      const data = await response.json();
      if (data.success) {
        message.success('角色删除成功');
        fetchRoles(currentPage, pageSize);
      } else {
        throw new Error(data.errMessage || '删除失败');
      }
    } catch (error: any) {
      console.error('Error deleting role:', error);
      message.error(error.message || '删除角色失败');
    }
  };

  const handleSearch = () => {
    setCurrentPage(1);
    fetchRoles(1, pageSize, searchKeyword);
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
      )
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
      )
    },
    { title: '备注', dataIndex: 'description', key: 'description', width: 160, ellipsis: true, render: (v) => v || '-' },
    { title: '创建人', dataIndex: 'createUserName', key: 'createUserName', width: 90, render: (v) => v || '-' },
    { title: '创建时间', dataIndex: 'createdTime', key: 'createdTime', width: 160, render: (v) => formatDateTime(v) },
    { title: '修改人', dataIndex: 'updateUserName', key: 'updateUserName', width: 90, render: (v) => v || '-' },
    { title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', width: 160, render: (v) => formatDateTime(v) },
    {
      title: '操作', key: 'action', width: 200, fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          {hasPermission('sys:role:edit') && (
          <Button type="link" size="small" icon={<EditOutlined />}
            onClick={() => { setSelectedRole(record); setModalMode('edit'); setIsModalOpen(true); }}>
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
      )
    }
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
              onClear={() => { setSearchKeyword(''); setCurrentPage(1); fetchRoles(1, pageSize, ''); }}
            />
            <Button type="primary" icon={<PlusOutlined />} onClick={() => { setModalMode('add'); setSelectedRole(null); setIsModalOpen(true); }} style={{ display: hasPermission('sys:role:add') ? undefined : 'none' }}>
              新增角色
            </Button>
            <Button icon={<UndoOutlined />} onClick={() => fetchRoles()}>刷新</Button>
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
            size: 'default',
            onChange: (page, size) => {
              setCurrentPage(page);
              setPageSize(size);
              fetchRoles(page, size);
            },
          }}
        />
      </Card>

      <Modal
        title={modalMode === 'add' ? '新增角色' : '编辑角色'}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        onOk={handleSaveRole}
        okText={modalMode === 'add' ? '保存角色' : '更新角色'}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" preserve={false}
          initialValues={{
            name: selectedRole?.name || '',
            status: selectedRole?.status ?? 1,
            description: selectedRole?.description || ''
          }}>
          <Form.Item label="角色名称" name="name" rules={[{ required: true, message: '请输入角色名称' }]}>
            <Input placeholder="请输入角色名称" />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select
              options={[
                { value: 1, label: '启用' },
                { value: 0, label: '禁用' },
              ]}
            />
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
