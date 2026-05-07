import React, { useState, useEffect, useRef } from 'react';
import { Table, Button, Input, Popconfirm, App } from 'antd';
import { ReloadOutlined, LogoutOutlined, SearchOutlined, UserOutlined, EnvironmentOutlined, LaptopOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { formatDateTime } from '../lib/utils';

interface OnlineUser {
  sessionId: string;
  userId: number;
  username: string;
  ip: string;
  location: string;
  browser: string;
  loginTime: number;
  lastAccessTime: number;
}

export default function OnlineUsersTable() {
  const { message } = App.useApp();
  const [users, setUsers] = useState<OnlineUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');
  const hasFetched = useRef(false);

  useEffect(() => {
    if (hasFetched.current) return;
    hasFetched.current = true;
    fetchOnlineUsers();
  }, []);

  const fetchOnlineUsers = async (keyword?: string) => {
    setLoading(true);
    try {
      const url = keyword
        ? `/api/online-users/list?username=${encodeURIComponent(keyword)}`
        : '/api/online-users/list';

      const response = await fetch(url);
      const data = await response.json();

      if (data.success && data.data) {
        setUsers(data.data);
      } else {
        message.error(data.errMessage || '获取在线用户失败');
      }
    } catch {
      message.error('网络错误，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handleForceLogout = async (user: OnlineUser) => {
    try {
      const res = await fetch('/api/online-users/force-logout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: user.sessionId }),
      });
      const data = await res.json();
      if (data.success) {
        message.success(`已成功将用户 ${user.username} 强制下线`);
        fetchOnlineUsers(searchKeyword);
      } else {
        message.error(data.errMessage || '强制下线失败');
      }
    } catch {
      message.error('网络错误，请重试');
    }
  };

  const columns: ColumnsType<OnlineUser> = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      render: (text: string) => (
        <span><UserOutlined style={{ marginRight: 8 }} />{text}</span>
      ),
    },
    {
      title: 'IP地址',
      dataIndex: 'ip',
      key: 'ip',
    },
    {
      title: '登录地点',
      dataIndex: 'location',
      key: 'location',
      render: (text: string) => (
        <span><EnvironmentOutlined style={{ marginRight: 4 }} />{text}</span>
      ),
    },
    {
      title: '浏览器',
      dataIndex: 'browser',
      key: 'browser',
      render: (text: string) => (
        <span><LaptopOutlined style={{ marginRight: 4 }} />{text}</span>
      ),
    },
    {
      title: '登录时间',
      dataIndex: 'loginTime',
      key: 'loginTime',
      render: (val: number) => formatDateTime(val),
    },
    {
      title: '操作',
      key: 'action',
      align: 'right',
      render: (_: unknown, record: OnlineUser) => (
        <Popconfirm
          title="确认强退"
          description={`确定要强制下线用户 "${record.username}" 吗？`}
          onConfirm={() => handleForceLogout(record)}
          okText="确定"
          cancelText="取消"
        >
          <Button type="link" danger size="small" icon={<LogoutOutlined />}>
            强退
          </Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <div>
          <h2 style={{ fontSize: 20, fontWeight: 600, margin: 0 }}>在线用户</h2>
          <p style={{ color: '#8c8c8c', marginTop: 4, marginBottom: 0 }}>监控当前系统活跃会话，支持强制下线操作。</p>
        </div>
        <Button icon={<ReloadOutlined />} onClick={() => fetchOnlineUsers(searchKeyword)}>
          刷新列表
        </Button>
      </div>

      <div style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="搜索用户名..."
          prefix={<SearchOutlined />}
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          onSearch={(value) => fetchOnlineUsers(value)}
          style={{ width: 300 }}
          allowClear
        />
      </div>

      <Table<OnlineUser>
        columns={columns}
        dataSource={users}
        rowKey="sessionId"
        loading={loading}
        size="small"
        pagination={false}
        locale={{ emptyText: '暂无在线用户' }}
      />
    </div>
  );
}
