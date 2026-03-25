import React, { useState } from 'react';
import { MoreHorizontal, Search, ShieldCheck, Plus, Lock, Unlock } from 'lucide-react';
import { cn } from '../lib/utils';

const mockRoles = [
  { id: 1, name: '超级管理员', code: 'ROLE_ADMIN', description: '拥有系统所有权限', status: 1, memberCount: 2 },
  { id: 2, name: '运营编辑', code: 'ROLE_EDITOR', description: '负责内容发布与审核', status: 1, memberCount: 5 },
  { id: 3, name: '普通用户', code: 'ROLE_USER', description: '仅拥有基础查看权限', status: 1, memberCount: 1240 },
  { id: 4, name: '访客', code: 'ROLE_GUEST', description: '只读权限', status: 0, memberCount: 0 },
];

export default function RoleTable() {
  const [roles] = useState(mockRoles);

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
              <th className="px-6 py-4 text-right">操作</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {roles.map((role) => (
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
                <td className="px-6 py-4 text-right">
                  <button className="p-2 text-slate-400 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-all">
                    <MoreHorizontal size={18} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
