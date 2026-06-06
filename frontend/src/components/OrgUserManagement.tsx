import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Table, Button, Space, Tag, Popconfirm, App, Card, Empty, Spin, Input, Modal, Form, Layout, Tree, Select } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined, UndoOutlined, UserOutlined, ApartmentOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { usePermission } from '../lib/PermissionContext';
import { usePaginatedQuery, useCrudModal, useCrudOperations, createAuditColumns } from '../lib/useCrudTable';
import { post, del } from '../lib/apiClient';

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
  const { hasPermission } = usePermission();

  // ─── Org tree state (not paginated — loaded once) ────────────
  const [orgs, setOrgs] = useState<Org[]>([]);
  const [treeData, setTreeData] = useState<TreeNodeData[]>([]);
  const [treeLoading, setTreeLoading] = useState(true);
  const [selectedOrgKey, setSelectedOrgKey] = useState<string>('');
  const [selectedOrgId, setSelectedOrgId] = useState<number | null>(null);
  const hasFetchedTree = useRef(false);

  // ─── User table: delegated to usePaginatedQuery ──────────────
  const {
    data: orgUsers,
    loading,
    currentPage,
    pageSize,
    totalCount,
    keyword: searchKeyword,
    setKeyword: setSearchKeyword,
    goToPage,
    refresh: refreshUsers,
    refetchWithQuery,
  } = usePaginatedQuery<OrgUser>({
    apiUrl: '/api/users/page',
    initialPageSize: 20,
    autoFetch: false,
  });

  // ─── Modal & CRUD via hooks ─────────────────────────────────
  const { modalOpen, modalMode, editingRecord, openAddModal, openEditModal, closeModal } =
    useCrudModal<OrgUser>();

  const { create, update, remove: deleteUser, submitting } = useCrudOperations<OrgUser>(
    {
      createUrl: '/api/users',
      updateUrl: '/api/users/update',
      deleteUrl: '/api/users/delete',
      beforeCreate: (values) => ({
        username: values.userId,
        nickname: values.nickname || values.userId,
        orgId: selectedOrgId ?? 0,
        status: values.status ?? 1,
      }),
      beforeUpdate: (values) => ({
        id: editingRecord?.id,
        status: values.status,
      }),
      successMessages: { create: '新增成功', update: '编辑成功', delete: '删除成功' },
    },
    { refresh: refreshUsers },
  );

  const [form] = Form.useForm();

  // ─── Fetch org tree (once on mount) ─────────────────────────
  const fetchOrgs = useCallback(async () => {
    setTreeLoading(true);
    try {
      const data = await post<any[]>('/api/orgs/page', { pageIndex: 1, pageSize: 1000 });
      const orgMap = new Map<number, Org>();
      (data || []).forEach((orgDTO: any) => {
        orgMap.set(orgDTO.id, {
          id: orgDTO.id,
          name: orgDTO.name,
          parentId: orgDTO.parentId,
          sort: orgDTO.sort || 0,
          status: orgDTO.status ?? 1,
          children: [],
        });
      });
      const rootOrgs: Org[] = [];
      orgMap.forEach((org) => {
        if (!org.parentId) {
          rootOrgs.push(org);
        } else {
          const parent = orgMap.get(org.parentId);
          if (parent) parent.children?.push(org);
        }
      });
      setOrgs(rootOrgs);

      const convertToTreeData = (orgList: Org[]): TreeNodeData[] => {
        return orgList.map((org) => ({
          key: `org-${org.id}`,
          title: <span>{org.name}</span>,
          orgId: org.id,
          children: org.children?.length ? convertToTreeData(org.children) : undefined,
        }));
      };
      setTreeData(convertToTreeData(rootOrgs));

      if (rootOrgs.length > 0 && !selectedOrgKey) {
        const firstKey = `org-${rootOrgs[0].id}`;
        const firstId = rootOrgs[0].id;
        setSelectedOrgKey(firstKey);
        setSelectedOrgId(firstId);
        refetchWithQuery({ orgId: firstId });
      }
    } catch (error) {
      console.error('Error fetching organizations:', error);
    } finally {
      setTreeLoading(false);
    }
  }, [selectedOrgKey, refetchWithQuery]);

  useEffect(() => {
    if (hasFetchedTree.current) return;
    hasFetchedTree.current = true;
    fetchOrgs();
  }, [fetchOrgs]);

  // ─── Event handlers ─────────────────────────────────────────
  const handleSelectNode = (_keys: React.Key[], info: any) => {
    const key = info.node.key as string;
    setSelectedOrgKey(key);
    const orgIdStr = key.replace('org-', '');
    const orgId = orgIdStr ? Number(orgIdStr) : null;
    setSelectedOrgId(orgId);
    setSearchKeyword('');
    if (orgId) {
      refetchWithQuery({ orgId });
    }
  };

  const handleSearch = () => {
    goToPage(1);
    refetchWithQuery({ ...(selectedOrgId ? { orgId: selectedOrgId } : {}) });
  };

  const handleClearAndRefresh = () => {
    setSearchKeyword('');
    goToPage(1);
    refetchWithQuery({ ...(selectedOrgId ? { orgId: selectedOrgId } : {}) });
  };

  const handleSaveOrgUser = async () => {
    try {
      const values = await form.validateFields();
      if (modalMode === 'add') {
        await create(values);
        closeModal();
      } else {
        await update(values, editingRecord!);
        closeModal();
      }
    } catch {
      // useCrudOperations already handles message.error
    }
  };

  // ─── Column definitions ─────────────────────────────────────
  const columns: ColumnsType<OrgUser> = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: '用户名', dataIndex: 'username', key: 'username', width: 100 },
    { title: '昵称', dataIndex: 'nickname', key: 'nickname', width: 100 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (v) => <Tag color={v === 1 ? 'success' : 'default'}>{v === 1 ? '启用' : '禁用'}</Tag>,
    },
    ...createAuditColumns<OrgUser>(),
    {
      title: '操作', key: 'action', width: 120, fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          {hasPermission('sys:user:edit') && (
            <Button type="link" size="small" icon={<EditOutlined />}
              onClick={() => openEditModal(record)}>
              编辑
            </Button>
          )}
          {hasPermission('sys:user:delete') && (
            <Popconfirm title="确定删除该组织用户关系吗？" onConfirm={() => deleteUser(record.id)} okText="确定" cancelText="取消">
              <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  // ─── Render ─────────────────────────────────────────────────
  return (
    <Layout style={{ height: 'calc(100vh - 48px)' }}>
      <Sider
        width={240}
        style={{
          background: '#fff',
          borderRight: '1px solid #f0f0f0',
          overflow: 'auto',
        }}
      >
        <div style={{ padding: '12px 16px', borderBottom: '1px solid #f0f0f0', fontWeight: 600, fontSize: 14 }}>
          组织架构
        </div>
        <div style={{ padding: 8 }}>
          {treeLoading ? (
            <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}><Spin /></div>
          ) : treeData.length > 0 ? (
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
              onPressEnter={handleSearch}
              allowClear
              onClear={handleClearAndRefresh}
              style={{ width: 200 }}
            />
            <Button type="primary" size="small" icon={<SearchOutlined />} onClick={handleSearch}>
              查询
            </Button>
            <Button size="small" icon={<UndoOutlined />} onClick={handleClearAndRefresh}>
              重置
            </Button>
          </Space>
        </Card>

        <Card
          size="small"
          title={`${selectedOrgId ? `当前组织（ID:${selectedOrgId}）` : '请选择组织'} - 人员列表`}
          extra={
            <Button type="primary" icon={<PlusOutlined />} disabled={!selectedOrgId}
              onClick={openAddModal} style={{ display: hasPermission('sys:user:add') ? undefined : 'none' }}>
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
              onChange: goToPage,
            }}
            locale={{ emptyText: selectedOrgId ? '该组织下暂无人员' : '请先选择左侧组织节点' }}
          />
        </Card>

        <Modal
          title={modalMode === 'add' ? '新增组织用户' : '编辑组织用户'}
          open={modalOpen}
          onCancel={closeModal}
          onOk={handleSaveOrgUser}
          okText={modalMode === 'add' ? '保存' : '更新'}
          confirmLoading={submitting}
          destroyOnHidden
        >
          <Form form={form} layout="vertical" preserve={false}
            initialValues={{
              userId: editingRecord?.username || '',
              status: editingRecord?.status ?? 1,
            }}
          >
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
