import React, { useMemo } from 'react';
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
  AppstoreOutlined,
} from '@ant-design/icons';

const { Sider } = Layout;

const iconMap: Record<string, React.ReactNode> = {
  'DashboardOutlined': <DashboardOutlined />,
  'SafetyCertificateOutlined': <SafetyCertificateOutlined />,
  'MenuOutlined': <MenuOutlined />,
  'BankOutlined': <BankOutlined />,
  'BookOutlined': <BookOutlined />,
  'FileTextOutlined': <FileTextOutlined />,
  'SettingOutlined': <SettingOutlined />,
  'DatabaseOutlined': <DatabaseOutlined />,
  'FolderOpenOutlined': <FolderOpenOutlined />,
  'BellOutlined': <BellOutlined />,
  'UserOutlined': <UserOutlined />,
  'MonitorOutlined': <MonitorOutlined />,
  'AppstoreOutlined': <AppstoreOutlined />,
};

interface MenuItem {
  id: number;
  parentId: number;
  name: string;
  path: string;
  icon: string;
  sort: number;
  type: string;
}

interface SidebarProps {
  collapsed: boolean;
  menus?: MenuItem[];
}

export default function Sidebar({ collapsed, menus = [] }: SidebarProps) {
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = useMemo(() => {
    return getDefaultMenuItems();
  }, []);

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
          const path = findPathByKey(key, menuItems);
          if (path) navigate(path);
        }}
        items={menuItems}
      />
    </Sider>
  );
}

function getDefaultMenuItems() {
  return [
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
}

function buildMenuItems(menus: MenuItem[]) {
  const rootMenus = menus.filter(m => !m.parentId || m.parentId === 0);
  
  return rootMenus.map(menu => {
    const children = menus.filter(m => m.parentId === menu.id);
    
    if (children.length > 0) {
      return {
        key: `group_${menu.id}`,
        label: menu.name,
        type: 'group' as const,
        children: children.map(child => ({
          key: `menu_${child.id}`,
          icon: iconMap[child.icon] || <AppstoreOutlined />,
          label: child.name,
          path: child.path,
        })),
      };
    }
    
    return {
      key: `menu_${menu.id}`,
      icon: iconMap[menu.icon] || <AppstoreOutlined />,
      label: menu.name,
      path: menu.path,
    };
  });
}

function getSelectedKey(pathname: string): string {
  if (pathname === '/') return 'dashboard';
  return pathname.replace(/^\//, '').replace(/\//g, '_');
}

function findPathByKey(key: string, menuItems: any[]): string | undefined {
  for (const item of menuItems) {
    if (item.key === key && item.path) {
      return item.path;
    }
    if (item.children) {
      for (const child of item.children) {
        if (child.key === key && child.path) {
          return child.path;
        }
      }
    }
  }
  return undefined;
}
