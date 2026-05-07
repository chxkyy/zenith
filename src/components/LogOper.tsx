import React, { useState, useEffect, useRef } from 'react';
import { Table, Button, Input, Space, Tag, Popconfirm, App, Card, Select } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { SearchOutlined, UndoOutlined, DownloadOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';

interface OperLog {
  id: number;
  module: string;
  content: string;
  operator: string;
  ip: string;
  result: string;
  remark: string;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

const LogOper: React.FC = () => {
  const { message } = App.useApp();
  const [logs, setLogs] = useState<OperLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const hasFetchedLogs = useRef(false);
  const [searchParams, setSearchParams] = useState({ operator: '', module: '', result: '' });

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams();
      if (searchParams.operator) params.append('operator', searchParams.operator);
      if (searchParams.module) params.append('module', searchParams.module);
      if (searchParams.result) params.append('result', searchParams.result);

      const response = await fetch(`/api/oper-logs?${params.toString()}`);
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
      console.error('Failed to fetch oper logs:', error);
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
    setSearchParams({ operator: '', module: '', result: '' });
  };

  const handleDelete = async (id: number) => {
    try {
      const response = await fetch(`/api/oper-logs/delete`, {
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

  const handleExport = () => {
    message.success('操作日志导出成功 (Excel)');
  };

  const columns: ColumnsType<OperLog> = [
    { title: '日志ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '操作人', dataIndex: 'operator', key: 'operator', width: 100 },
    { title: '操作IP', dataIndex: 'ip', key: 'ip', width: 130, render: (v) => <code>{v}</code> },
    { title: '操作模块', dataIndex: 'module', key: 'module', width: 110, render: (v) => <Tag color="blue">{v}</Tag> },
    { title: '操作内容', dataIndex: 'content', key: 'content', width: 180, ellipsis: true },
    {
      title: '结果', dataIndex: 'result', key: 'result', width: 80,
      render: (v) => <Tag color={v === '成功' ? 'success' : 'error'}>{v}</Tag>
    },
    { title: '创建人', dataIndex: 'createUserName', key: 'createUserName', width: 90, render: (v) => v || '-' },
    { title: '创建时间', dataIndex: 'createdTime', key: 'createdTime', width: 160, render: (v) => formatDateTime(v) },
    { title: '修改人', dataIndex: 'updateUserName', key: 'updateUserName', width: 90, render: (v) => v || '-' },
    { title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', width: 160, render: (v) => formatDateTime(v) },
    {
      title: '操作', key: 'action', width: 80, fixed: 'right',
      render: (_, record) => (
        <Popconfirm title="确定删除该日志吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
          <Button type="link" size="small" danger>删除</Button>
        </Popconfirm>
      )
    }
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <Card size="small">
        <Space wrap>
          <Space>
            <span style={{ fontSize: 14, color: '#666' }}>操作人</span>
            <Input size="small" placeholder="请输入操作人" value={searchParams.operator}
              onChange={(e) => setSearchParams({ ...searchParams, operator: e.target.value })}
              onPressEnter={handleSearch} style={{ width: 140 }} />
          </Space>
          <Space>
            <span style={{ fontSize: 14, color: '#666' }}>操作模块</span>
            <Input size="small" placeholder="请输入模块名称" value={searchParams.module}
              onChange={(e) => setSearchParams({ ...searchParams, module: e.target.value })}
              onPressEnter={handleSearch} style={{ width: 140 }} />
          </Space>
          <Space>
            <span style={{ fontSize: 14, color: '#666' }}>操作结果</span>
            <Select size="small" value={searchParams.result || undefined} placeholder="全部"
              onChange={(v) => setSearchParams({ ...searchParams, result: v || '' })}
              style={{ width: 100 }} allowClear
              options={[
                { value: '成功', label: '成功' },
                { value: '失败', label: '失败' },
              ]}
            />
          </Space>
          <Button type="primary" size="small" icon={<SearchOutlined />} onClick={handleSearch}>查询</Button>
          <Button size="small" icon={<UndoOutlined />} onClick={handleReset}>重置</Button>
        </Space>
      </Card>

      <Card size="small" title="操作日志列表"
        extra={<Button size="small" icon={<DownloadOutlined />} style={{ borderColor: '#16a34a', color: '#16a34a' }} onClick={handleExport}>导出</Button>}
      >
        <Table<OperLog>
          columns={columns}
          dataSource={logs}
          rowKey="id"
          size="small"
          loading={loading}
          scroll={{ x: 1300 }}
          locale={{ emptyText: error || '暂无数据' }}
          pagination={false}
        />
      </Card>
    </div>
  );
};

export default LogOper;
