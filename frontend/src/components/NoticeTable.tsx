import React, { useState } from 'react';
import { Table, Button, Tag, Modal, Form, Select, Input, Space, App, Popconfirm } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  PlusOutlined,
  EyeOutlined,
  EditOutlined,
  DeleteOutlined,
  SendOutlined,
  RollbackOutlined,
  PushpinOutlined,
  SearchOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { usePermission } from '../lib/PermissionContext';
import { usePaginatedQuery, useCrudModal } from '../lib/useCrudTable';
import { post, del } from '../lib/apiClient';

interface Notice {
  id: number;
  title: string;
  type: string;
  author: string;
  time: string;
  status: string;
  statusName: string;
  isPinned: boolean;
  readCount: number;
  content?: string;
  remark?: string;
  createUserId: number;
  updateUserId: number;
  createdTime: string;
  updateTime: string;
  createUserName?: string;
  updateUserName?: string;
}

interface NoticeForm {
  id: number | null;
  title: string;
  type: string;
  author: string;
  content: string;
  status: string;
  remark: string;
}

const typeMap: Record<string, string> = {
  system: '系统通知',
  business: '业务公告',
  rule: '规则通知',
};

const statusColorMap: Record<string, string> = {
  '1': 'green',
  '0': 'blue',
};

export default function NoticeTable() {
  const { message } = App.useApp();
  const { hasPermission } = usePermission();
  const [form] = Form.useForm();

  // 搜索参数（多字段）
  const [searchParams, setSearchParams] = useState({ keyword: '', type: '', status: '' });

  // 分页查询
  const {
    data: notices,
    loading,
    currentPage,
    pageSize,
    totalCount,
    goToPage,
    search: handleSearch,
    refresh,
  } = usePaginatedQuery<Notice>({
    apiUrl: '/api/notices/page',
    buildQuery: (page, size) => ({
      pageIndex: page,
      pageSize: size,
      keyword: searchParams.keyword || undefined,
      type: searchParams.type || undefined,
      status: searchParams.status || undefined,
    }),
    autoFetch: false,
  });

  // 首次加载 + 依赖搜索参数变化
  React.useEffect(() => {
    refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, pageSize]);

  const hasFetchedRef = React.useRef(false);
  React.useEffect(() => {
    if (hasFetchedRef.current) return;
    hasFetchedRef.current = true;
    refresh();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // 新增/编辑模态框
  const { modalOpen: formModalOpen, modalMode: formModalType, editingRecord, openAddModal, openEditModal, closeModal: closeFormModal } =
    useCrudModal<Notice>();

  // 查看详情模态框
  const [viewModalOpen, setViewModalOpen] = React.useState(false);
  const [currentNotice, setCurrentNotice] = React.useState<Notice | null>(null);

  const handleSearchClick = () => { goToPage(1); };

  const handleAddNotice = () => {
    openAddModal();
    form.resetFields();
    form.setFieldsValue({ type: 'system', author: 'admin', status: '0' });
  };

  const handleEditNotice = (notice: Notice) => {
    openEditModal(notice);
    form.resetFields();
    form.setFieldsValue({
      id: notice.id,
      title: notice.title,
      type: notice.type,
      author: notice.author,
      content: notice.content || '',
      status: notice.status,
      remark: notice.remark || '',
    });
  };

  const handleViewNotice = (notice: Notice) => {
    setCurrentNotice(notice);
    setViewModalOpen(true);
  };

  const handleSaveNotice = async () => {
    try {
      const values = await form.validateFields();
      const formData = {
        id: formModalType === 'edit' ? values.id : null,
        ...values,
      } as Record<string, unknown>;
      await post('/api/notices', formData);
      closeFormModal();
      message.success(formModalType === 'edit' ? '公告编辑成功' : '公告新增成功');
      refresh();
    } catch (err: unknown) {
      if (err instanceof Error && err.message !== 'Validate Failed') {
        message.error(err.message || '保存失败，请重试');
      }
    }
  };

  const handleDeleteNotice = async (notice: Notice) => {
    try {
      await del('/api/notices/delete', { id: notice.id });
      message.success('公告删除成功');
      refresh();
    } catch (err: unknown) {
      message.error(err instanceof Error ? err.message : '删除失败，请重试');
    }
  };

  const handlePublishNotice = async (notice: Notice) => {
    try {
      const newStatus = notice.status === '1' ? '0' : '1';
      await post('/api/notices/status', { id: notice.id, status: newStatus });
      message.success(notice.status === '1' ? '公告已撤回' : '公告已发布');
      refresh();
    } catch (err: unknown) {
      message.error(err instanceof Error ? err.message : '操作失败，请重试');
    }
  };

  const columns: ColumnsType<Notice> = [
    {
      title: '公告标题',
      dataIndex: 'title',
      key: 'title',
      ellipsis: true,
      render: (title: string, record: Notice) => (
        <Space>
          {record.isPinned && <PushpinOutlined style={{ color: '#fa8c16' }} />}
          <span style={{ fontWeight: 500 }}>{title}</span>
        </Space>
      ),
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (type: string) => <Tag>{typeMap[type] || type}</Tag>,
    },
    { title: '发布人', dataIndex: 'author', key: 'author', width: 90 },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      align: 'center',
      render: (status: string, record: Notice) => (
        <Tag color={statusColorMap[status] || 'default'}>{record.statusName}</Tag>
      ),
    },
    { title: '阅读人数', dataIndex: 'readCount', key: 'readCount', width: 90, align: 'center' },
    { title: '创建人', dataIndex: 'createUserName', key: 'createUserName', width: 90, render: (val: string) => val || '-' },
    { title: '创建时间', dataIndex: 'createdTime', key: 'createdTime', width: 170, render: (val: string) => formatDateTime(val) },
    { title: '修改人', dataIndex: 'updateUserName', key: 'updateUserName', width: 90, render: (val: string) => val || '-' },
    { title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', width: 170, render: (val: string) => formatDateTime(val) },
    {
      title: '操作',
      key: 'action',
      width: 260,
      render: (_: unknown, record: Notice) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewNotice(record)}>
            查看
          </Button>
          {hasPermission('notice:edit') && (
            <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditNotice(record)}>
              编辑
            </Button>
          )}
          {hasPermission('notice:publish') && (
            record.status === '1' ? (
              <Popconfirm title="确定要撤回该公告吗？" onConfirm={() => handlePublishNotice(record)} okText="确定" cancelText="取消">
                <Button type="link" size="small" icon={<RollbackOutlined />}>撤回</Button>
              </Popconfirm>
            ) : (
              <Popconfirm title="确定要发布该公告吗？" onConfirm={() => handlePublishNotice(record)} okText="确定" cancelText="取消">
                <Button type="link" size="small" icon={<SendOutlined />} style={{ color: '#52c41a' }}>发布</Button>
              </Popconfirm>
            )
          )}
          {hasPermission('notice:delete') && (
            <Popconfirm
              title="删除后数据不可恢复，是否确认删除？"
              icon={<ExclamationCircleOutlined style={{ color: '#faad14' }} />}
              onConfirm={() => handleDeleteNotice(record)}
              okText="确定"
              cancelText="取消"
              okButtonProps={{ danger: true }}
            >
              <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <div>
          <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>通知公告</h2>
          <p style={{ color: '#64748b', marginTop: 4 }}>管理系统内的通知、公告及重要规则发布。</p>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAddNotice}
          style={{ display: hasPermission('notice:add') ? undefined : 'none' }}>
          新增公告
        </Button>
      </div>

      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
        <Input.Search
          placeholder="搜索公告标题..."
          value={searchParams.keyword}
          onChange={(e) => setSearchParams((prev) => ({ ...prev, keyword: e.target.value }))}
          onSearch={handleSearchClick}
          allowClear
          style={{ width: 280 }}
        />
        <Select
          value={searchParams.type || undefined}
          onChange={(val) => setSearchParams((prev) => ({ ...prev, type: val || '' }))}
          placeholder="所有类型"
          allowClear
          style={{ width: 140 }}
          options={[
            { value: 'system', label: '系统通知' },
            { value: 'business', label: '业务公告' },
            { value: 'rule', label: '规则通知' },
          ]}
        />
        <Select
          value={searchParams.status || undefined}
          onChange={(val) => setSearchParams((prev) => ({ ...prev, status: val || '' }))}
          placeholder="所有状态"
          allowClear
          style={{ width: 140 }}
          options={[
            { value: '1', label: '已发布' },
            { value: '0', label: '草稿' },
          ]}
        />
        <Button type="primary" icon={<SearchOutlined />} onClick={handleSearchClick}>
          查询
        </Button>
      </div>

      <Table<Notice>
        columns={columns}
        dataSource={notices}
        rowKey="id"
        loading={loading}
        size="small"
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

      {/* 新增/编辑模态框 */}
      <Modal
        title={formModalType === 'add' ? '新增公告' : '编辑公告'}
        open={formModalOpen}
        onOk={handleSaveNotice}
        onCancel={closeFormModal}
        okText="保存"
        cancelText="取消"
        width={640}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" preserve={false}>
          {formModalType === 'edit' && (
            <Form.Item name="id" hidden><Input /></Form.Item>
          )}
          <Form.Item name="title" label="公告标题" rules={[{ required: true, message: '请输入公告标题' }]}>
            <Input placeholder="请输入公告标题" />
          </Form.Item>
          <Form.Item name="type" label="公告类型" rules={[{ required: true, message: '请选择公告类型' }]}>
            <Select options={[
              { value: 'system', label: '系统通知' },
              { value: 'business', label: '业务公告' },
              { value: 'rule', label: '规则通知' },
            ]} />
          </Form.Item>
          <Form.Item name="author" label="发布人" rules={[{ required: true, message: '请输入发布人' }]}>
            <Input placeholder="请输入发布人" />
          </Form.Item>
          <Form.Item name="content" label="公告内容" rules={[{ required: true, message: '请输入公告内容' }]}>
            <Input.TextArea rows={6} placeholder="请输入公告内容" />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true, message: '请选择状态' }]}>
            <Select options={[{ value: '0', label: '草稿' }, { value: '1', label: '已发布' }]} />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="查看详情"
        open={viewModalOpen}
        onCancel={() => setViewModalOpen(false)}
        footer={<Button type="primary" onClick={() => setViewModalOpen(false)}>关闭</Button>}
        width={640}
        destroyOnHidden
      >
        {currentNotice && (
          <div>
            <div style={{ borderBottom: '1px solid #f0f0f0', paddingBottom: 16, marginBottom: 16 }}>
              <h3 style={{ fontSize: 18, fontWeight: 700, margin: '0 0 8px 0' }}>{currentNotice.title}</h3>
              <Space size={16} style={{ color: '#64748b', fontSize: 13 }}>
                <span>类型：{typeMap[currentNotice.type] || currentNotice.type}</span>
                <span>发布人：{currentNotice.author}</span>
                <span>发布时间：{currentNotice.time}</span>
                <Tag color={statusColorMap[currentNotice.status] || 'default'}>{currentNotice.statusName}</Tag>
              </Space>
            </div>
            <div style={{ marginBottom: 16 }}>
              <h4 style={{ fontWeight: 600, marginBottom: 8 }}>公告内容</h4>
              <div style={{ padding: 16, background: '#f8fafc', border: '1px solid #f0f0f0', borderRadius: 6 }}>
                {currentNotice.content || '无内容'}
              </div>
            </div>
            {currentNotice.remark && (
              <div>
                <h4 style={{ fontWeight: 600, marginBottom: 8 }}>备注</h4>
                <div style={{ padding: 16, background: '#f8fafc', border: '1px solid #f0f0f0', borderRadius: 6 }}>
                  {currentNotice.remark}
                </div>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
}
