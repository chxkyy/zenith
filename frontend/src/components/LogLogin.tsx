import React, { useState, useEffect, useRef } from 'react';
import { Table, Button, Input, Space, Tag, Popconfirm, App, Card, Select } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { SearchOutlined, UndoOutlined, DeleteOutlined, DownloadOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { usePermission } from '../lib/PermissionContext';

interface LoginLog {
  id: number;
  username: string;
  ip: string;
  status: string;
  msg: string;
  loginAt: string;
  logoutAt: string | null;
  createUserId: number;
  updateUserId: number;
  createdTime: string;
  updateTime: string;
  createUserName?: string;
  updateUserName?: string;
}

const LogLogin: React.FC = () => {
  const { message } = App.useApp();
  const { hasPermission } = usePermission();
  const [logs, setLogs] = useState<LoginLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const hasFetchedLogs = useRef(false);
  const [searchParams, setSearchParams] = useState({ username: '', status: '', ip: '' });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams();
      if (searchParams.username) params.append('username', searchParams.username);
      if (searchParams.status) params.append('status', searchParams.status);
      if (searchParams.ip) params.append('ip', searchParams.ip);

      const response = await fetch(`/api/login-logs?${params.toString()}`);
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
      console.error('Failed to fetch login logs:', error);
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

  const handleSearch = () => { setCurrentPage(1); fetchLogs(); };
  const handleReset = () => {
    setSearchParams({ username: '', status: '', ip: '' });
  };

  const handleDelete = async (id: number) => {
    try {
      const response = await fetch(`/api/login-logs/delete`, {
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
    message.success('登录日志导出成功 (Excel)');
  };

  const columns: ColumnsType<LoginLog> = [
    { title: '日志ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '用户名', dataIndex: 'username', key: 'username', width: 100 },
    { title: '登录IP', dataIndex: 'ip', key: 'ip', width: 130, render: (v) => <code>{v}</code> },
    {
      title: '登录状态', dataIndex: 'status', key: 'status', width: 90,
      render: (v) => <Tag color={v === '成功' ? 'success' : 'error'}>{v}</Tag>
    },
    { title: '失败原因', dataIndex: 'msg', key: 'msg', width: 120, render: (v) => v || '-' },
    { title: '登录时间', dataIndex: 'loginAt', key: 'loginAt', width: 160, render: (v) => formatDateTime(v) },
    { title: '登出时间', dataIndex: 'logoutAt', key: 'logoutAt', width: 160, render: (v) => v ? formatDateTime(v) : '-' },
    { title: '创建人', dataIndex: 'createUserName', key: 'createUserName', width: 90, render: (v) => v || '-' },
    { title: '创建时间', dataIndex: 'createdTime', key: 'createdTime', width: 160, render: (v) => formatDateTime(v) },
    { title: '修改人', dataIndex: 'updateUserName', key: 'updateUserName', width: 90, render: (v) => v || '-' },
    { title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', width: 160, render: (v) => formatDateTime(v) },
    {
      title: '操作', key: 'action', width: 80, fixed: 'right',
      render: (_, record) => (
        hasPermission('ops:login-log:delete') ? (
        <Popconfirm title="确定删除该日志吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
          <Button type="link" size="small" danger>删除</Button>
        </Popconfirm>
        ) : null
      )
    }
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <Card size="small">
        <Space wrap>
          <Space>
            <span style={{ fontSize: 14, color: '#666' }}>用户名</span>
            <Input size="small" placeholder="请输入用户名" value={searchParams.username}
              onChange={(e) => setSearchParams({ ...searchParams, username: e.target.value })}
              onPressEnter={handleSearch} style={{ width: 140 }} />
          </Space>
          <Space>
            <span style={{ fontSize: 14, color: '#666' }}>登录状态</span>
            <Select size="small" value={searchParams.status || undefined} placeholder="全部"
              onChange={(v) => setSearchParams({ ...searchParams, status: v || '' })}
              style={{ width: 100 }} allowClear
              options={[
                { value: '成功', label: '成功' },
                { value: '失败', label: '失败' },
              ]}
            />
          </Space>
          <Space>
            <span style={{ fontSize: 14, color: '#666' }}>登录IP</span>
            <Input size="small" placeholder="请输入登录IP" value={searchParams.ip}
              onChange={(e) => setSearchParams({ ...searchParams, ip: e.target.value })}
              onPressEnter={handleSearch} style={{ width: 140 }} />
          </Space>
          <Button type="primary" size="small" icon={<SearchOutlined />} onClick={handleSearch}>查询</Button>
          <Button size="small" icon={<UndoOutlined />} onClick={handleReset}>重置</Button>
        </Space>
      </Card>

      <Card size="small" title="登录日志列表"
        extra={hasPermission('ops:login-log:export') ? <Button size="small" icon={<DownloadOutlined />} style={{ borderColor: '#16a34a', color: '#16a34a' }} onClick={handleExport}>导出</Button> : null}
      >
        <Table<LoginLog>
          columns={columns}
          dataSource={logs.slice((currentPage - 1) * pageSize, currentPage * pageSize)}
          rowKey="id"
          size="small"
          loading={loading}
          scroll={{ x: 1500 }}
          locale={{ emptyText: error || '暂无数据' }}
          pagination={{
            current: currentPage,
            pageSize: pageSize,
            total: logs.length,
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
      </Card>
    </div>
  );
};

export default LogLogin;
