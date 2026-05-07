import React from 'react';
import { Layout, Menu } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
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
      { key: 'dashboard', icon: <DashboardOutlined />, label: '仪表盘', path: '/dashboard' },
    ],
  },
  {
    key: 'core',
    label: '核心管理',
    type: 'group' as const,
    children: [
      { key: 'roles', icon: <SafetyCertificateOutlined />, label: '角色管理', path: '/roles' },
      { key: 'menus', icon: <MenuOutlined />, label: '菜单管理', path: '/menus' },
      { key: 'orgs', icon: <BankOutlined />, label: '组织管理', path: '/orgs' },
    ],
  },
  {
    key: 'ops',
    label: '系统运维',
    type: 'group' as const,
    children: [
      { key: 'dicts', icon: <BookOutlined />, label: '字典管理', path: '/dicts' },
      { key: 'logs_oper', icon: <FileTextOutlined />, label: '操作日志', path: '/logs/oper' },
      { key: 'logs_login', icon: <FileTextOutlined />, label: '登录日志', path: '/logs/login' },
      { key: 'logs_error', icon: <FileTextOutlined />, label: '异常日志', path: '/logs/error' },
      { key: 'config', icon: <SettingOutlined />, label: '系统配置', path: '/config' },
      { key: 'cache', icon: <DatabaseOutlined />, label: '缓存管理', path: '/cache' },
    ],
  },
  {
    key: 'aux',
    label: '辅助功能',
    type: 'group' as const,
    children: [
      { key: 'files', icon: <FolderOpenOutlined />, label: '文件管理', path: '/files' },
      { key: 'notices', icon: <BellOutlined />, label: '通知公告', path: '/notices' },
      { key: 'online', icon: <UserOutlined />, label: '在线用户', path: '/online' },
    ],
  },
  {
    key: 'monitor',
    label: '监控统计',
    type: 'group' as const,
    children: [
      { key: 'monitoring', icon: <MonitorOutlined />, label: '数据监控', path: '/monitoring' },
    ],
  },
  {
    key: 'personal',
    label: '个人中心',
    type: 'group' as const,
    children: [
      { key: 'profile', icon: <UserOutlined />, label: '账户设置', path: '/profile' },
    ],
  },
];

interface SidebarProps {
  collapsed: boolean;
}

export default function Sidebar({ collapsed }: SidebarProps) {
  const navigate = useNavigate();
  const location = useLocation();

  const selectedKey = getSelectedKey(location.pathname);

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
        selectedKeys={[selectedKey]}
        onClick={({ key }) => {
          const path = findPathByKey(key);
          if (path) navigate(path);
        }}
        items={menuItems}
      />
    </Sider>
  );
}

function getSelectedKey(pathname: string): string {
  if (pathname === '/') return 'dashboard';
  const item = findItemByPath(pathname);
  return item?.key || 'dashboard';
}

function findItemByPath(pathname: string): { key: string; path: string } | null {
  for (const group of menuItems) {
    if (group.children) {
      for (const child of group.children) {
        if ((child as any).path === pathname) {
          return { key: child.key, path: (child as any).path };
        }
      }
    }
  }
  return null;
}

function findPathByKey(key: string): string | undefined {
  for (const group of menuItems) {
    if (group.children) {
      for (const child of group.children) {
        if (child.key === key) {
          return (child as any).path;
        }
      }
    }
  }
  return undefined;
}
