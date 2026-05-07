import React, { useState, useRef } from 'react';
import { Table, Button, Space, Tag, Popconfirm, App, Card, Empty, Spin, Input, Select, Modal, Form } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, UndoOutlined, UserOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';

interface OrgUser {
  id: number;
  userId: number;
  username: string;
  nickname: string;
  orgId: number;
  orgName: string;
  status: number;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

interface Org {
  id: number;
  name: string;
  parentId: number | null;
  sort: number;
  status: number;
  children?: Org[];
}

export default function OrgUserManagement() {
  const { message } = App.useApp();
  const [orgUsers, setOrgUsers] = useState<OrgUser[]>([]);
  const [orgs, setOrgs] = useState<Org[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedOrg, setSelectedOrg] = useState<Org | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedOrgUser, setSelectedOrgUser] = useState<OrgUser | null>(null);
  const [searchParams, setSearchParams] = useState({ username: '', orgId: null as number | null });
  const hasFetchedData = useRef(false);
  const [form] = Form.useForm();

  const fetchOrgs = async () => {
    try {
      const response = await fetch('/api/orgs');
      if (!response.ok) throw new Error('Failed to fetch organizations');
      const data = await response.json();
      if (data.success && data.data) {
        const orgMap = new Map<number, Org>();
        data.data.forEach((orgDTO: any) => {
          orgMap.set(orgDTO.id, { id: orgDTO.id, name: orgDTO.name, parentId: orgDTO.parentId, sort: orgDTO.sort || 0, status: orgDTO.status ?? 1, children: [] });
        });
        const rootOrgs: Org[] = [];
        orgMap.forEach(org => {
          if (!org.parentId) { rootOrgs.push(org); }
          else { const parent = orgMap.get(org.parentId); if (parent) parent.children?.push(org); }
        });
        setOrgs(rootOrgs);
      }
    } catch (error) {
      console.error('Error fetching organizations:', error);
    }
  };

  const fetchOrgUsers = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (searchParams.username) params.append('username', searchParams.username);
      if (searchParams.orgId) params.append('orgId', String(searchParams.orgId));

      const response = await fetch(`/api/org-users?${params.toString()}`);
      if (!response.ok) throw new Error('Failed to fetch org users');
      const data = await response.json();
      if (data.success && data.data) {
        setOrgUsers(data.data);
      } else {
        setOrgUsers([]);
      }
    } catch (error) {
      console.error('Error fetching org users:', error);
      message.error('获取组织用户列表失败');
    } finally {
      setLoading(false);
    }
  };

  React.useEffect(() => {
    if (hasFetchedData.current) return;
    hasFetchedData.current = true;
    fetchOrgs();
    fetchOrgUsers();
  }, []);

  const handleSaveOrgUser = async () => {
    try {
      const values = await form.validateFields();
      const orgUserDTO = {
        id: selectedOrgUser?.id,
        userId: values.userId,
        orgId: values.orgId,
        status: values.status
      };
      const response = await fetch('/api/org-users', {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(orgUserDTO)
      });
      if (!response.ok) throw new Error('Failed to save org user');
      const data = await response.json();
      if (data.success) {
        setIsModalOpen(false);
        message.success(modalMode === 'edit' ? '编辑成功' : '新增成功');
        fetchOrgUsers();
      } else {
        throw new Error(data.errMessage || '保存失败');
      }
    } catch (error: any) {
      console.error('Error saving org user:', error);
      message.error(error.message || '保存失败');
    }
  };

  const handleDeleteOrgUser = async (id: number) => {
    try {
      const response = await fetch(`/api/org-users/delete`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id })
      });
      if (!response.ok) throw new Error('Failed to delete org user');
      const data = await response.json();
      if (data.success) {
        message.success('删除成功');
        fetchOrgUsers();
      } else {
        throw new Error(data.errMessage || '删除失败');
      }
    } catch (error: any) {
      console.error('Error deleting org user:', error);
      message.error(error.message || '删除失败');
    }
  };

  const flattenOrgs = (orgList: Org[]): Org[] => {
    return orgList.reduce((result: Org[], org) => {
      result.push(org);
      if (org.children?.length) result.push(...flattenOrgs(org.children));
      return result;
    }, []);
  };

  const allOrgsFlat = flattenOrgs(orgs);

  const columns: ColumnsType<OrgUser> = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: '用户名', dataIndex: 'username', key: 'username', width: 100 },
    { title: '昵称', dataIndex: 'nickname', key: 'nickname', width: 100 },
    { title: '所属组织', dataIndex: 'orgName', key: 'orgName', width: 120 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (v) => <Tag color={v === 1 ? 'success' : 'default'}>{v === 1 ? '启用' : '禁用'}</Tag>
    },
    { title: '创建人', dataIndex: 'createUserName', key: 'createUserName', width: 90, render: (v) => v || '-' },
    { title: '创建时间', dataIndex: 'createdTime', key: 'createdTime', width: 160, render: (v) => formatDateTime(v) },
    { title: '修改人', dataIndex: 'updateUserName', key: 'updateUserName', width: 90, render: (v) => v || '-' },
    { title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', width: 160, render: (v) => formatDateTime(v) },
    {
      title: '操作', key: 'action', width: 120, fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EditOutlined />}
            onClick={() => { setSelectedOrgUser(record); setModalMode('edit'); setIsModalOpen(true); }}>
            编辑
          </Button>
          <Popconfirm title="确定删除该组织用户关系吗？" onConfirm={() => handleDeleteOrgUser(record.id)} okText="确定" cancelText="取消">
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <Card size="small">
        <Space wrap>
          <Space>
            <span style={{ fontSize: 14, color: '#666' }}>用户名</span>
            <Input size="small" placeholder="请输入用户名" value={searchParams.username}
              onChange={(e) => setSearchParams({ ...searchParams, username: e.target.value })}
              onPressEnter={fetchOrgUsers} style={{ width: 140 }} />
          </Space>
          <Space>
            <span style={{ fontSize: 14, color: '#666' }}>组织</span>
            <Select size="small" value={searchParams.orgId || undefined} placeholder="全部"
              onChange={(v) => setSearchParams({ ...searchParams, orgId: v || null })}
              style={{ width: 160 }} allowClear
              options={allOrgsFlat.map(org => ({ value: org.id, label: org.name }))}
            />
          </Space>
          <Button type="primary" size="small" icon={<SearchOutlined />} onClick={fetchOrgUsers}>查询</Button>
          <Button size="small" icon={<UndoOutlined />} onClick={() => setSearchParams({ username: '', orgId: null })}>重置</Button>
        </Space>
      </Card>

      <Card size="small" title="组织用户管理"
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={() => { setModalMode('add'); setSelectedOrgUser(null); setIsModalOpen(true); }}>
            新增
          </Button>
        }
      >
        <Table<OrgUser>
          columns={columns}
          dataSource={orgUsers}
          rowKey="id"
          size="small"
          loading={loading}
          scroll={{ x: 1100 }}
          pagination={false}
        />
      </Card>

      <Modal
        title={modalMode === 'add' ? '新增组织用户' : '编辑组织用户'}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        onOk={handleSaveOrgUser}
        okText={modalMode === 'add' ? '保存' : '更新'}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}
          initialValues={{
            userId: selectedOrgUser?.userId || '',
            orgId: selectedOrgUser?.orgId || '',
            status: selectedOrgUser?.status ?? 1
          }}>
          <Form.Item label="用户ID" name="userId" rules={[{ required: true, message: '请输入用户ID' }]}>
            <Input placeholder="请输入用户ID" prefix={<UserOutlined />} disabled={modalMode === 'edit'} />
          </Form.Item>
          <Form.Item label="所属组织" name="orgId" rules={[{ required: true, message: '请选择组织' }]}>
            <Select placeholder="请选择组织"
              options={allOrgsFlat.map(org => ({ value: org.id, label: org.name }))}
            />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select
              options={[
                { value: 1, label: '启用' },
                { value: 0, label: '禁用' },
              ]}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
