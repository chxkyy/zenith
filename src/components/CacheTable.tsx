import React, { useState } from 'react';
import { Table, Button, Tag, Input, Space } from 'antd';
import { ReloadOutlined, EyeOutlined, DeleteOutlined, KeyOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';

interface CacheRecord {
  id: number;
  name: string;
  key: string;
  type: string;
  size: string;
  expire: string;
}

const mockCaches: CacheRecord[] = [
  { id: 1, name: '用户权限缓存', key: 'sys:auth:perm:1', type: 'REDIS', size: '2.5 KB', expire: '3600s' },
  { id: 2, name: '字典数据缓存', key: 'sys:dict:all', type: 'REDIS', size: '124 KB', expire: '永久' },
  { id: 3, name: '系统配置缓存', key: 'sys:config:all', type: 'REDIS', size: '12 KB', expire: '永久' },
];

export default function CacheTable() {
  const [caches] = useState(mockCaches);
  const [searchText, setSearchText] = useState('');

  const filteredCaches = caches.filter(
    (item) => item.key.includes(searchText)
  );

  const columns: ColumnsType<CacheRecord> = [
    {
      title: '缓存名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '缓存键名',
      dataIndex: 'key',
      key: 'key',
      render: (text: string) => (
        <Space size={4}>
          <KeyOutlined style={{ color: '#8c8c8c' }} />
          <code>{text}</code>
        </Space>
      ),
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => <Tag color="red">{type}</Tag>,
    },
    {
      title: '大小',
      dataIndex: 'size',
      key: 'size',
    },
    {
      title: '过期时间',
      dataIndex: 'expire',
      key: 'expire',
    },
    {
      title: '操作',
      key: 'action',
      align: 'right',
      render: () => (
        <Space size="small">
          <Button type="link" size="small" icon={<EyeOutlined />}>
            查看
          </Button>
          <Button type="link" size="small" danger icon={<DeleteOutlined />}>
            删除
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <div>
          <h2 style={{ fontSize: 20, fontWeight: 600, margin: 0 }}>缓存管理</h2>
          <p style={{ color: '#8c8c8c', margin: '4px 0 0' }}>管理系统Redis缓存数据，支持按键名查询、查看详情及清理。</p>
        </div>
        <Button icon={<ReloadOutlined />}>刷新列表</Button>
      </div>

      <div style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="搜索缓存键名..."
          allowClear
          style={{ width: 300 }}
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          onSearch={(value) => setSearchText(value)}
        />
      </div>

      <Table<CacheRecord>
        columns={columns}
        dataSource={filteredCaches}
        rowKey="id"
        size="small"
        pagination={false}
      />
    </div>
  );
}
