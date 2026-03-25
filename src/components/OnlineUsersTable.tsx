import React, { useState } from 'react';
import { Search, UserCheck, LogOut, RefreshCw, MapPin, Monitor } from 'lucide-react';
import { cn } from '../lib/utils';

const mockOnlineUsers = [
  { id: 1, username: 'admin', ip: '127.0.0.1', location: '内网', browser: 'Chrome 122', loginTime: '2026-03-23 13:00:00' },
  { id: 2, username: 'editor', ip: '112.10.20.30', location: '上海', browser: 'Safari 17', loginTime: '2026-03-23 12:45:12' },
];

export default function OnlineUsersTable() {
  const [users] = useState(mockOnlineUsers);

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">在线用户</h2>
          <p className="text-slate-500 mt-1">监控当前系统活跃会话，支持强制下线操作。</p>
        </div>
        <button className="flex items-center gap-2 bg-white border border-slate-200 text-slate-700 px-4 py-2 rounded-xl font-medium hover:bg-slate-50 transition-all">
          <RefreshCw size={18} />
          刷新列表
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input type="text" placeholder="搜索用户名..." className="text-sm outline-none w-full" />
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
              {users.map((user) => (
                <tr key={user.id} className="hover:bg-slate-50 transition-colors">
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
                  <td className="px-6 py-4 text-sm text-slate-500">{user.loginTime}</td>
                  <td className="px-6 py-4 text-right">
                    <button className="flex items-center gap-1 p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors text-xs font-bold ml-auto">
                      <LogOut size={14} />
                      强退
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
