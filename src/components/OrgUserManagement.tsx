import React, { useState, useEffect, useRef } from 'react';
import { Table, Button, Space, Tag, Popconfirm, App, Card, Empty, Spin, Input, Modal, Form, Layout, Tree, Select } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, UndoOutlined, UserOutlined, ApartmentOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';

const { Sider, Content } = Layout;

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

interface TreeNodeData {
  key: string;
  title: React.ReactNode;
  orgId: number;
  children?: TreeNodeData[];
}

export default function OrgUserManagement() {
  const { message } = App.useApp();
  const [orgUsers, setOrgUsers] = useState<OrgUser[]>([]);
  const [orgs, setOrgs] = useState<Org[]>([]);
  const [treeData, setTreeData] = useState<TreeNodeData[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedOrgKey, setSelectedOrgKey] = useState<string>('');
  const [selectedOrgId, setSelectedOrgId] = useState<number | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedOrgUser, setSelectedOrgUser] = useState<OrgUser | null>(null);
  const [searchKeyword, setSearchKeyword] = useState('');
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
          orgMap.set(orgDTO.id, {
            id: orgDTO.id,
            name: orgDTO.name,
            parentId: orgDTO.parentId,
            sort: orgDTO.sort || 0,
            status: orgDTO.status ?? 1,
            children: []
          });
        });
        const rootOrgs: Org[] = [];
        orgMap.forEach(org => {
          if (!org.parentId) {
            rootOrgs.push(org);
          } else {
            const parent = orgMap.get(org.parentId);
            if (parent) parent.children?.push(org);
          }
        });
        setOrgs(rootOrgs);

        // 转换为 Tree 组件数据格式
        const convertToTreeData = (orgList: Org[]): TreeNodeData[] => {
          return orgList.map(org => ({
            key: `org-${org.id}`,
            title: <span>{org.name}</span>,
            orgId: org.id,
            children: org.children?.length ? convertToTreeData(org.children) : undefined
          }));
        };
        setTreeData(convertToTreeData(rootOrgs));

        // 默认选中第一个节点
        if (rootOrgs.length > 0 && !selectedOrgKey) {
          const firstKey = `org-${rootOrgs[0].id}`;
          setSelectedOrgKey(firstKey);
          setSelectedOrgId(rootOrgs[0].id);
        }
      }
    } catch (error) {
      console.error('Error fetching organizations:', error);
    }
  };

  const fetchOrgUsers = async (orgId?: number) => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      params.append('orgId', String(orgId || selectedOrgId || ''));
      if (searchKeyword) params.append('username', searchKeyword);

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

  useEffect(() => {
    if (hasFetchedData.current) return;
    hasFetchedData.current = true;
    fetchOrgs().then(() => {
      if (selectedOrgId) fetchOrgUsers(selectedOrgId);
    });
  }, []);

  const handleSelectNode = (keys: React.Key[], info: any) => {
    const key = keys[0]?.toString() || '';
    setSelectedOrgKey(key);
    const orgIdStr = key.replace('org-', '');
    const orgId = orgIdStr ? Number(orgIdStr) : null;
    setSelectedOrgId(orgId);
    setSearchKeyword('');
    fetchOrgUsers(orgId || undefined);
  };

  const handleSaveOrgUser = async () => {
    try {
      const values = await form.validateFields();
      const orgUserDTO = {
        id: selectedOrgUser?.id,
        userId: values.userId,
        orgId: values.orgId || selectedOrgId,
        status: values.status
      };
      const response = await fetch('/api/org-users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orgUserDTO)
      });
      if (!response.ok) throw new Error('Failed to save org user');
      const data = await response.json();
      if (data.success) {
        setIsModalOpen(false);
        message.success(modalMode === 'edit' ? '编辑成功' : '新增成功');
        fetchOrgUsers(selectedOrgId || undefined);
      } else {
        throw new Error(data.errMessage || '保存失败');
      }
    } catch (error: any) {
      console.error('Error saving org user:', error);
      message.error(error.message || '保存组织用户失败');
    }
  };

  const handleDeleteOrgUser = async (id: number) => {
    try {
      const response = await fetch(`/api/org-users/delete`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id })
      });
      if (!response.ok) throw new Error('Failed to delete org user');
      const data = await response.json();
      if (data.success) {
        message.success('删除成功');
        fetchOrgUsers(selectedOrgId || undefined);
      } else {
        throw new Error(data.errMessage || '删除失败');
      }
    } catch (error: any) {
      console.error('Error deleting org user:', error);
      message.error(error.message || '删除失败');
    }
  };

  const columns: ColumnsType<OrgUser> = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: '用户名', dataIndex: 'username', key: 'username', width: 100 },
    { title: '昵称', dataIndex: 'nickname', key: 'nickname', width: 100 },
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
    <Layout style={{ height: 'calc(100vh - 48px)' }}>
      <Sider
        width={240}
        style={{
          background: '#fff',
          borderRight: '1px solid #f0f0f0',
          overflow: 'auto'
        }}
      >
        <div style={{ padding: '12px 16px', borderBottom: '1px solid #f0f0f0', fontWeight: 600, fontSize: 14 }}>
          组织架构
        </div>
        <div style={{ padding: 8 }}>
          {treeData.length > 0 ? (
            <Tree
              showIcon
              defaultExpandAll
              selectedKeys={[selectedOrgKey]}
              onSelect={handleSelectNode as any}
              treeData={treeData}
              style={{ fontSize: 13 }}
              icon={<ApartmentOutlined style={{ color: '#1677ff', fontSize: 12 }} />}
            />
          ) : (
            <Empty description="暂无组织" image={Empty.PRESENTED_IMAGE_SIMPLE} />
          )}
        </div>
      </Sider>

      <Content style={{ padding: 16, overflow: 'auto', background: '#f5f5f5' }}>
        <Card size="small" style={{ marginBottom: 16 }}>
          <Space wrap>
            <Input
              size="small"
              placeholder="搜索用户名..."
              prefix={<SearchOutlined />}
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              onPressEnter={() => fetchOrgUsers(selectedOrgId || undefined)}
              allowClear
              onClear={() => { setSearchKeyword(''); fetchOrgUsers(selectedOrgId || undefined); }}
              style={{ width: 200 }}
            />
            <Button type="primary" size="small" icon={<SearchOutlined />} onClick={() => fetchOrgUsers(selectedOrgId || undefined)}>
              查询
            </Button>
            <Button size="small" icon={<UndoOutlined />} onClick={() => { setSearchKeyword(''); fetchOrgUsers(selectedOrgId || undefined); }}>
              重置
            </Button>
          </Space>
        </Card>

        <Card
          size="small"
          title={`${selectedOrgId ? `当前组织（ID:${selectedOrgId}）` : '请选择组织'} - 人员列表`}
          extra={
            <Button type="primary" icon={<PlusOutlined />} disabled={!selectedOrgId}
              onClick={() => { setModalMode('add'); setSelectedOrgUser(null); setIsModalOpen(true); }}>
              新增人员
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
            locale={{ emptyText: selectedOrgId ? '该组织下暂无人员' : '请先选择左侧组织节点' }}
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
              orgId: selectedOrgUser?.orgId || selectedOrgId || '',
              status: selectedOrgUser?.status ?? 1
            }}>
            <Form.Item label="用户ID" name="userId" rules={[{ required: true, message: '请输入用户ID' }]}>
              <Input placeholder="请输入用户ID" prefix={<UserOutlined />} disabled={modalMode === 'edit'} />
            </Form.Item>
            <Form.Item label="所属组织" name="orgId" rules={[{ required: true, message: '请选择组织' }]}>
              <Input placeholder={`当前选中：ID ${selectedOrgId || '-'}`} disabled />
            </Form.Item>
            <Form.Item label="状态" name="status">
              <Select options={[
                { value: 1, label: '启用' },
                { value: 0, label: '禁用' },
              ]} />
            </Form.Item>
          </Form>
        </Modal>
      </Content>
    </Layout>
  );
}
