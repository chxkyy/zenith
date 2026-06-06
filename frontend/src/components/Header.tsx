import React, { useMemo } from 'react';
import { Layout, Button, Avatar, Dropdown, Space } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  LogoutOutlined,
  BellOutlined,
} from '@ant-design/icons';

const { Header: AntHeader } = Layout;

interface HeaderProps {
  username?: string;
  onLogout?: () => void;
  onToggleSidebar: () => void;
  collapsed: boolean;
}

export default function Header({ username, onLogout, onToggleSidebar, collapsed }: HeaderProps) {
  const menuItems = useMemo(() => [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: onLogout,
    },
  ], [onLogout]);

  return (
    <AntHeader style={{
      padding: '0 24px',
      background: '#fff',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      borderBottom: '1px solid #f0f0f0',
      height: 48,
      lineHeight: '48px',
    }}>
      <Button
        type="text"
        icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
        onClick={onToggleSidebar}
        style={{ fontSize: 16, width: 48, height: 48 }}
      />

      <Space size={16}>
        <Button type="text" icon={<BellOutlined />} style={{ fontSize: 16 }} />
        <Dropdown menu={{ items: menuItems }} placement="bottomRight">
          <Space style={{ cursor: 'pointer' }}>
            <Avatar size="small" icon={<UserOutlined />} />
            <span style={{ fontSize: 14 }}>{username || '管理员'}</span>
          </Space>
        </Dropdown>
      </Space>
    </AntHeader>
  );
}
