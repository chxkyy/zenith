import React, { useState, useEffect } from 'react';
import { Table, Button, Tag, Space, App, Tabs, Modal, Input, Select, Popconfirm } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  CheckOutlined,
  CloseOutlined,
  TeamOutlined,
  StopOutlined,
  SearchOutlined,
  EyeOutlined,
} from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { useNavigate } from 'react-router-dom';
import Notification from './Notification';

interface Task {
  id: number;
  processInstanceId: number;
  processNo: string;
  processTemplateName: string;
  title: string;
  nodeOrder: number;
  nodeName: string;
  nodeType: number;
  nodeTypeName: string;
  initiatorId: number;
  initiatorName: string;
  createdTime: number;
}

export default function TaskList() {
  const { message } = App.useApp();
  const navigate = useNavigate();
  const [todoTasks, setTodoTasks] = useState<Task[]>([]);
  const [doneTasks, setDoneTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('todo');
  const [searchParams, setSearchParams] = useState({
    processTemplateName: '',
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [totalCount, setTotalCount] = useState(0);
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' | 'info'; key: number } | null>(null);

  const [actionModalOpen, setActionModalOpen] = useState(false);
  const [actionType, setActionType] = useState<'approve' | 'reject' | 'countersign' | 'terminate'>('approve');
  const [currentTask, setCurrentTask] = useState<Task | null>(null);
  const [opinion, setOpinion] = useState('');
  const [countersignType, setCountersignType] = useState(1);
  const [countersignIds, setCountersignIds] = useState('');

  const fetchTasks = async () => {
    setLoading(true);
    try {
      const query = {
        pageIndex: currentPage,
        pageSize: pageSize,
        processTemplateName: searchParams.processTemplateName || undefined,
      };

      const url = activeTab === 'todo' 
        ? '/api/workflow/tasks/todo/page' 
        : '/api/workflow/tasks/done/page';

      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(query),
      });
      const data = await response.json();
      if (data.success && data.data) {
        if (activeTab === 'todo') {
          setTodoTasks(data.data);
        } else {
          setDoneTasks(data.data);
        }
        setTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      console.error('获取任务列表失败:', error);
      setNotification({ message: '获取任务列表失败', type: 'error', key: Date.now() });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
  }, [activeTab, currentPage, pageSize]);

  const handleSearch = () => {
    setCurrentPage(1);
    fetchTasks();
  };

  const handleOpenAction = (task: Task, type: 'approve' | 'reject' | 'countersign' | 'terminate') => {
    setCurrentTask(task);
    setActionType(type);
    setOpinion('');
    setCountersignIds('');
    setCountersignType(1);
    setActionModalOpen(true);
  };

  const handleExecuteAction = async () => {
    if (!currentTask) return;

    try {
      let url = '';
      let body: any = { taskId: currentTask.id, opinion };

      switch (actionType) {
        case 'approve':
          url = '/api/workflow/tasks/approve';
          break;
        case 'reject':
          url = '/api/workflow/tasks/reject';
          break;
        case 'countersign':
          url = '/api/workflow/tasks/countersign';
          body = {
            taskId: currentTask.id,
            approverType: countersignType,
            approverIds: JSON.parse(countersignIds || '[]'),
            opinion,
          };
          break;
        case 'terminate':
          url = '/api/workflow/tasks/terminate';
          break;
      }

      const response = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });

      const data = await response.json();
      if (data.success) {
        setActionModalOpen(false);
        fetchTasks();
        const actionNames: Record<string, string> = {
          approve: '审批通过',
          reject: '退回',
          countersign: '加签',
          terminate: '提前终止',
        };
        setNotification({ message: `${actionNames[actionType]}成功`, type: 'success', key: Date.now() });
      } else {
        setNotification({ message: data.errMessage || '操作失败', type: 'error', key: Date.now() });
      }
    } catch (error) {
      setNotification({ message: '操作失败，请重试', type: 'error', key: Date.now() });
    }
  };

  const handleViewDetail = (task: Task) => {
    navigate(`/workflow/detail?id=${task.processInstanceId}`);
  };

  const columns: ColumnsType<Task> = [
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
      title: '当前节点',
      dataIndex: 'nodeName',
      key: 'nodeName',
      width: 120,
      render: (name: string, record: Task) => (
        <Space>
          <span>{name}</span>
          {record.nodeType === 2 && <Tag color="blue">会签</Tag>}
        </Space>
      ),
    },
    {
      title: '发起人',
      dataIndex: 'initiatorName',
      key: 'initiatorName',
      width: 100,
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
      width: activeTab === 'todo' ? 280 : 100,
      render: (_: unknown, record: Task) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          {activeTab === 'todo' && (
            <>
              <Popconfirm
                title="确定通过该申请吗？"
                onConfirm={() => handleOpenAction(record, 'approve')}
                okText="确定"
                cancelText="取消"
              >
                <Button type="link" size="small" icon={<CheckOutlined />} style={{ color: '#52c41a' }}>
                  通过
                </Button>
              </Popconfirm>
              <Button type="link" size="small" icon={<CloseOutlined />} danger onClick={() => handleOpenAction(record, 'reject')}>
                退回
              </Button>
              <Button type="link" size="small" icon={<TeamOutlined />} onClick={() => handleOpenAction(record, 'countersign')}>
                加签
              </Button>
              <Button type="link" size="small" icon={<StopOutlined />} onClick={() => handleOpenAction(record, 'terminate')}>
                终止
              </Button>
            </>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 16 }}>
        <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>工作流审批</h2>
        <p style={{ color: '#64748b', marginTop: 4 }}>处理待办任务，查看已办记录。</p>
      </div>

      <Tabs
        activeKey={activeTab}
        onChange={(key) => {
          setActiveTab(key);
          setCurrentPage(1);
        }}
        items={[
          { key: 'todo', label: '待办任务' },
          { key: 'done', label: '已办任务' },
        ]}
      />

      <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
        <Input
          placeholder="搜索流程名称..."
          value={searchParams.processTemplateName}
          onChange={(e) => setSearchParams((prev) => ({ ...prev, processTemplateName: e.target.value }))}
          onPressEnter={handleSearch}
          allowClear
          style={{ width: 200 }}
        />
        <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
          查询
        </Button>
      </div>

      <Table<Task>
        columns={columns}
        dataSource={activeTab === 'todo' ? todoTasks : doneTasks}
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
        title={
          actionType === 'approve' ? '审批通过' :
          actionType === 'reject' ? '退回申请' :
          actionType === 'countersign' ? '加签' : '提前终止'
        }
        open={actionModalOpen}
        onOk={handleExecuteAction}
        onCancel={() => setActionModalOpen(false)}
        okText="确定"
        cancelText="取消"
        destroyOnHidden
      >
        {actionType === 'countersign' && (
          <div style={{ marginBottom: 16 }}>
            <div style={{ marginBottom: 8 }}>加签类型</div>
            <Select
              value={countersignType}
              onChange={setCountersignType}
              style={{ width: '100%' }}
              options={[
                { value: 1, label: '用户' },
                { value: 2, label: '角色' },
              ]}
            />
            <div style={{ marginTop: 8, marginBottom: 8 }}>加签对象ID（JSON数组）</div>
            <Input
              placeholder='如：[1, 2, 3]'
              value={countersignIds}
              onChange={(e) => setCountersignIds(e.target.value)}
            />
          </div>
        )}
        <div style={{ marginBottom: 8 }}>审批意见</div>
        <Input.TextArea
          rows={4}
          value={opinion}
          onChange={(e) => setOpinion(e.target.value)}
          placeholder="请输入审批意见"
        />
      </Modal>

      {notification && (
        <Notification key={notification.key} message={notification.message} type={notification.type} />
      )}
    </div>
  );
}
