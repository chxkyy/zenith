import React, { useState, lazy, Suspense, useEffect } from 'react';
import { Layout, Spin } from 'antd';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Login from './Login';
import { PermissionProvider } from './lib/PermissionContext';

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
const ProcessTemplateTable = lazy(() => import('./components/ProcessTemplateTable'));
const TaskList = lazy(() => import('./components/TaskList'));
const StartProcess = lazy(() => import('./components/StartProcess'));
const MyProcess = lazy(() => import('./components/MyProcess'));
const ProcessDetail = lazy(() => import('./components/ProcessDetail'));

function AppContent() {
  const location = useLocation();
  const navigate = useNavigate();
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [checkingAuth, setCheckingAuth] = useState(true);
  const [currentUser, setCurrentUser] = useState<{ username?: string; email?: string } | null>(null);
  const [permissions, setPermissions] = useState<string[]>([]);
  const [menus, setMenus] = useState<any[]>([]);
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
        fetchPermissions();
        fetchMenus();
      } else {
        setIsAuthenticated(false);
        setCurrentUser(null);
        setPermissions([]);
        setMenus([]);
      }
    } catch (error) {
      setIsAuthenticated(false);
      setCurrentUser(null);
      setPermissions([]);
      setMenus([]);
    } finally {
      setCheckingAuth(false);
    }
  };

  const fetchPermissions = async () => {
    try {
      const response = await fetch('/api/auth/permissions', {
        credentials: 'include'
      });
      const data = await response.json();
      if (data.success) {
        setPermissions(data.data || []);
      } else {
        setPermissions([]);
      }
    } catch (error) {
      setPermissions([]);
    }
  };

  const fetchMenus = async () => {
    try {
      const response = await fetch('/api/auth/menus', {
        credentials: 'include'
      });
      const data = await response.json();
      if (data.success) {
        setMenus(data.data || []);
      } else {
        setMenus([]);
      }
    } catch (error) {
      setMenus([]);
    }
  };

  const handleLoginSuccess = () => {
    checkAuth();
    navigate('/dashboard');
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
    setPermissions([]);
    setMenus([]);
    navigate('/dashboard');
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

  return (
    <PermissionProvider value={{ permissions, menus }}>
    <Layout style={{ minHeight: '100vh' }}>
      <Sidebar collapsed={collapsed} menus={menus} />
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
              <Spin size="large" description="加载中..." />
            </div>
          }>
            <Routes>
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/roles" element={<RoleManagement />} />
              <Route path="/orgs" element={<OrgUserManagement />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="/notices" element={<NoticeTable />} />
              <Route path="/menus" element={<MenuManagement />} />
              <Route path="/logs/oper" element={<LogOper />} />
              <Route path="/logs/login" element={<LogLogin />} />
              <Route path="/logs/error" element={<LogError />} />
              <Route path="/config" element={<ConfigTable />} />
              <Route path="/dicts" element={<DictTable />} />
              <Route path="/files" element={<FileTable />} />
              <Route path="/monitoring" element={<MonitoringTable />} />
              <Route path="/cache" element={<CacheTable />} />
              <Route path="/online" element={<OnlineUsersTable />} />
              <Route path="/workflow/templates" element={<ProcessTemplateTable />} />
              <Route path="/workflow/todo" element={<TaskList />} />
              <Route path="/workflow/start" element={<StartProcess />} />
              <Route path="/workflow/my" element={<MyProcess />} />
              <Route path="/workflow/detail" element={<ProcessDetail />} />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
            </Routes>
          </Suspense>
        </Content>
      </Layout>
    </Layout>
    </PermissionProvider>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AppContent />
    </BrowserRouter>
  );
}
