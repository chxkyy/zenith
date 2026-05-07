import React, { useState, useEffect, useRef } from 'react';
import { Table, Button, Input, Space, Tag, Popconfirm, App, Modal, Descriptions, Card } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { SearchOutlined, UndoOutlined, DeleteOutlined, EyeOutlined, ClearOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';

interface ErrorLog {
  id: number;
  module: string;
  ip: string;
  errorMsg: string;
  stackTrace: string;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

const LogError: React.FC = () => {
  const { message } = App.useApp();
  const [logs, setLogs] = useState<ErrorLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const hasFetchedLogs = useRef(false);
  const [searchParams, setSearchParams] = useState({ module: '', ip: '' });
  const [selectedLog, setSelectedLog] = useState<ErrorLog | null>(null);

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams();
      if (searchParams.module) params.append('module', searchParams.module);
      if (searchParams.ip) params.append('ip', searchParams.ip);

      const response = await fetch(`/api/error-logs?${params.toString()}`);
      const text = await response.text();
      if (response.status === 503) {
        throw new Error('Java 后端正在启动，请稍候...');
      }
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}. ${text}`);
      }
      if (!text) {
        setLogs([]);
        return;
      }
      try {
        const data = JSON.parse(text);
        if (data.success) {
          setLogs(data.data);
        } else {
          setError(data.errMessage || '获取日志失败');
        }
      } catch (e) {
        console.error('Failed to parse JSON:', text);
        throw new Error('服务器返回了非 JSON 格式的数据');
      }
    } catch (error: any) {
      console.error('Failed to fetch error logs:', error);
      setError(error.message || '网络错误');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (hasFetchedLogs.current) return;
    hasFetchedLogs.current = true;
    fetchLogs();
  }, []);

  const handleSearch = () => fetchLogs();
  const handleReset = () => {
    setSearchParams({ module: '', ip: '' });
  };

  const handleDelete = async (id: number) => {
    try {
      const response = await fetch(`/api/error-logs/delete`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id })
      });
      const data = await response.json();
      if (data.success) {
        message.success('删除成功');
        fetchLogs();
      }
    } catch (error) {
      console.error('Failed to delete log:', error);
      message.error('删除失败');
    }
  };

  const handleClear = async () => {
    try {
      const response = await fetch(`/api/error-logs/clear?months=3`, { method: 'POST' });
      const data = await response.json();
      if (data.success) {
        message.success('清理成功');
        fetchLogs();
      }
    } catch (error) {
      console.error('Failed to clear logs:', error);
      message.error('清理失败');
    }
  };

  const columns: ColumnsType<ErrorLog> = [
    { title: '日志ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '异常IP', dataIndex: 'ip', key: 'ip', width: 130, render: (v) => <code>{v}</code> },
    { title: '异常模块', dataIndex: 'module', key: 'module', width: 120, render: (v) => <Tag color="red">{v}</Tag> },
    {
      title: '异常信息', dataIndex: 'errorMsg', key: 'errorMsg', width: 200, ellipsis: true,
      render: (v) => <span style={{ color: '#dc2626', fontWeight: 500 }}>{v}</span>
    },
    { title: '创建人', dataIndex: 'createUserName', key: 'createUserName', width: 90, render: (v) => v || '-' },
    { title: '创建时间', dataIndex: 'createdTime', key: 'createdTime', width: 160, render: (v) => formatDateTime(v) },
    { title: '修改人', dataIndex: 'updateUserName', key: 'updateUserName', width: 90, render: (v) => v || '-' },
    { title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', width: 160, render: (v) => formatDateTime(v) },
    {
      title: '操作', key: 'action', width: 130, fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => setSelectedLog(record)}>详情</Button>
          <Popconfirm title="确定删除该异常日志吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
            <Button type="link" size="small" danger>删除</Button>
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
            <span style={{ fontSize: 14, color: '#666' }}>异常模块</span>
            <Input size="small" placeholder="请输入模块名称" value={searchParams.module}
              onChange={(e) => setSearchParams({ ...searchParams, module: e.target.value })}
              onPressEnter={handleSearch} style={{ width: 160 }} />
          </Space>
          <Space>
            <span style={{ fontSize: 14, color: '#666' }}>异常IP</span>
            <Input size="small" placeholder="请输入异常IP" value={searchParams.ip}
              onChange={(e) => setSearchParams({ ...searchParams, ip: e.target.value })}
              onPressEnter={handleSearch} style={{ width: 160 }} />
          </Space>
          <Button type="primary" size="small" icon={<SearchOutlined />} onClick={handleSearch}>查询</Button>
          <Button size="small" icon={<UndoOutlined />} onClick={handleReset}>重置</Button>
        </Space>
      </Card>

      <Card size="small" title="异常日志列表"
        extra={
          <Popconfirm title="确定清理3个月前的所有异常日志吗？" onConfirm={handleClear} okText="确定" cancelText="取消">
            <Button size="small" danger icon={<ClearOutlined />}>清理日志</Button>
          </Popconfirm>
        }
      >
        <Table<ErrorLog>
          columns={columns}
          dataSource={logs}
          rowKey="id"
          size="small"
          loading={loading}
          scroll={{ x: 1200 }}
          locale={{ emptyText: error || '暂无数据' }}
          pagination={false}
        />
      </Card>

      <Modal
        title="异常详情"
        open={!!selectedLog}
        onCancel={() => setSelectedLog(null)}
        width={800}
        footer={<Button onClick={() => setSelectedLog(null)}>关闭</Button>}
      >
        {selectedLog && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <Descriptions column={2} size="small" bordered>
              <Descriptions.Item label="异常模块">{selectedLog.module}</Descriptions.Item>
              <Descriptions.Item label="异常时间">{formatDateTime(selectedLog.createdTime)}</Descriptions.Item>
              <Descriptions.Item label="异常IP">{selectedLog.ip}</Descriptions.Item>
            </Descriptions>
            <div>
              <div style={{ fontWeight: 600, marginBottom: 8 }}>异常信息</div>
              <div style={{ padding: 12, backgroundColor: '#fef2f2', color: '#b91c1c', borderRadius: 6, fontFamily: 'monospace', fontSize: 13, border: '1px solid #fecaca' }}>
                {selectedLog.errorMsg}
              </div>
            </div>
            <div>
              <div style={{ fontWeight: 600, marginBottom: 8 }}>堆栈信息</div>
              <pre style={{ padding: 16, backgroundColor: '#1e293b', color: '#cbd5e1', borderRadius: 6, fontFamily: 'monospace', fontSize: 12, overflowX: 'auto', whiteSpace: 'pre-wrap', lineHeight: 1.6 }}>
                {selectedLog.stackTrace}
              </pre>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default LogError;
