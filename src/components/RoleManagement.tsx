import React, { useState, useRef } from 'react';
import { Table, Button, Space, Tag, Popconfirm, App, Card, Input, Modal, Form, Select } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, UndoOutlined, SafetyOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import PermissionAssignModal from './PermissionAssignModal';

interface Role {
  id: number;
  name: string;
  code: string;
  status: number;
  remark: string;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

export default function RoleManagement() {
  const { message } = App.useApp();
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

  const fetchRoles = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/roles/list');
      if (!response.ok) throw new Error('Failed to fetch roles');
      const data = await response.json();
      if (data.success) {
        setRoles(data.data || []);
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

  const handleSaveRole = async () => {
    try {
      const values = await form.validateFields();
      const roleDTO = {
        id: selectedRole?.id,
        name: values.name,
        code: values.code,
        status: values.status,
        remark: values.remark
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
        fetchRoles();
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
        fetchRoles();
      } else {
        throw new Error(data.errMessage || '删除失败');
      }
    } catch (error: any) {
      console.error('Error deleting role:', error);
      message.error(error.message || '删除角色失败');
    }
  };

  const filteredRoles = searchKeyword
    ? roles.filter(role => role.name.toLowerCase().includes(searchKeyword.toLowerCase()) || role.code.toLowerCase().includes(searchKeyword.toLowerCase()))
    : roles;

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
    { title: '角色编码', dataIndex: 'code', key: 'code', width: 140, render: (v) => <code>{v}</code> },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (v) => <Tag color={v === 1 ? 'success' : 'default'}>{v === 1 ? '启用' : '禁用'}</Tag>
    },
    { title: '备注', dataIndex: 'remark', key: 'remark', width: 160, ellipsis: true, render: (v) => v || '-' },
    { title: '创建人', dataIndex: 'createUserName', key: 'createUserName', width: 90, render: (v) => v || '-' },
    { title: '创建时间', dataIndex: 'createdTime', key: 'createdTime', width: 160, render: (v) => formatDateTime(v) },
    { title: '修改人', dataIndex: 'updateUserName', key: 'updateUserName', width: 90, render: (v) => v || '-' },
    { title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', width: 160, render: (v) => formatDateTime(v) },
    {
      title: '操作', key: 'action', width: 200, fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EditOutlined />}
            onClick={() => { setSelectedRole(record); setModalMode('edit'); setIsModalOpen(true); }}>
            编辑
          </Button>
          <Button type="link" size="small" icon={<SafetyOutlined />}
            onClick={() => { setPermRole({ id: record.id, name: record.name }); setIsPermModalOpen(true); }}>
            权限
          </Button>
          <Popconfirm title="确定删除该角色吗？删除后不可恢复" onConfirm={() => handleDeleteRole(record.id)} okText="确定" cancelText="取消">
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <Card size="small" title="角色管理"
        extra={
          <Space>
            <Input size="small" placeholder="搜索角色..." prefix={<SearchOutlined />} value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)} style={{ width: 200 }} allowClear />
            <Button type="primary" icon={<PlusOutlined />} onClick={() => { setModalMode('add'); setSelectedRole(null); setIsModalOpen(true); }}>
              新增角色
            </Button>
            <Button icon={<UndoOutlined />} onClick={fetchRoles}>刷新</Button>
          </Space>
        }
      >
        <Table<Role>
          columns={columns}
          dataSource={filteredRoles}
          rowKey="id"
          size="small"
          loading={loading}
          scroll={{ x: 1300 }}
          pagination={false}
        />
      </Card>

      <Modal
        title={modalMode === 'add' ? '新增角色' : '编辑角色'}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        onOk={handleSaveRole}
        okText={modalMode === 'add' ? '保存角色' : '更新角色'}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}
          initialValues={{
            name: selectedRole?.name || '',
            code: selectedRole?.code || '',
            status: selectedRole?.status ?? 1,
            remark: selectedRole?.remark || ''
          }}>
          <Form.Item label="角色名称" name="name" rules={[{ required: true, message: '请输入角色名称' }]}>
            <Input placeholder="请输入角色名称" />
          </Form.Item>
          <Form.Item label="角色编码" name="code" rules={[{ required: true, message: '请输入角色编码' }]}>
            <Input placeholder="请输入角色编码，如 ROLE_ADMIN" disabled={modalMode === 'edit'} />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select
              options={[
                { value: 1, label: '启用' },
                { value: 0, label: '禁用' },
              ]}
            />
          </Form.Item>
          <Form.Item label="备注" name="remark">
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
