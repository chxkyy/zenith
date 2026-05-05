import React, { useState, useEffect, useRef } from 'react';
import { Search, UserCheck, LogOut, RefreshCw, MapPin, Monitor } from 'lucide-react';
import { cn, formatDateTime } from '../lib/utils';
import Notification from './Notification';

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
  const [users, setUsers] = useState<OnlineUser[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [notification, setNotification] = useState<{
    message: string;
    type: 'success' | 'error' | 'info';
    key: number;
  } | null>(null);

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
        setNotification({
          message: data.errMessage || '获取在线用户失败',
          type: 'error',
          key: Date.now(),
        });
      }
    } catch {
      setNotification({
        message: '网络错误，请重试',
        type: 'error',
        key: Date.now(),
      });
    } finally {
      setLoading(false);
    }
  };

  const handleForceLogout = (user: OnlineUser) => {
    if (window.confirm(`确定要强制下线用户 "${user.username}" 吗？`)) {
      fetch('/api/online-users/force-logout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: user.sessionId }),
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            setNotification({
              message: `已成功将用户 ${user.username} 强制下线`,
              type: 'success',
              key: Date.now(),
            });
            fetchOnlineUsers(searchKeyword);
          } else {
            setNotification({
              message: data.errMessage || '强制下线失败',
              type: 'error',
              key: Date.now(),
            });
          }
        })
        .catch(() => {
          setNotification({
            message: '网络错误，请重试',
            type: 'error',
            key: Date.now(),
          });
        });
    }
  };

  const handleSearch = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      fetchOnlineUsers(searchKeyword);
    }
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">在线用户</h2>
          <p className="text-slate-500 mt-1">监控当前系统活跃会话，支持强制下线操作。</p>
        </div>
        <button
          onClick={() => fetchOnlineUsers(searchKeyword)}
          className="flex items-center gap-2 bg-white border border-slate-200 text-slate-700 px-4 py-2 rounded-xl font-medium hover:bg-slate-50 transition-all"
        >
          <RefreshCw size={18} />
          刷新列表
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input
              type="text"
              placeholder="搜索用户名..."
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              onKeyDown={handleSearch}
              className="text-sm outline-none w-full"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">用户名</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">IP地址</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">登录地点</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">浏览器</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">登录时间</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center">
                    <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto" />
                  </td>
                </tr>
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-slate-500">
                    暂无在线用户
                  </td>
                </tr>
              ) : (
                users.map((user) => (
                  <tr key={user.sessionId} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center">
                          <UserCheck size={16} />
                        </div>
                        <span className="text-sm font-semibold text-slate-900">{user.username}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{user.ip}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-1 text-sm text-slate-500">
                        <MapPin size={14} />
                        {user.location}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-1 text-sm text-slate-500">
                        <Monitor size={14} />
                        {user.browser}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-500">{formatDateTime(user.loginTime)}</td>
                    <td className="px-6 py-4 text-right">
                      <button
                        onClick={() => handleForceLogout(user)}
                        className="flex items-center gap-1 p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors text-xs font-bold ml-auto"
                      >
                        <LogOut size={14} />
                        强退
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

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
