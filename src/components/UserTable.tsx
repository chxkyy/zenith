import React, { useEffect, useState } from 'react';
import { MoreHorizontal, Search, Filter, UserPlus } from 'lucide-react';
import { cn } from '../lib/utils';
import UserModal from './UserModal';

export default function UserTable() {
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const fetchUsers = () => {
    setLoading(true);
    setError(null);
    fetch('/api/users/page', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        pageIndex: 1,
        pageSize: 10
      })
    })
      .then(async res => {
        const text = await res.text();
        if (res.status === 503) {
          throw new Error('Java 后端正在启动，请稍候...');
        }
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}. ${text}`);
        }
        if (!text) {
          return { success: true, data: [] };
        }
        try {
          return JSON.parse(text);
        } catch (e) {
          console.error('Failed to parse JSON:', text);
          throw new Error('服务器返回了非 JSON 格式的数据');
        }
      })
      .then(res => {
        console.log('Response from backend:', res);
        if (res.success) {
          setUsers(res.data || []);
        } else {
          setError(res.errMessage || '获取用户列表失败');
        }
      })
      .catch(err => {
        console.error('Error fetching users:', err);
        setError(err.message || '网络错误，请检查后端服务');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleAddUser = (newUser: any) => {
    setLoading(true);
    fetch('/api/users', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newUser)
    })
      .then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.text().then(text => text ? JSON.parse(text) : { success: true });
      })
      .then(res => {
        if (res.success) {
          fetchUsers(); // 重新加载列表
          setIsModalOpen(false);
        } else {
          alert('添加用户失败: ' + res.errMessage);
        }
      })
      .catch(err => {
        console.error('Error adding user:', err);
        alert('添加用户失败，请检查网络');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">用户管理 (Java Backend)</h2>
          <p className="text-slate-500 mt-1">管理系统中的所有用户及其权限。</p>
        </div>
        <div className="flex items-center gap-3">
          <button 
            onClick={fetchUsers}
            disabled={loading}
            className="flex items-center gap-2 bg-white text-slate-600 border border-slate-200 px-4 py-2.5 rounded-xl font-medium hover:bg-slate-50 transition-all shadow-sm disabled:opacity-50"
          >
            刷新
          </button>
          <button 
            onClick={() => setIsModalOpen(true)}
            className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
          >
            <UserPlus size={18} />
            添加用户
          </button>
        </div>
      </div>

      <UserModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        onSave={handleAddUser} 
      />

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={16} className="text-slate-400" />
            <input type="text" placeholder="搜索用户..." className="text-sm outline-none w-full" />
          </div>
          <button className="flex items-center gap-2 px-3 py-1.5 text-sm font-medium text-slate-600 hover:bg-white hover:shadow-sm border border-transparent hover:border-slate-200 rounded-lg transition-all">
            <Filter size={16} />
            筛选
          </button>
        </div>

        <table className="w-full text-left">
          <thead>
            <tr className="bg-slate-50/50 text-slate-500 text-xs uppercase font-bold tracking-wider">
              <th className="px-6 py-4">用户信息</th>
              <th className="px-6 py-4">所属组织</th>
              <th className="px-6 py-4">角色</th>
              <th className="px-6 py-4">状态</th>
              <th className="px-6 py-4 text-right">操作</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {loading ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center">
                  <div className="flex flex-col items-center gap-3">
                    <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                    <p className="text-slate-500 text-sm">正在加载用户数据...</p>
                  </div>
                </td>
              </tr>
            ) : error ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center">
                  <div className="flex flex-col items-center gap-3">
                    <p className="text-red-500 text-sm font-medium">{error}</p>
                    <button 
                      onClick={fetchUsers}
                      className="text-blue-600 text-sm hover:underline"
                    >
                      点击重试
                    </button>
                  </div>
                </td>
              </tr>
            ) : users.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center">
                  <p className="text-slate-500 text-sm">暂无用户数据</p>
                </td>
              </tr>
            ) : (
              users.map((user) => (
                <tr key={user.id} className="hover:bg-slate-50 transition-colors group">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold">
                        {user.username ? user.username[0] : '?'}
                      </div>
                      <div>
                        <p className="text-sm font-semibold text-slate-900">{user.username}</p>
                        <p className="text-xs text-slate-500">{user.email}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2 text-slate-600">
                      <span className="text-sm">{user.orgName || '-'}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-600">{user.role}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={cn(
                      "px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider",
                      user.status === 1 ? "bg-emerald-100 text-emerald-700" : 
                      user.status === 0 ? "bg-red-100 text-red-700" : "bg-slate-100 text-slate-600"
                    )}>
                      {user.status === 1 ? '活跃' : '禁用'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <button className="p-2 text-slate-400 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-all">
                      <MoreHorizontal size={18} />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
