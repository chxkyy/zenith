import React, { useState, useEffect } from 'react';
import { Card, Descriptions, Tag, Steps, Table, Button, Space, App, Spin } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  MinusCircleOutlined,
  SyncOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { useSearchParams, useNavigate } from 'react-router-dom';
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
  formData: string;
  nodes: NodeProgress[];
  approvalRecords: ApprovalRecord[];
}

interface NodeProgress {
  nodeOrder: number;
  nodeName: string;
  status: string;
  assigneeNames: string;
}

interface ApprovalRecord {
  id: number;
  nodeOrder: number;
  nodeName: string;
  operatorName: string;
  actionType: number;
  actionName: string;
  opinion: string;
  operateTime: number;
}

export default function ProcessDetail() {
  const { message } = App.useApp();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const processId = searchParams.get('id');
  
  const [process, setProcess] = useState<ProcessInstance | null>(null);
  const [loading, setLoading] = useState(true);
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' | 'info'; key: number } | null>(null);

  useEffect(() => {
    if (processId) {
      fetchProcessDetail();
    }
  }, [processId]);

  const fetchProcessDetail = async () => {
    setLoading(true);
    try {
      const response = await fetch(`/api/workflow/process-instances/detail?id=${processId}`);
      const data = await response.json();
      if (data.success && data.data) {
        setProcess(data.data);
      } else {
        setNotification({ message: data.errMessage || '获取流程详情失败', type: 'error', key: Date.now() });
      }
    } catch (error) {
      console.error('获取流程详情失败:', error);
      setNotification({ message: '获取流程详情失败', type: 'error', key: Date.now() });
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'completed':
        return <CheckCircleOutlined style={{ color: '#52c41a' }} />;
      case 'current':
        return <SyncOutlined spin style={{ color: '#1890ff' }} />;
      case 'pending':
        return <ClockCircleOutlined style={{ color: '#999' }} />;
      default:
        return <MinusCircleOutlined />;
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

  const actionColorMap: Record<number, string> = {
    1: 'blue',
    2: 'green',
    3: 'orange',
    4: 'purple',
    5: 'cyan',
    6: 'warning',
    7: 'blue',
    8: 'default',
  };

  const recordColumns: ColumnsType<ApprovalRecord> = [
    {
      title: '节点',
      dataIndex: 'nodeName',
      key: 'nodeName',
      width: 150,
    },
    {
      title: '操作人',
      dataIndex: 'operatorName',
      key: 'operatorName',
      width: 100,
    },
    {
      title: '操作',
      dataIndex: 'actionName',
      key: 'actionName',
      width: 100,
      render: (name: string, record: ApprovalRecord) => (
        <Tag color={actionColorMap[record.actionType] || 'default'}>{name}</Tag>
      ),
    },
    {
      title: '意见',
      dataIndex: 'opinion',
      key: 'opinion',
      ellipsis: true,
      render: (text: string) => text || '-',
    },
    {
      title: '时间',
      dataIndex: 'operateTime',
      key: 'operateTime',
      width: 170,
      render: (val: number) => formatDateTime(val),
    },
  ];

  if (loading) {
    return (
      <div style={{ padding: 24, textAlign: 'center' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!process) {
    return (
      <div style={{ padding: 24, textAlign: 'center' }}>
        <p>流程不存在或已被删除</p>
        <Button onClick={() => navigate(-1)}>返回</Button>
      </div>
    );
  }

  let formDataObj: Record<string, any> = {};
  try {
    formDataObj = JSON.parse(process.formData || '{}');
  } catch (e) {}

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: 20, fontWeight: 700, margin: 0 }}>{process.title}</h2>
          <p style={{ color: '#64748b', marginTop: 4 }}>流程编号：{process.processNo}</p>
        </div>
        <Button onClick={() => navigate(-1)}>返回</Button>
      </div>

      <Card title="基本信息" style={{ marginBottom: 16 }}>
        <Descriptions column={4}>
          <Descriptions.Item label="流程类型">{process.processTemplateName}</Descriptions.Item>
          <Descriptions.Item label="发起人">{process.initiatorName}</Descriptions.Item>
          <Descriptions.Item label="状态">
            <Tag color={statusColorMap[process.status]}>{process.statusName}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="发起时间">{formatDateTime(process.createdTime)}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="流程进度" style={{ marginBottom: 16 }}>
        <Steps
          current={process.nodes?.findIndex(n => n.status === 'current') || -1}
          items={process.nodes?.map(node => ({
            title: node.nodeName,
            description: node.assigneeNames ? `审批人：${node.assigneeNames}` : '',
            status: node.status === 'completed' ? 'finish' : 
                   node.status === 'current' ? 'process' : 'wait',
            icon: getStatusIcon(node.status),
          })) || []}
        />
      </Card>

      <Card title="申请内容" style={{ marginBottom: 16 }}>
        <Descriptions column={2}>
          {Object.entries(formDataObj).map(([key, value]) => (
            <Descriptions.Item key={key} label={key}>
              {typeof value === 'object' ? JSON.stringify(value) : String(value)}
            </Descriptions.Item>
          ))}
        </Descriptions>
      </Card>

      <Card title="审批记录">
        <Table<ApprovalRecord>
          columns={recordColumns}
          dataSource={process.approvalRecords || []}
          rowKey="id"
          size="small"
          pagination={false}
        />
      </Card>

      {notification && (
        <Notification key={notification.key} message={notification.message} type={notification.type} />
      )}
    </div>
  );
}
