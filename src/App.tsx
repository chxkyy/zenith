import React, { useState } from 'react';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Dashboard from './components/Dashboard';
import UserTable from './components/UserTable';
import RoleTable from './components/RoleTable';
import OrgTable from './components/OrgTable';
import Profile from './components/Profile';
import NoticeTable from './components/NoticeTable';
import PermissionTable from './components/PermissionTable';
import MenuTable from './components/MenuTable';
import LogOper from './components/LogOper';
import LogLogin from './components/LogLogin';
import LogError from './components/LogError';
import ConfigTable from './components/ConfigTable';
import DictTable from './components/DictTable';
import FileTable from './components/FileTable';
import MonitoringTable from './components/MonitoringTable';
import CacheTable from './components/CacheTable';
import OnlineUsersTable from './components/OnlineUsersTable';
import SystemLogs from './components/SystemLogs';
import { motion, AnimatePresence } from 'motion/react';

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');

  const renderContent = () => {
    switch (activeTab) {
      case 'dashboard':
        return <Dashboard />;
      case 'users':
        return <UserTable />;
      case 'roles':
        return <RoleTable />;
      case 'orgs':
        return <OrgTable />;
      case 'profile':
        return <Profile />;
      case 'notices':
        return <NoticeTable />;
      case 'permissions':
        return <PermissionTable />;
      case 'menus':
        return <MenuTable />;
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
        <Header />
        
        <div className="flex-1 overflow-y-auto">
          <AnimatePresence mode="wait">
            <motion.div
              key={activeTab}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.2 }}
            >
              {renderContent()}
            </motion.div>
          </AnimatePresence>
        </div>
      </main>
    </div>
  );
}
