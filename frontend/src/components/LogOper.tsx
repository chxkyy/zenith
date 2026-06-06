import React, { useState, useEffect, useRef } from 'react';
import { Table, Button, Input, Space, Tag, Popconfirm, App, Card, Select } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { SearchOutlined, UndoOutlined, DownloadOutlined } from '@ant-design/icons';
import { formatDateTime } from '../lib/utils';
import { usePermission } from '../lib/PermissionContext';
import { get, post, del } from '../lib/apiClient';

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
  const { hasPermission } = usePermission();
  const [logs, setLogs] = useState<OperLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const hasFetchedLogs = useRef(false);
  const [searchParams, setSearchParams] = useState({ operator: '', module: '', result: '' });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const params: Record<string, string> = {};
      if (searchParams.operator) params.operator = searchParams.operator;
      if (searchParams.module) params.module = searchParams.module;
      if (searchParams.result) params.result = searchParams.result;

      const data = await get<OperLog[]>('/api/oper-logs', params);
      setLogs(data);
    } catch (error: unknown) {
      setError(error instanceof Error ? error.message : '网络错误');
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
    setSearchParams({ operator: '', module: '', result: '' });
  };

  const handleDelete = async (id: number) => {
    try {
      await del('/api/oper-logs/delete', { id });
      message.success('删除成功');
      fetchLogs();
    } catch (error: unknown) {
      message.error(error instanceof Error ? error.message : '删除失败');
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
        hasPermission('ops:oper-log:delete') ? (
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
        extra={hasPermission('ops:oper-log:export') ? <Button size="small" icon={<DownloadOutlined />} style={{ borderColor: '#16a34a', color: '#16a34a' }} onClick={handleExport}>导出</Button> : null}
      >
        <Table<OperLog>
          columns={columns}
          dataSource={logs.slice((currentPage - 1) * pageSize, currentPage * pageSize)}
          rowKey="id"
          size="small"
          loading={loading}
          scroll={{ x: 1300 }}
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

export default LogOper;
