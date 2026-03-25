import React, { useState } from 'react';
import { Search, Plus, Menu as MenuIcon, Layout, Square, ChevronRight, ChevronDown, Trash2, Edit, MoveUp, MoveDown } from 'lucide-react';
import { cn } from '../lib/utils';

const mockMenus = [
  { id: 1, name: '概览', type: 'DIR', path: '/dashboard', icon: 'LayoutDashboard', order: 1, status: 1, children: [] },
  { id: 2, name: '核心管理', type: 'DIR', path: '/core', icon: 'Users', order: 2, status: 1, children: [
    { id: 21, name: '用户管理', type: 'MENU', path: '/core/users', icon: 'Users', order: 1, status: 1 },
    { id: 22, name: '角色管理', type: 'MENU', path: '/core/roles', icon: 'ShieldCheck', order: 2, status: 1 },
  ]},
];

export default function MenuTable() {
  const [expanded, setExpanded] = useState<number[]>([2]);

  const toggleExpand = (id: number) => {
    setExpanded(prev => prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]);
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">菜单管理</h2>
          <p className="text-slate-500 mt-1">配置系统左侧导航菜单，支持多级目录及按钮权限关联。</p>
        </div>
        <button className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
          <Plus size={18} />
          新增菜单
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input type="text" placeholder="搜索菜单名称或路径..." className="text-sm outline-none w-full" />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">菜单名称</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">类型</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">路由路径</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">排序</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">状态</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {mockMenus.map((menu) => (
                <React.Fragment key={menu.id}>
                  <tr className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        {menu.children.length > 0 ? (
                          <button onClick={() => toggleExpand(menu.id)} className="p-1 hover:bg-slate-200 rounded transition-colors">
                            {expanded.includes(menu.id) ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
                          </button>
                        ) : <div className="w-6" />}
                        <Layout size={16} className="text-blue-600" />
                        <span className="font-semibold text-slate-900">{menu.name}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="px-2 py-1 bg-slate-100 text-slate-600 text-xs font-bold rounded-md">目录</span>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-500">{menu.path}</td>
                    <td className="px-6 py-4 text-sm text-slate-500">{menu.order}</td>
                    <td className="px-6 py-4">
                      <span className="px-2 py-1 bg-emerald-50 text-emerald-600 text-xs font-bold rounded-md">启用</span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-1">
                        <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><MoveUp size={14} /></button>
                        <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><MoveDown size={14} /></button>
                        <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><Edit size={16} /></button>
                        <button className="p-2 text-slate-400 hover:text-red-600 transition-colors"><Trash2 size={16} /></button>
                      </div>
                    </td>
                  </tr>
                  {expanded.includes(menu.id) && menu.children.map((child) => (
                    <tr key={child.id} className="bg-slate-50/30 hover:bg-slate-50 transition-colors">
                      <td className="px-6 py-4 pl-14">
                        <div className="flex items-center gap-2">
                          <Square size={14} className="text-slate-400" />
                          <span className="text-sm text-slate-700">{child.name}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <span className="px-2 py-1 bg-blue-50 text-blue-600 text-xs font-bold rounded-md">菜单</span>
                      </td>
                      <td className="px-6 py-4 text-sm text-slate-500">{child.path}</td>
                      <td className="px-6 py-4 text-sm text-slate-500">{child.order}</td>
                      <td className="px-6 py-4">
                        <span className="px-2 py-1 bg-emerald-50 text-emerald-600 text-xs font-bold rounded-md">启用</span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-1">
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
