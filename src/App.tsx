import React, { useState, lazy, Suspense, useEffect } from 'react';
import { Layout, Spin } from 'antd';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Login from './Login';

const { Content } = Layout;

const Dashboard = lazy(() => import('./components/Dashboard'));
const RoleManagement = lazy(() => import('./components/RoleManagement'));
const MenuManagement = lazy(() => import('./components/MenuManagement'));
const OrgUserManagement = lazy(() => import('./components/OrgUserManagement'));
const Profile = lazy(() => import('./components/Profile'));
const NoticeTable = lazy(() => import('./components/NoticeTable'));
const LogOper = lazy(() => import('./components/LogOper'));
const LogLogin = lazy(() => import('./components/LogLogin'));
const LogError = lazy(() => import('./components/LogError'));
const ConfigTable = lazy(() => import('./components/ConfigTable'));
const DictTable = lazy(() => import('./components/DictTable'));
const FileTable = lazy(() => import('./components/FileTable'));
const MonitoringTable = lazy(() => import('./components/MonitoringTable'));
const CacheTable = lazy(() => import('./components/CacheTable'));
const OnlineUsersTable = lazy(() => import('./components/OnlineUsersTable'));

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [checkingAuth, setCheckingAuth] = useState(true);
  const [currentUser, setCurrentUser] = useState<{ username?: string; email?: string } | null>(null);
  const [collapsed, setCollapsed] = useState(() => {
    const saved = localStorage.getItem('sidebar-collapsed');
    return saved ? JSON.parse(saved) : false;
  });

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const response = await fetch('/api/auth/me', {
        credentials: 'include'
      });
      const data = await response.json();
      if (data.success) {
        setIsAuthenticated(true);
        setCurrentUser({ username: data.data.username, email: data.data.email });
      } else {
        setIsAuthenticated(false);
        setCurrentUser(null);
      }
    } catch (error) {
      setIsAuthenticated(false);
      setCurrentUser(null);
    } finally {
      setCheckingAuth(false);
    }
  };

  const handleLoginSuccess = () => {
    checkAuth();
  };

  const handleLogout = async () => {
    try {
      await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include'
      });
    } catch (error) {
      console.error('Logout error:', error);
    }
    setIsAuthenticated(false);
    setCurrentUser(null);
    setActiveTab('dashboard');
  };

  const toggleCollapsed = () => {
    setCollapsed(prev => {
      const newValue = !prev;
      localStorage.setItem('sidebar-collapsed', JSON.stringify(newValue));
      return newValue;
    });
  };

  if (checkingAuth) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return <Dashboard />;
      case 'roles':
        return <RoleManagement />;
      case 'orgs':
        return <OrgUserManagement />;
      case 'profile':
        return <Profile />;
      case 'notices':
        return <NoticeTable />;
      case 'menus':
        return <MenuManagement />;
      case 'logs_oper':
        return <LogOper />;
      case 'logs_login':
        return <LogLogin />;
      case 'logs_error':
        return <LogError />;
      case 'config':
        return <ConfigTable />;
      case 'dicts':
        return <DictTable />;
      case 'files':
        return <FileTable />;
      case 'monitoring':
        return <MonitoringTable />;
      case 'cache':
        return <CacheTable />;
      case 'online':
        return <OnlineUsersTable />;
      default:
        return (
          <div style={{ textAlign: 'center', padding: 48, color: '#999' }}>
            <h2>模块开发中</h2>
            <p>该功能模块 ({activeTab}) 正在开发中，敬请期待。</p>
          </div>
        );
    }
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} collapsed={collapsed} />
      <Layout>
        <Header
          username={currentUser?.username}
          onLogout={handleLogout}
          onToggleSidebar={toggleCollapsed}
          collapsed={collapsed}
        />
        <Content style={{ margin: 16, overflow: 'auto' }}>
          <Suspense fallback={
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '60vh' }}>
              <Spin size="large" tip="加载中..." />
            </div>
          }>
            {renderContent()}
          </Suspense>
        </Content>
      </Layout>
    </Layout>
  );
}
