import React, { useState, useEffect } from 'react';
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
  const [notices, setNotices] = useState<Notice[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useState({
    keyword: '',
    type: '',
    status: '',
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);

  // 新增/编辑模态框
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [formModalType, setFormModalType] = useState<'add' | 'edit'>('add');
  const [form] = Form.useForm();

  // 查看详情模态框
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [currentNotice, setCurrentNotice] = useState<Notice | null>(null);

  // 从后端获取通知数据
  const fetchNotices = async () => {
    setLoading(true);
    try {
      const query = {
        pageIndex: currentPage,
        pageSize: pageSize,
        keyword: searchParams.keyword,
        type: searchParams.type,
        status: searchParams.status,
      };

      const response = await fetch('/api/notices/page', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(query),
      });
      if (!response.ok) {
        throw new Error('Failed to fetch notices');
      }
      const data = await response.json();
      if (data.success && data.data) {
        const formattedNotices = data.data.map((notice: any) => ({
          id: notice.id,
          title: notice.title,
          type: notice.type,
          author: notice.author,
          time: formatDateTime(notice.createdTime || notice.createdAt || notice.time),
          status: notice.status,
          statusName: notice.statusName,
          isPinned: notice.isPinned || false,
          readCount: notice.readCount || 0,
          content: notice.content,
          remark: notice.remark,
          createUserId: notice.createUserId,
          updateUserId: notice.updateUserId,
          createdTime: notice.createdTime,
          updateTime: notice.updateTime,
          createUserName: notice.createUserName,
          updateUserName: notice.updateUserName,
        }));
        setNotices(formattedNotices);
        setTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      console.error('获取通知列表失败:', error);
      message.error('获取通知列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotices();
  }, [currentPage, pageSize]);

  // 手动触发搜索
  const handleSearch = () => {
    setCurrentPage(1);
    fetchNotices();
  };

  // 打开新增公告模态框
  const handleAddNotice = () => {
    setFormModalType('add');
    form.resetFields();
    form.setFieldsValue({
      type: 'system',
      author: 'admin',
      status: '0',
    });
    setFormModalOpen(true);
  };

  // 打开编辑公告模态框
  const handleEditNotice = (notice: Notice) => {
    setFormModalType('edit');
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
    setFormModalOpen(true);
  };

  // 打开查看详情模态框
  const handleViewNotice = (notice: Notice) => {
    setCurrentNotice(notice);
    setViewModalOpen(true);
  };

  // 保存公告
  const handleSaveNotice = async () => {
    try {
      const values = await form.validateFields();
      const formData: NoticeForm = {
        id: formModalType === 'edit' ? values.id : null,
        ...values,
      };

      const response = await fetch('/api/notices', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        throw new Error('Failed to save notice');
      }

      const data = await response.json();
      if (data.success) {
        setFormModalOpen(false);
        fetchNotices();
        message.success(formModalType === 'edit' ? '公告编辑成功' : '公告新增成功');
      } else {
        message.error(data.errMessage || '保存失败');
      }
    } catch (error) {
      if (error instanceof Error) {
        console.error('保存公告失败:', error);
        message.error('保存失败，请重试');
      }
    }
  };

  // 删除公告
  const handleDeleteNotice = async (notice: Notice) => {
    try {
      const response = await fetch('/api/notices/delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: notice.id }),
      });

      if (!response.ok) {
        throw new Error('Failed to delete notice');
      }

      const data = await response.json();
      if (data.success) {
        fetchNotices();
        message.success('公告删除成功');
      } else {
        message.error(data.errMessage || '删除失败');
      }
    } catch (error) {
      console.error('删除公告失败:', error);
      message.error('删除失败，请重试');
    }
  };

  // 发布/撤回公告
  const handlePublishNotice = async (notice: Notice) => {
    try {
      const newStatus = notice.status === '1' ? '0' : '1';
      const response = await fetch('/api/notices/status', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: notice.id, status: newStatus }),
      });

      if (!response.ok) {
        throw new Error('Failed to update notice status');
      }

      const data = await response.json();
      if (data.success) {
        fetchNotices();
        message.success(notice.status === '1' ? '公告已撤回' : '公告已发布');
      } else {
        message.error(data.errMessage || '操作失败');
      }
    } catch (error) {
      console.error('更新公告状态失败:', error);
      message.error('操作失败，请重试');
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
    {
      title: '发布人',
      dataIndex: 'author',
      key: 'author',
      width: 90,
    },
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
    {
      title: '阅读人数',
      dataIndex: 'readCount',
      key: 'readCount',
      width: 90,
      align: 'center',
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
      width: 260,
      render: (_: unknown, record: Notice) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewNotice(record)}>
            查看
          </Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEditNotice(record)}>
            编辑
          </Button>
          {record.status === '1' ? (
            <Popconfirm
              title="确定要撤回该公告吗？"
              onConfirm={() => handlePublishNotice(record)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" size="small" icon={<RollbackOutlined />}>
                撤回
              </Button>
            </Popconfirm>
          ) : (
            <Popconfirm
              title="确定要发布该公告吗？"
              onConfirm={() => handlePublishNotice(record)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" size="small" icon={<SendOutlined />} style={{ color: '#52c41a' }}>
                发布
              </Button>
            </Popconfirm>
          )}
          <Popconfirm
            title="删除后数据不可恢复，是否确认删除？"
            icon={<ExclamationCircleOutlined style={{ color: '#faad14' }} />}
            onConfirm={() => handleDeleteNotice(record)}
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
          <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>通知公告</h2>
          <p style={{ color: '#64748b', marginTop: 4 }}>管理系统内的通知、公告及重要规则发布。</p>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAddNotice}>
          新增公告
        </Button>
      </div>

      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
        <Input.Search
          placeholder="搜索公告标题..."
          value={searchParams.keyword}
          onChange={(e) => setSearchParams((prev) => ({ ...prev, keyword: e.target.value }))}
          onSearch={handleSearch}
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
        <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
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
          showTotal: (total) => `共 ${total} 条记录`,
          onChange: (page, size) => {
            setCurrentPage(page);
            setPageSize(size);
          },
          showSizeChanger: true,
        }}
      />

      {/* 新增/编辑模态框 */}
      <Modal
        title={formModalType === 'add' ? '新增公告' : '编辑公告'}
        open={formModalOpen}
        onOk={handleSaveNotice}
        onCancel={() => setFormModalOpen(false)}
        okText="保存"
        cancelText="取消"
        width={640}
        destroyOnClose
      >
        <Form form={form} layout="vertical" preserve={false}>
          {formModalType === 'edit' && (
            <Form.Item name="id" hidden>
              <Input />
            </Form.Item>
          )}
          <Form.Item
            name="title"
            label="公告标题"
            rules={[{ required: true, message: '请输入公告标题' }]}
          >
            <Input placeholder="请输入公告标题" />
          </Form.Item>
          <Form.Item
            name="type"
            label="公告类型"
            rules={[{ required: true, message: '请选择公告类型' }]}
          >
            <Select
              options={[
                { value: 'system', label: '系统通知' },
                { value: 'business', label: '业务公告' },
                { value: 'rule', label: '规则通知' },
              ]}
            />
          </Form.Item>
          <Form.Item
            name="author"
            label="发布人"
            rules={[{ required: true, message: '请输入发布人' }]}
          >
            <Input placeholder="请输入发布人" />
          </Form.Item>
          <Form.Item
            name="content"
            label="公告内容"
            rules={[{ required: true, message: '请输入公告内容' }]}
          >
            <Input.TextArea rows={6} placeholder="请输入公告内容" />
          </Form.Item>
          <Form.Item
            name="status"
            label="状态"
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select
              options={[
                { value: '0', label: '草稿' },
                { value: '1', label: '已发布' },
              ]}
            />
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
        destroyOnClose
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
