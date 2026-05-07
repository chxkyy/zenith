import React, { useState } from 'react';
import { Table, Button, Tag, Input, Space } from 'antd';
import { PlusOutlined, ReloadOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';

interface ConfigRecord {
  id: number;
  name: string;
  key: string;
  value: string;
  type: string;
  remark: string;
}

const mockConfigs: ConfigRecord[] = [
  { id: 1, name: '主页标题', key: 'sys.index.title', value: 'Zenith Enterprise Admin', type: 'SYSTEM', remark: '系统主页显示的标题' },
  { id: 2, name: '用户默认密码', key: 'sys.user.defaultPassword', value: '123456', type: 'SYSTEM', remark: '新增用户时的初始密码' },
  { id: 3, name: '验证码开关', key: 'sys.login.captchaEnabled', value: 'true', type: 'SYSTEM', remark: '登录时是否开启验证码' },
];

export default function ConfigTable() {
  const [configs] = useState(mockConfigs);
  const [searchText, setSearchText] = useState('');

  const filteredConfigs = configs.filter(
    (item) =>
      item.name.includes(searchText) ||
      item.key.includes(searchText)
  );

  const columns: ColumnsType<ConfigRecord> = [
    {
      title: '参数名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '参数键名',
      dataIndex: 'key',
      key: 'key',
      render: (text: string) => <code>{text}</code>,
    },
    {
      title: '参数键值',
      dataIndex: 'value',
      key: 'value',
    },
    {
      title: '系统内置',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => (
        <Tag color="blue">{type === 'SYSTEM' ? '是' : '否'}</Tag>
      ),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      align: 'right',
      render: () => (
        <Space size="small">
          <Button type="link" size="small" icon={<EditOutlined />}>
            编辑
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
          <h2 style={{ fontSize: 20, fontWeight: 600, margin: 0 }}>参数设置</h2>
          <p style={{ color: '#8c8c8c', margin: '4px 0 0' }}>动态配置系统运行参数，无需重启服务即可生效。</p>
        </div>
        <Space>
          <Button icon={<ReloadOutlined />}>刷新缓存</Button>
          <Button type="primary" icon={<PlusOutlined />}>新增参数</Button>
        </Space>
      </div>

      <div style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="搜索参数名称或键名..."
          allowClear
          style={{ width: 300 }}
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          onSearch={(value) => setSearchText(value)}
        />
      </div>

      <Table<ConfigRecord>
        columns={columns}
        dataSource={filteredConfigs}
        rowKey="id"
        size="small"
        pagination={false}
      />
    </div>
  );
}
