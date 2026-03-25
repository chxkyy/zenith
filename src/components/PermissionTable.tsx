import React, { useState } from 'react';
import { Search, Plus, Key, Shield, ChevronRight, ChevronDown, Trash2, Edit } from 'lucide-react';
import { cn } from '../lib/utils';

const mockPermissions = [
  { id: 1, name: '用户管理', code: 'sys:user:view', type: 'MENU', parent: null, children: [
    { id: 11, name: '新增用户', code: 'sys:user:add', type: 'BUTTON', parent: 1 },
    { id: 12, name: '编辑用户', code: 'sys:user:edit', type: 'BUTTON', parent: 1 },
    { id: 13, name: '删除用户', code: 'sys:user:delete', type: 'BUTTON', parent: 1 },
  ]},
  { id: 2, name: '角色管理', code: 'sys:role:view', type: 'MENU', parent: null, children: [
    { id: 21, name: '权限分配', code: 'sys:role:assign', type: 'BUTTON', parent: 2 },
  ]},
];

export default function PermissionTable() {
  const [expanded, setExpanded] = useState<number[]>([1, 2]);

  const toggleExpand = (id: number) => {
    setExpanded(prev => prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]);
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">权限管理</h2>
          <p className="text-slate-500 mt-1">基于RBAC模型，精细化管控菜单、按钮及接口权限。</p>
        </div>
        <button className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
          <Plus size={18} />
          新增权限
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input type="text" placeholder="搜索权限名称或标识..." className="text-sm outline-none w-full" />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">权限名称</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">权限标识</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">类型</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">创建时间</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {mockPermissions.map((perm) => (
                <React.Fragment key={perm.id}>
                  <tr className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        <button onClick={() => toggleExpand(perm.id)} className="p-1 hover:bg-slate-200 rounded transition-colors">
                          {expanded.includes(perm.id) ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
                        </button>
                        <Key size={16} className="text-blue-600" />
                        <span className="font-semibold text-slate-900">{perm.name}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm font-mono text-slate-600">{perm.code}</td>
                    <td className="px-6 py-4">
                      <span className="px-2 py-1 bg-blue-50 text-blue-600 text-xs font-bold rounded-md">菜单</span>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-500">2026-03-01</td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><Edit size={16} /></button>
                        <button className="p-2 text-slate-400 hover:text-red-600 transition-colors"><Trash2 size={16} /></button>
                      </div>
                    </td>
                  </tr>
                  {expanded.includes(perm.id) && perm.children.map((child) => (
                    <tr key={child.id} className="bg-slate-50/30 hover:bg-slate-50 transition-colors">
                      <td className="px-6 py-4 pl-14">
                        <div className="flex items-center gap-2">
                          <Shield size={14} className="text-slate-400" />
                          <span className="text-sm text-slate-700">{child.name}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-sm font-mono text-slate-500">{child.code}</td>
                      <td className="px-6 py-4">
                        <span className="px-2 py-1 bg-slate-100 text-slate-500 text-xs font-bold rounded-md">按钮</span>
                      </td>
                      <td className="px-6 py-4 text-sm text-slate-400">2026-03-01</td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><Edit size={14} /></button>
                          <button className="p-2 text-slate-400 hover:text-red-600 transition-colors"><Trash2 size={14} /></button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </React.Fragment>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
