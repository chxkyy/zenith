import React, { useState, useEffect } from 'react';
import { Table, Button, Tag, Space, App, Tabs, Modal, Input, Select, Popconfirm } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  EyeOutlined,
  SearchOutlined,
  UndoOutlined,
  CloseOutlined,
  RedoOutlined,
} from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { useNavigate } from 'react-router-dom';
import Notification from './Notification';

interface ProcessInstance {
  id: number;
  processNo: string;
  processTemplateName: string;
  title: string;
  status: number;
  statusName: string;
  initiatorName: string;
  currentNodeOrder: number;
  currentNodeName: string;
  createdTime: number;
}

export default function MyProcess() {
  const { message } = App.useApp();
  const navigate = useNavigate();
  const [processes, setProcesses] = useState<ProcessInstance[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useState({
    status: null as number | null,
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalCount, setTotalCount] = useState(0);
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' | 'info'; key: number } | null>(null);

  const [resubmitModalOpen, setResubmitModalOpen] = useState(false);
  const [currentProcess, setCurrentProcess] = useState<ProcessInstance | null>(null);

  const fetchProcesses = async () => {
    setLoading(true);
    try {
      const query = {
        pageIndex: currentPage,
        pageSize: pageSize,
        status: searchParams.status,
      };

      const response = await fetch('/api/workflow/process-instances/my/page', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(query),
      });
      const data = await response.json();
      if (data.success && data.data) {
        setProcesses(data.data);
        setTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      console.error('获取我的申请列表失败:', error);
      setNotification({ message: '获取我的申请列表失败', type: 'error', key: Date.now() });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProcesses();
  }, [currentPage, pageSize]);

  const handleSearch = () => {
    setCurrentPage(1);
    fetchProcesses();
  };

  const handleViewDetail = (process: ProcessInstance) => {
    navigate(`/workflow/detail?id=${process.id}`);
  };

  const handleRevoke = async (process: ProcessInstance) => {
    try {
      const response = await fetch('/api/workflow/process-instances/revoke', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: process.id }),
      });
      const data = await response.json();
      if (data.success) {
        fetchProcesses();
        setNotification({ message: '撤销成功', type: 'success', key: Date.now() });
      } else {
        setNotification({ message: data.errMessage || '撤销失败', type: 'error', key: Date.now() });
      }
    } catch (error) {
      setNotification({ message: '撤销失败，请重试', type: 'error', key: Date.now() });
    }
  };

  const handleCancel = async (process: ProcessInstance) => {
    try {
      const response = await fetch('/api/workflow/process-instances/cancel', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id: process.id }),
      });
      const data = await response.json();
      if (data.success) {
        fetchProcesses();
        setNotification({ message: '取消成功', type: 'success', key: Date.now() });
      } else {
        setNotification({ message: data.errMessage || '取消失败', type: 'error', key: Date.now() });
      }
    } catch (error) {
      setNotification({ message: '取消失败，请重试', type: 'error', key: Date.now() });
    }
  };

  const handleResubmit = async () => {
    if (!currentProcess) return;
    
    try {
      const response = await fetch('/api/workflow/process-instances/resubmit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ processTemplateId: currentProcess.id }),
      });
      const data = await response.json();
      if (data.success) {
        setResubmitModalOpen(false);
        fetchProcesses();
        setNotification({ message: '重新提交成功', type: 'success', key: Date.now() });
      } else {
        setNotification({ message: data.errMessage || '重新提交失败', type: 'error', key: Date.now() });
      }
    } catch (error) {
      setNotification({ message: '重新提交失败，请重试', type: 'error', key: Date.now() });
    }
  };

  const statusColorMap: Record<number, string> = {
    0: 'default',
    1: 'processing',
    2: 'success',
    3: 'warning',
    4: 'error',
    5: 'default',
  };

  const columns: ColumnsType<ProcessInstance> = [
    {
      title: '流程编号',
      dataIndex: 'processNo',
      key: 'processNo',
      width: 150,
    },
    {
      title: '流程类型',
      dataIndex: 'processTemplateName',
      key: 'processTemplateName',
      width: 120,
    },
    {
      title: '申请标题',
      dataIndex: 'title',
      key: 'title',
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number, record: ProcessInstance) => (
        <Tag color={statusColorMap[status] || 'default'}>{record.statusName}</Tag>
      ),
    },
    {
      title: '当前节点',
      dataIndex: 'currentNodeName',
      key: 'currentNodeName',
      width: 120,
      render: (name: string) => name || '-',
    },
    {
      title: '发起时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 170,
      render: (val: number) => formatDateTime(val),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: unknown, record: ProcessInstance) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          {record.status === 1 && (
            <Popconfirm
              title="确定要撤销该申请吗？"
              onConfirm={() => handleRevoke(record)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" size="small" icon={<UndoOutlined />}>
                撤销
              </Button>
            </Popconfirm>
          )}
          {record.status === 4 && (
            <>
              <Button type="link" size="small" icon={<RedoOutlined />} onClick={() => { setCurrentProcess(record); setResubmitModalOpen(true); }}>
                重新提交
              </Button>
              <Popconfirm
                title="确定要取消该申请吗？"
                onConfirm={() => handleCancel(record)}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link" size="small" danger icon={<CloseOutlined />}>
                  取消
                </Button>
              </Popconfirm>
            </>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 16 }}>
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>我的申请</h2>
        <p style={{ color: '#64748b', marginTop: 4 }}>查看和管理我发起的流程申请。</p>
      </div>

      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
        <Select
          value={searchParams.status}
          onChange={(val) => setSearchParams((prev) => ({ ...prev, status: val }))}
          placeholder="状态筛选"
          allowClear
          style={{ width: 150 }}
          options={[
            { value: 0, label: '草稿' },
            { value: 1, label: '审批中' },
            { value: 2, label: '已通过' },
            { value: 3, label: '已撤销' },
            { value: 4, label: '已退回' },
            { value: 5, label: '已取消' },
          ]}
        />
        <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
          查询
        </Button>
      </div>

      <Table<ProcessInstance>
        columns={columns}
        dataSource={processes}
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
          size: 'default',
          onChange: (page, size) => {
            setCurrentPage(page);
            setPageSize(size);
          },
        }}
      />

      <Modal
        title="重新提交"
        open={resubmitModalOpen}
        onOk={handleResubmit}
        onCancel={() => setResubmitModalOpen(false)}
        okText="确定"
        cancelText="取消"
        destroyOnHidden
      >
        <p>确定要重新提交该申请吗？</p>
      </Modal>

      {notification && (
        <Notification key={notification.key} message={notification.message} type={notification.type} />
      )}
    </div>
  );
}
