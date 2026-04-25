import React, { useState, useEffect } from 'react';
import { MoreHorizontal, Search, ShieldCheck, Plus, Lock, Unlock, Edit, Trash2, User, Shield } from 'lucide-react';
import { cn, formatDateTime } from '../lib/utils';

interface Role {
  id: number;
  name: string;
  code: string;
  description: string;
  status: number;
  memberCount: number;
  createUserId: number;
  updateUserId: number;
  createdTime: string;
  updateTime: string;
}

export default function RoleTable() {
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);

  // 从后端获取角色数据
  useEffect(() => {
    const fetchRoles = async () => {
      setLoading(true);
      try {
        const response = await fetch('/api/roles');
        if (!response.ok) {
          throw new Error('Failed to fetch roles');
        }
        const data = await response.json();
        if (data.success && data.data) {
          setRoles(data.data);
        }
      } catch (error) {
        console.error('Error fetching roles:', error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchRoles();
  }, []);

  const handleEditRole = (role: Role) => {
    console.log('编辑角色:', role);
    // 这里可以添加编辑角色的逻辑
  };

  const handleDeleteRole = (id: number) => {
    if (window.confirm('删除后角色数据不可恢复，关联用户自动解除该角色，是否确认删除？')) {
      setLoading(true);
      fetch('/api/roles/delete', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id })
      })
        .then(res => {
          if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
          }
          return res.text().then(text => text ? JSON.parse(text) : { success: true });
        })
        .then(res => {
          if (res.success) {
            // 重新加载角色列表
            const fetchRoles = async () => {
              try {
                const response = await fetch('/api/roles');
                if (!response.ok) {
                  throw new Error('Failed to fetch roles');
                }
                const data = await response.json();
                if (data.success && data.data) {
                  setRoles(data.data);
                }
              } catch (error) {
                console.error('Error fetching roles:', error);
              }
            };
            fetchRoles();
          } else {
            alert('删除角色失败: ' + res.errMessage);
          }
        })
        .catch(err => {
          console.error('Error deleting role:', err);
          alert('删除角色失败，请检查网络');
        })
        .finally(() => {
          setLoading(false);
        });
    }
  };

  const handleChangeStatus = (id: number, currentStatus: number) => {
    const newStatus = currentStatus === 1 ? 0 : 1;
    if (window.confirm(`确定要将角色状态切换为${newStatus === 1 ? '启用' : '禁用'}吗？`)) {
      setLoading(true);
      fetch(`/api/roles/status?roleId=${id}&status=${newStatus}`, {
        method: 'POST'
      })
        .then(res => {
          if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
          }
          return res.text().then(text => text ? JSON.parse(text) : { success: true });
        })
        .then(res => {
          if (res.success) {
            // 重新加载角色列表
            const fetchRoles = async () => {
              try {
                const response = await fetch('/api/roles');
                if (!response.ok) {
                  throw new Error('Failed to fetch roles');
                }
                const data = await response.json();
                if (data.success && data.data) {
                  setRoles(data.data);
                }
              } catch (error) {
                console.error('Error fetching roles:', error);
              }
            };
            fetchRoles();
          } else {
            alert('状态切换失败: ' + res.errMessage);
          }
        })
        .catch(err => {
          console.error('Error changing status:', err);
          alert('状态切换失败，请检查网络');
        })
        .finally(() => {
          setLoading(false);
        });
    }
  };

  const handleAssignPermissions = (role: Role) => {
    console.log('分配权限:', role);
    // 这里可以添加分配权限的逻辑
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">角色管理</h2>
          <p className="text-slate-500 mt-1">配置系统角色及其关联的资源权限。</p>
        </div>
        <button className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
          <Plus size={18} />
          新增角色
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={16} className="text-slate-400" />
            <input type="text" placeholder="搜索角色名称或编码..." className="text-sm outline-none w-full" />
          </div>
        </div>

        <table className="w-full text-left">
          <thead>
            <tr className="bg-slate-50/50 text-slate-500 text-xs uppercase font-bold tracking-wider">
              <th className="px-6 py-4">角色名称</th>
              <th className="px-6 py-4">角色编码</th>
              <th className="px-6 py-4">描述</th>
              <th className="px-6 py-4">成员数</th>
              <th className="px-6 py-4">状态</th>
              <th className="px-6 py-4">创建人</th>
              <th className="px-6 py-4">创建时间</th>
              <th className="px-6 py-4">修改人</th>
              <th className="px-6 py-4">修改时间</th>
              <th className="px-6 py-4 text-right">操作</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {loading ? (
              <tr>
                <td colSpan={10} className="px-6 py-12 text-center">
                  <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
                </td>
              </tr>
            ) : roles.length === 0 ? (
              <tr>
                <td colSpan={10} className="px-6 py-12 text-center text-slate-500">
                  暂无角色数据
                </td>
              </tr>
            ) : (
              roles.map((role) => (
                <tr key={role.id} className="hover:bg-slate-50 transition-colors group">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 rounded-lg bg-indigo-50 text-indigo-600 flex items-center justify-center">
                        <ShieldCheck size={16} />
                      </div>
                      <span className="text-sm font-semibold text-slate-900">{role.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <code className="text-xs bg-slate-100 text-slate-600 px-2 py-1 rounded font-mono">
                      {role.code}
                    </code>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-500 line-clamp-1">{role.description}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-600 font-medium">{role.memberCount}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={cn(
                      "px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider flex items-center gap-1 w-fit",
                      role.status === 1 ? "bg-emerald-100 text-emerald-700" : "bg-red-100 text-red-700"
                    )}>
                      {role.status === 1 ? <Unlock size={10} /> : <Lock size={10} />}
                      {role.status === 1 ? '启用' : '禁用'}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-600">{role.createUserId || '-'}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-600">{formatDateTime(role.createdTime)}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-600">{role.updateUserId || '-'}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-600">{formatDateTime(role.updateTime)}</span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-3">
                      <button 
                        onClick={() => handleEditRole(role)}
                        className="text-sm text-blue-600 hover:text-blue-800 font-medium"
                      >
                        编辑
                      </button>
                      <button 
                        onClick={() => handleChangeStatus(role.id, role.status)}
                        className="text-sm text-purple-600 hover:text-purple-800 font-medium"
                      >
                        {role.status === 1 ? '禁用' : '启用'}
                      </button>
                      <button 
                        onClick={() => handleAssignPermissions(role)}
                        className="text-sm text-emerald-600 hover:text-emerald-800 font-medium"
                      >
                        分配权限
                      </button>
                      <button 
                        onClick={() => handleDeleteRole(role.id)}
                        className="text-sm text-red-600 hover:text-red-800 font-medium"
                      >
                        删除
                      </button>
                    </div>
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
