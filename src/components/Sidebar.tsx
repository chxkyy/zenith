import React from 'react';
import { Layout, Menu } from 'antd';
import {
  DashboardOutlined,
  SafetyCertificateOutlined,
  MenuOutlined,
  BankOutlined,
  BookOutlined,
  FileTextOutlined,
  SettingOutlined,
  DatabaseOutlined,
  FolderOpenOutlined,
  BellOutlined,
  UserOutlined,
  MonitorOutlined,
} from '@ant-design/icons';

const { Sider } = Layout;

const menuItems = [
  {
    key: 'overview',
    label: '概览',
    type: 'group' as const,
    children: [
      { key: 'dashboard', icon: <DashboardOutlined />, label: '仪表盘' },
    ],
  },
  {
    key: 'core',
    label: '核心管理',
    type: 'group' as const,
    children: [
      { key: 'roles', icon: <SafetyCertificateOutlined />, label: '角色管理' },
      { key: 'menus', icon: <MenuOutlined />, label: '菜单管理' },
      { key: 'orgs', icon: <BankOutlined />, label: '组织管理' },
    ],
  },
  {
    key: 'ops',
    label: '系统运维',
    type: 'group' as const,
    children: [
      { key: 'dicts', icon: <BookOutlined />, label: '字典管理' },
      { key: 'logs_oper', icon: <FileTextOutlined />, label: '操作日志' },
      { key: 'logs_login', icon: <FileTextOutlined />, label: '登录日志' },
      { key: 'logs_error', icon: <FileTextOutlined />, label: '异常日志' },
      { key: 'config', icon: <SettingOutlined />, label: '系统配置' },
      { key: 'cache', icon: <DatabaseOutlined />, label: '缓存管理' },
    ],
  },
  {
    key: 'aux',
    label: '辅助功能',
    type: 'group' as const,
    children: [
      { key: 'files', icon: <FolderOpenOutlined />, label: '文件管理' },
      { key: 'notices', icon: <BellOutlined />, label: '通知公告' },
      { key: 'online', icon: <UserOutlined />, label: '在线用户' },
    ],
  },
  {
    key: 'monitor',
    label: '监控统计',
    type: 'group' as const,
    children: [
      { key: 'monitoring', icon: <MonitorOutlined />, label: '数据监控' },
    ],
  },
  {
    key: 'personal',
    label: '个人中心',
    type: 'group' as const,
    children: [
      { key: 'profile', icon: <UserOutlined />, label: '账户设置' },
    ],
  },
];

interface SidebarProps {
  activeTab: string;
  setActiveTab: (id: string) => void;
  collapsed: boolean;
}

export default function Sidebar({ activeTab, setActiveTab, collapsed }: SidebarProps) {
  return (
    <Sider
      collapsible
      collapsed={collapsed}
      trigger={null}
      width={220}
      style={{
        overflow: 'auto',
        height: '100vh',
        position: 'sticky',
        top: 0,
        left: 0,
      }}
    >
      <div style={{
        height: 48,
        margin: 12,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 8,
      }}>
        <div style={{
          width: 32,
          height: 32,
          background: '#1677ff',
          borderRadius: 6,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          fontWeight: 700,
          fontSize: 18,
          flexShrink: 0,
        }}>
          Z
        </div>
        {!collapsed && (
          <span style={{ color: '#fff', fontSize: 16, fontWeight: 600, whiteSpace: 'nowrap' }}>
            Zenith Admin
          </span>
        )}
      </div>
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[activeTab]}
        onClick={({ key }) => setActiveTab(key)}
        items={menuItems}
      />
    </Sider>
  );
}
