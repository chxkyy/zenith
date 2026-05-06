import React, { useState, useEffect } from 'react';
import { User, Shield, History, LogOut, Monitor, MapPin, Clock, Key, Mail, Phone, Building, X, AlertCircle, Download } from 'lucide-react';
import { motion } from 'motion/react';
import { cn } from '../lib/utils';
import Notification from './Notification';
import { formatDateTime } from '../lib/utils';

const tabs = [
  { id: 'info', label: '个人信息', icon: User },
  { id: 'security', label: '账户安全', icon: Shield },
  { id: 'logs', label: '操作记录', icon: History },
];

interface UserDTO {
  id: number;
  username: string;
  loginId: string;
  email: string;
  phone?: string;
  orgId?: number;
  orgName?: string;
  roles?: string[];
  roleNames?: string;
  status: number;
  createdTime: string;
  updateTime: string;
}

interface OperLog {
  id: number;
  module: string;
  content: string;
  operator: string;
  ip: string;
  result: string;
  remark: string;
  createdTime: string;
  createTime: number;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
  createUserName?: string;
  updateUserName?: string;
}

export default function Profile() {
  const [activeTab, setActiveTab] = useState('info');
  const [userInfo, setUserInfo] = useState<UserDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [operLogs, setOperLogs] = useState<OperLog[]>([]);
  const [logsLoading, setLogsLoading] = useState(false);

  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordForm, setPasswordForm] = useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [passwordLoading, setPasswordLoading] = useState(false);
  const [passwordError, setPasswordError] = useState('');

  const [showProfileModal, setShowProfileModal] = useState(false);
  const [profileForm, setProfileForm] = useState({
    username: '',
    email: '',
    phone: '',
  });
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileError, setProfileError] = useState('');

  const [notification, setNotification] = useState<{
    message: string;
    type: 'success' | 'error' | 'info';
    key: number;
  } | null>(null);

  useEffect(() => {
    fetchUserInfo();
  }, []);

  useEffect(() => {
    if (activeTab === 'logs' && operLogs.length === 0) {
      fetchOperLogs();
    }
  }, [activeTab]);

  const fetchUserInfo = async () => {
    try {
      const response = await fetch('/api/auth/me', { credentials: 'include' });
      if (response.status === 503) throw new Error('后端服务正在启动，请稍候...');
      const data = await response.json();
      if (data.success) {
        setUserInfo(data.data);
      } else {
        setNotification({ message: data.errMessage || '获取用户信息失败', type: 'error', key: Date.now() });
      }
    } catch (error: any) {
      console.error('Failed to fetch user info:', error);
      setNotification({ message: error.message || '网络错误', type: 'error', key: Date.now() });
    } finally {
      setLoading(false);
    }
  };

  const fetchOperLogs = async () => {
    setLogsLoading(true);
    try {
      const params = new URLSearchParams();
      params.append('pageIndex', '1');
      params.append('pageSize', '20');

      const response = await fetch(`/api/oper-logs?${params.toString()}`, { credentials: 'include' });
      if (response.status === 503) throw new Error('后端服务正在启动，请稍候...');
      const data = await response.json();
      if (data.success) {
        setOperLogs(data.data || []);
      } else {
        setNotification({ message: data.errMessage || '获取操作记录失败', type: 'error', key: Date.now() });
      }
    } catch (error: any) {
      console.error('Failed to fetch oper logs:', error);
      setNotification({ message: error.message || '网络错误', type: 'error', key: Date.now() });
    } finally {
      setLogsLoading(false);
    }
  };

  const handleChangePassword = async () => {
    setPasswordError('');
    if (!passwordForm.oldPassword || !passwordForm.newPassword || !passwordForm.confirmPassword) {
      setPasswordError('请填写所有密码字段');
      return;
    }
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setPasswordError('两次输入的新密码不一致');
      return;
    }
    if (passwordForm.newPassword.length < 6) {
      setPasswordError('新密码长度不能少于6位');
      return;
    }

    setPasswordLoading(true);
    try {
      const response = await fetch('/api/auth/password', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
        }),
      });
      const data = await response.json();
      if (data.success) {
        setNotification({ message: '密码修改成功', type: 'success', key: Date.now() });
        setShowPasswordModal(false);
        setPasswordForm({ oldPassword: '', newPassword: '', confirmPassword: '' });
      } else {
        setPasswordError(data.errMessage || '密码修改失败');
      }
    } catch (error: any) {
      setPasswordError(error.message || '网络错误');
    } finally {
      setPasswordLoading(false);
    }
  };

  const handleEditProfile = () => {
    if (!userInfo) return;
    setProfileForm({
      username: userInfo.username || '',
      email: userInfo.email || '',
      phone: userInfo.phone || '',
    });
    setProfileError('');
    setShowProfileModal(true);
  };

  const handleSaveProfile = async () => {
    setProfileError('');
    if (!profileForm.username.trim()) {
      setProfileError('用户名不能为空');
      return;
    }

    setProfileLoading(true);
    try {
      const response = await fetch('/api/auth/profile', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(profileForm),
      });
      const data = await response.json();
      if (data.success) {
        setNotification({ message: '个人信息更新成功', type: 'success', key: Date.now() });
        setShowProfileModal(false);
        fetchUserInfo();
      } else {
        setProfileError(data.errMessage || '更新失败');
      }
    } catch (error: any) {
      setProfileError(error.message || '网络错误');
    } finally {
      setProfileLoading(false);
    }
  };

  const handleExportLogs = () => {
    window.open('/api/oper-logs/export', '_blank');
  };

  const getRoleDisplayName = () => {
    if (!userInfo?.roles) return '-';
    if (userInfo.roles.includes('ROLE_ADMIN')) return '超级管理员';
    if (userInfo.roles.includes('ROLE_USER')) return '普通用户';
    return userInfo.roleNames || userInfo.roles.join(', ');
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'info':
        if (loading) {
          return (
            <div className="flex items-center justify-center py-20">
              <div className="w-10 h-10 border-4 border-slate-200 border-t-blue-600 rounded-full animate-spin"></div>
            </div>
          );
        }

        return (
          <div className="space-y-8">
            <div className="flex items-center gap-6">
              <div className="w-24 h-24 bg-blue-100 rounded-2xl flex items-center justify-center text-blue-600">
                <User size={48} />
              </div>
              <div>
                <h3 className="text-2xl font-bold text-slate-900">{userInfo?.username || '-'}</h3>
                <p className="text-slate-500">{getRoleDisplayName()}</p>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-4">
                <div className="flex items-center gap-3 text-slate-600">
                  <Mail size={18} />
                  <span className="font-medium">电子邮箱:</span>
                  <span>{userInfo?.email || '-'}</span>
                </div>
                <div className="flex items-center gap-3 text-slate-600">
                  <Phone size={18} />
                  <span className="font-medium">手机号码:</span>
                  <span>{userInfo?.phone || '-'}</span>
                </div>
                <div className="flex items-center gap-3 text-slate-600">
                  <Building size={18} />
                  <span className="font-medium">所属部门:</span>
                  <span>{userInfo?.orgName || '-'}</span>
                </div>
              </div>
              <div className="space-y-4">
                <div className="flex items-center gap-3 text-slate-600">
                  <Clock size={18} />
                  <span className="font-medium">创建时间:</span>
                  <span>{formatDateTime(userInfo?.createdTime)}</span>
                </div>
                <div className="flex items-center gap-3 text-slate-600">
                  <MapPin size={18} />
                  <span className="font-medium">最后登录IP:</span>
                  <span>-</span>
                </div>
              </div>
            </div>

            <div className="pt-6 border-t border-slate-100">
              <button
                onClick={handleEditProfile}
                className="px-6 py-2.5 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
              >
                编辑个人信息
              </button>
            </div>
          </div>
        );
      case 'security':
        return (
          <div className="space-y-8">
            <div className="bg-slate-50 p-6 rounded-2xl border border-slate-100 flex items-center justify-between">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-white rounded-xl border border-slate-200 text-slate-600">
                  <Key size={24} />
                </div>
                <div>
                  <h4 className="font-bold text-slate-900">登录密码</h4>
                  <p className="text-sm text-slate-500">建议定期修改密码以保障账户安全</p>
                </div>
              </div>
              <button
                onClick={() => setShowPasswordModal(true)}
                className="px-4 py-2 bg-white border border-slate-200 rounded-lg font-semibold text-slate-700 hover:bg-slate-50 transition-all"
              >
                修改密码
              </button>
            </div>

            <div className="space-y-4">
              <h4 className="font-bold text-slate-900 flex items-center gap-2">
                <Monitor size={20} />
                当前在线会话
              </h4>
              <div className="border border-slate-200 rounded-2xl overflow-hidden">
                <table className="w-full text-left">
                  <thead className="bg-slate-50 border-b border-slate-200">
                    <tr>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">登录时间</th>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">IP地址</th>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">设备</th>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">状态</th>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase text-right">操作</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    <tr>
                      <td className="px-6 py-4 text-sm text-slate-600">{formatDateTime(new Date())}</td>
                      <td className="px-6 py-4 text-sm text-slate-600">-</td>
                      <td className="px-6 py-4 text-sm text-slate-600">当前设备</td>
                      <td className="px-6 py-4">
                        <span className="px-2 py-1 bg-emerald-50 text-emerald-600 text-xs font-bold rounded-md">当前在线</span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <button
                          onClick={() => {
                            fetch('/api/auth/logout', { method: 'POST', credentials: 'include' }).then(() => {
                              window.location.reload();
                            });
                          }}
                          className="text-slate-400 hover:text-red-600 transition-colors"
                          title="退出当前会话"
                        >
                          <LogOut size={18} />
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        );
      case 'logs':
        return (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h4 className="font-bold text-slate-900">最近操作记录</h4>
              <button
                onClick={handleExportLogs}
                className="flex items-center gap-1.5 text-sm text-blue-600 font-medium hover:underline"
              >
                <Download size={16} />
                导出记录
              </button>
            </div>
            <div className="border border-slate-200 rounded-2xl overflow-hidden">
              <table className="w-full text-left">
                <thead className="bg-slate-50 border-b border-slate-200">
                  <tr>
                    <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">时间</th>
                    <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">模块</th>
                    <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">操作内容</th>
                    <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">结果</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {logsLoading ? (
                    <tr>
                      <td colSpan={4} className="px-6 py-10 text-center text-slate-400">
                        <div className="inline-block w-8 h-8 border-3 border-slate-200 border-t-slate-600 rounded-full animate-spin mr-2"></div>
                        加载中...
                      </td>
                    </tr>
                  ) : operLogs.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="px-6 py-10 text-center text-slate-400">暂无操作记录</td>
                    </tr>
                  ) : (
                    operLogs.map((log) => (
                      <tr key={log.id}>
                        <td className="px-6 py-4 text-sm text-slate-600">{formatDateTime(log.createdTime)}</td>
                        <td className="px-6 py-4 text-sm text-slate-600">{log.module}</td>
                        <td className="px-6 py-4 text-sm text-slate-600">{log.content}</td>
                        <td className="px-6 py-4">
                          <span className={cn(
                            "px-2 py-1 text-xs font-bold rounded-md",
                            log.result === '成功'
                              ? "bg-emerald-50 text-emerald-600"
                              : "bg-red-50 text-red-600"
                          )}>
                            {log.result}
                          </span>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="p-8 max-w-5xl mx-auto space-y-8">
      <div>
        <h2 className="text-3xl font-bold text-slate-900">个人中心</h2>
        <p className="text-slate-500 mt-2">管理您的个人信息、账户安全及查看操作历史。</p>
      </div>

      <div className="flex gap-2 p-1 bg-slate-100 rounded-2xl w-fit">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={cn(
              "flex items-center gap-2 px-6 py-2.5 rounded-xl font-semibold transition-all",
              activeTab === tab.id
                ? "bg-white text-blue-600 shadow-sm"
                : "text-slate-500 hover:text-slate-700"
            )}
          >
            <tab.icon size={18} />
            {tab.label}
          </button>
        ))}
      </div>

      <motion.div
        key={activeTab}
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm"
      >
        {renderTabContent()}
      </motion.div>

      {showPasswordModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm" onClick={(e) => {
          if (e.target === e.currentTarget) setShowPasswordModal(false);
        }}>
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 p-6 space-y-5"
          >
            <div className="flex items-center justify-between">
              <h3 className="text-xl font-bold text-slate-900">修改登录密码</h3>
              <button
                onClick={() => { setShowPasswordModal(false); setPasswordError(''); }}
                className="p-1.5 hover:bg-slate-100 rounded-lg transition-colors"
              >
                <X size={20} className="text-slate-400" />
              </button>
            </div>

            {passwordError && (
              <div className="flex items-center gap-2 px-4 py-3 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
                <AlertCircle size={16} />
                {passwordError}
              </div>
            )}

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1.5">原密码</label>
                <input
                  type="password"
                  value={passwordForm.oldPassword}
                  onChange={(e) => setPasswordForm(prev => ({ ...prev, oldPassword: e.target.value }))}
                  className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
                  placeholder="请输入当前密码"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1.5">新密码</label>
                <input
                  type="password"
                  value={passwordForm.newPassword}
                  onChange={(e) => setPasswordForm(prev => ({ ...prev, newPassword: e.target.value }))}
                  className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
                  placeholder="至少6个字符"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1.5">确认新密码</label>
                <input
                  type="password"
                  value={passwordForm.confirmPassword}
                  onChange={(e) => setPasswordForm(prev => ({ ...prev, confirmPassword: e.target.value }))}
                  onKeyDown={(e) => e.key === 'Enter' && handleChangePassword()}
                  className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
                  placeholder="再次输入新密码"
                />
              </div>
            </div>

            <div className="flex gap-3 pt-2">
              <button
                onClick={() => { setShowPasswordModal(false); setPasswordError(''); }}
                className="flex-1 px-4 py-2.5 border border-slate-200 rounded-xl font-semibold text-slate-700 hover:bg-slate-50 transition-all"
              >
                取消
              </button>
              <button
                onClick={handleChangePassword}
                disabled={passwordLoading}
                className="flex-1 px-4 py-2.5 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-all disabled:opacity-60 disabled:cursor-not-allowed"
              >
                {passwordLoading ? '提交中...' : '确认修改'}
              </button>
            </div>
          </motion.div>
        </div>
      )}

      {showProfileModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm" onClick={(e) => {
          if (e.target === e.currentTarget) setShowProfileModal(false);
        }}>
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 p-6 space-y-5"
          >
            <div className="flex items-center justify-between">
              <h3 className="text-xl font-bold text-slate-900">编辑个人信息</h3>
              <button
                onClick={() => { setShowProfileModal(false); setProfileError(''); }}
                className="p-1.5 hover:bg-slate-100 rounded-lg transition-colors"
              >
                <X size={20} className="text-slate-400" />
              </button>
            </div>

            {profileError && (
              <div className="flex items-center gap-2 px-4 py-3 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
                <AlertCircle size={16} />
                {profileError}
              </div>
            )}

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1.5">用户名</label>
                <input
                  type="text"
                  value={profileForm.username}
                  onChange={(e) => setProfileForm(prev => ({ ...prev, username: e.target.value }))}
                  className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
                  placeholder="请输入用户名"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1.5">电子邮箱</label>
                <input
                  type="email"
                  value={profileForm.email}
                  onChange={(e) => setProfileForm(prev => ({ ...prev, email: e.target.value }))}
                  className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
                  placeholder="请输入电子邮箱"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-1.5">手机号码</label>
                <input
                  type="tel"
                  value={profileForm.phone}
                  onChange={(e) => setProfileForm(prev => ({ ...prev, phone: e.target.value }))}
                  onKeyDown={(e) => e.key === 'Enter' && handleSaveProfile()}
                  className="w-full px-4 py-2.5 border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
                  placeholder="请输入手机号码"
                />
              </div>
            </div>

            <div className="flex gap-3 pt-2">
              <button
                onClick={() => { setShowProfileModal(false); setProfileError(''); }}
                className="flex-1 px-4 py-2.5 border border-slate-200 rounded-xl font-semibold text-slate-700 hover:bg-slate-50 transition-all"
              >
                取消
              </button>
              <button
                onClick={handleSaveProfile}
                disabled={profileLoading}
                className="flex-1 px-4 py-2.5 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-all disabled:opacity-60 disabled:cursor-not-allowed"
              >
                {profileLoading ? '保存中...' : '保存'}
              </button>
            </div>
          </motion.div>
        </div>
      )}

      {notification && (
        <Notification
          key={notification.key}
          message={notification.message}
          type={notification.type}
        />
      )}
    </div>
  );
}
