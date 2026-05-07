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
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalCount, setTotalCount] = useState(0);
  const hasFetchedData = useRef(false);
  const [form] = Form.useForm();

  const fetchOrgs = async () => {
    try {
      const response = await fetch('/api/orgs/page', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pageIndex: 1, pageSize: 1000 })
      });
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
          const firstId = rootOrgs[0].id;
          setSelectedOrgKey(firstKey);
          setSelectedOrgId(firstId);
          fetchOrgUsers(firstId);
        }
      }
    } catch (error) {
      console.error('Error fetching organizations:', error);
    }
  };

  const fetchOrgUsers = async (orgId?: number, page?: number, size?: number) => {
    setLoading(true);
    try {
      const response = await fetch('/api/users/page', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          pageIndex: page || currentPage,
          pageSize: size || pageSize,
          orgId: orgId || selectedOrgId,
          keyword: searchKeyword || undefined
        })
      });
      if (!response.ok) throw new Error('Failed to fetch users');
      const data = await response.json();
      if (data.success && data.data) {
        setOrgUsers(data.data);
        setTotalCount(data.totalCount || 0);
      } else {
        setOrgUsers([]);
        setTotalCount(0);
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
    fetchOrgs();
  }, []);

  const handleSelectNode = (keys: React.Key[], info: any) => {
    const key = keys[0]?.toString() || '';
    setSelectedOrgKey(key);
    const orgIdStr = key.replace('org-', '');
    const orgId = orgIdStr ? Number(orgIdStr) : null;
    setSelectedOrgId(orgId);
    setSearchKeyword('');
    setCurrentPage(1);
    fetchOrgUsers(orgId, 1);
  };

  const handleSaveOrgUser = async () => {
    try {
      const values = await form.validateFields();
      if (modalMode === 'add') {
        const response = await fetch('/api/users', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            username: values.userId,
            nickname: values.nickname || values.userId,
            orgId: selectedOrgId,
            status: values.status ?? 1
          })
        });
        if (!response.ok) throw new Error('Failed to save user');
        const data = await response.json();
        if (data.success) {
          setIsModalOpen(false);
          message.success('新增成功');
          fetchOrgUsers(selectedOrgId || undefined);
        } else {
          throw new Error(data.errMessage || '新增失败');
        }
      } else {
        const response = await fetch('/api/users/update', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            id: selectedOrgUser?.id,
            status: values.status
          })
        });
        if (!response.ok) throw new Error('Failed to update user');
        const data = await response.json();
        if (data.success) {
          setIsModalOpen(false);
          message.success('编辑成功');
          fetchOrgUsers(selectedOrgId || undefined);
        } else {
          throw new Error(data.errMessage || '编辑失败');
        }
      }
    } catch (error: any) {
      console.error('Error saving:', error);
      message.error(error.message || '操作失败');
    }
  };

  const handleDeleteOrgUser = async (id: number) => {
    try {
      const response = await fetch(`/api/users/delete`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id })
      });
      if (!response.ok) throw new Error('Failed to delete user');
      const data = await response.json();
      if (data.success) {
        message.success('删除成功');
        fetchOrgUsers(selectedOrgId || undefined);
      } else {
        throw new Error(data.errMessage || '删除失败');
      }
    } catch (error: any) {
      console.error('Error deleting user:', error);
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
              onPressEnter={() => { setCurrentPage(1); fetchOrgUsers(selectedOrgId || undefined, 1); }}
              allowClear
              onClear={() => { setSearchKeyword(''); setCurrentPage(1); fetchOrgUsers(selectedOrgId || undefined, 1); }}
              style={{ width: 200 }}
            />
            <Button type="primary" size="small" icon={<SearchOutlined />} onClick={() => { setCurrentPage(1); fetchOrgUsers(selectedOrgId || undefined, 1); }}>
              查询
            </Button>
            <Button size="small" icon={<UndoOutlined />} onClick={() => { setSearchKeyword(''); setCurrentPage(1); fetchOrgUsers(selectedOrgId || undefined, 1); }}>
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
                fetchOrgUsers(selectedOrgId || undefined, page, size);
              }
            }}
            locale={{ emptyText: selectedOrgId ? '该组织下暂无人员' : '请先选择左侧组织节点' }}
          />
        </Card>

        <Modal
          title={modalMode === 'add' ? '新增组织用户' : '编辑组织用户'}
          open={isModalOpen}
          onCancel={() => setIsModalOpen(false)}
          onOk={handleSaveOrgUser}
          okText={modalMode === 'add' ? '保存' : '更新'}
          destroyOnHidden
        >
          <Form form={form} layout="vertical" preserve={false}
            initialValues={{
              userId: selectedOrgUser?.username || '',
              status: selectedOrgUser?.status ?? 1
            }}>
            <Form.Item label="用户名" name="userId" rules={[{ required: true, message: '请输入用户名' }]}>
              <Input placeholder="请输入用户名" prefix={<UserOutlined />} disabled={modalMode === 'edit'} />
            </Form.Item>
            <Form.Item label="所属组织">
              <Input value={`ID ${selectedOrgId || '-'}`} disabled />
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
