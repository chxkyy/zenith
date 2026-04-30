import React, { useState, lazy, Suspense, useEffect } from 'react';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import { motion, AnimatePresence } from 'motion/react';
import Login from './Login';

// 懒加载组件
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
const SystemLogs = lazy(() => import('./components/SystemLogs'));

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [checkingAuth, setCheckingAuth] = useState(true);
  const [currentUser, setCurrentUser] = useState<{ username?: string; email?: string } | null>(null);

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

  if (checkingAuth) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="w-12 h-12 border-4 border-slate-200 border-t-slate-700 rounded-full animate-spin"></div>
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
      case 'permissions':
        return <PermissionTable />;
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
      case 'system_logs':
        return <SystemLogs />;
      default:
        return (
          <div className="p-8 flex flex-col items-center justify-center h-[calc(100vh-64px)] text-slate-400">
            <div className="w-20 h-20 bg-slate-100 rounded-full flex items-center justify-center mb-4">
              <span className="text-4xl">🚧</span>
            </div>
            <h2 className="text-xl font-bold text-slate-900">模块开发中</h2>
            <p className="mt-2">该功能模块 ({activeTab}) 正在紧锣密鼓地开发中，敬请期待。</p>
          </div>
        );
    }
  };

  return (
    <div className="flex min-h-screen bg-slate-50">
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />

      <main className="flex-1 flex flex-col min-w-0">
        <Header
          username={currentUser?.username}
          email={currentUser?.email}
          onLogout={handleLogout}
        />
        
        <div className="flex-1 overflow-y-auto">
          <AnimatePresence mode="wait">
            <motion.div
              key={activeTab}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.2 }}
            >
              <Suspense fallback={
                <div className="p-8 flex flex-col items-center justify-center h-[calc(100vh-64px)]">
                  <div className="w-12 h-12 border-4 border-slate-200 border-t-slate-700 rounded-full animate-spin mb-4"></div>
                  <p className="text-slate-500">加载中...</p>
                </div>
              }>
                {renderContent()}
              </Suspense>
            </motion.div>
          </AnimatePresence>
        </div>
      </main>
    </div>
  );
}
