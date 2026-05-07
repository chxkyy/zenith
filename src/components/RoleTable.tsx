import React, { useState, useEffect, useRef } from 'react';
import { Table, Button, Tag, Popconfirm, App, Space, Input } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  LockOutlined,
  UnlockOutlined,
  SafetyCertificateOutlined,
  SearchOutlined,
  UserSwitchOutlined,
} from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';

interface Role {
  id: number;
  name: string;
  code: string;
  description: string;
  status: number;
  memberCount: number;
  createUserId: number;
  updateUserId: number;
  createdTime: string;
  updateTime: string;
  createUserName?: string;
  updateUserName?: string;
}

export default function RoleTable() {
  const { message } = App.useApp();
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchText, setSearchText] = useState('');
  const hasFetchedRoles = useRef(false);

  const fetchRoles = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/roles/list');
      if (!response.ok) {
        throw new Error('Failed to fetch roles');
      }
      const data = await response.json();
      if (data.success && data.data) {
        setRoles(data.data);
      }
    } catch (error) {
      console.error('获取角色列表失败:', error);
      message.error('获取角色列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (hasFetchedRoles.current) return;
    hasFetchedRoles.current = true;
    fetchRoles();
  }, []);

  const handleEditRole = (role: Role) => {
    console.log('编辑角色:', role);
  };

  const handleDeleteRole = async (id: number) => {
    setLoading(true);
    try {
      const res = await fetch('/api/roles/delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id }),
      });
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }
      const text = await res.text();
      const data = text ? JSON.parse(text) : { success: true };
      if (data.success) {
        message.success('删除成功');
        await fetchRoles();
      } else {
        message.error(data.errMessage || '删除角色失败');
      }
    } catch (err) {
      console.error('删除角色失败:', err);
      message.error('删除角色失败，请检查网络');
    } finally {
      setLoading(false);
    }
  };

  const handleChangeStatus = async (id: number, currentStatus: number) => {
    const newStatus = currentStatus === 1 ? 0 : 1;
    setLoading(true);
    try {
      const res = await fetch('/api/roles/status', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id, status: newStatus }),
      });
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }
      const text = await res.text();
      const data = text ? JSON.parse(text) : { success: true };
      if (data.success) {
        message.success(`角色已${newStatus === 1 ? '启用' : '禁用'}`);
        await fetchRoles();
      } else {
        message.error(data.errMessage || '状态切换失败');
      }
    } catch (err) {
      console.error('状态切换失败:', err);
      message.error('状态切换失败，请检查网络');
    } finally {
      setLoading(false);
    }
  };

  const handleAssignPermissions = (role: Role) => {
    console.log('分配权限:', role);
  };

  const filteredRoles = roles.filter(
    (role) =>
      role.name.toLowerCase().includes(searchText.toLowerCase()) ||
      role.code.toLowerCase().includes(searchText.toLowerCase())
  );

  const columns: ColumnsType<Role> = [
    {
      title: '角色名称',
      dataIndex: 'name',
      key: 'name',
      render: (name: string) => (
        <Space>
          <SafetyCertificateOutlined style={{ color: '#4f46e5' }} />
          <span style={{ fontWeight: 600 }}>{name}</span>
        </Space>
      ),
    },
    {
      title: '角色编码',
      dataIndex: 'code',
      key: 'code',
      render: (code: string) => <code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{code}</code>,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '成员数',
      dataIndex: 'memberCount',
      key: 'memberCount',
      width: 80,
      align: 'center',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      align: 'center',
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'red'} icon={status === 1 ? <UnlockOutlined /> : <LockOutlined />}>
          {status === 1 ? '启用' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '创建人',
      dataIndex: 'createUserName',
      key: 'createUserName',
      width: 90,
      render: (val: string) => val || '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 170,
      render: (val: string) => formatDateTime(val),
    },
    {
      title: '修改人',
      dataIndex: 'updateUserName',
      key: 'updateUserName',
      width: 90,
      render: (val: string) => val || '-',
    },
    {
      title: '修改时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 170,
      render: (val: string) => formatDateTime(val),
    },
    {
      title: '操作',
      key: 'action',
      width: 240,
      render: (_: unknown, record: Role) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditRole(record)}>
            编辑
          </Button>
          <Popconfirm
            title={`确定要将角色状态切换为${record.status === 1 ? '禁用' : '启用'}吗？`}
            onConfirm={() => handleChangeStatus(record.id, record.status)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" size="small" icon={record.status === 1 ? <LockOutlined /> : <UnlockOutlined />}>
              {record.status === 1 ? '禁用' : '启用'}
            </Button>
          </Popconfirm>
          <Button type="link" size="small" icon={<UserSwitchOutlined />} onClick={() => handleAssignPermissions(record)}>
            分配权限
          </Button>
          <Popconfirm
            title="删除后角色数据不可恢复，关联用户自动解除该角色，是否确认删除？"
            onConfirm={() => handleDeleteRole(record.id)}
            okText="确定"
            cancelText="取消"
            okButtonProps={{ danger: true }}
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <div>
          <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>角色管理</h2>
          <p style={{ color: '#64748b', marginTop: 4 }}>配置系统角色及其关联的资源权限。</p>
        </div>
        <Button type="primary" icon={<PlusOutlined />}>
          新增角色
        </Button>
      </div>

      <div style={{ marginBottom: 16 }}>
        <Input
          placeholder="搜索角色名称或编码..."
          prefix={<SearchOutlined />}
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          style={{ width: 280 }}
          allowClear
        />
      </div>

      <Table<Role>
        columns={columns}
        dataSource={filteredRoles}
        rowKey="id"
        loading={loading}
        size="small"
        pagination={false}
      />
    </div>
  );
}
