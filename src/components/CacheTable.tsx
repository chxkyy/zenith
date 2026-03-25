import React, { useState } from 'react';
import { Search, Database, Trash2, RefreshCw, Eye, Key } from 'lucide-react';
import { cn } from '../lib/utils';

const mockCaches = [
  { id: 1, name: '用户权限缓存', key: 'sys:auth:perm:1', type: 'REDIS', size: '2.5 KB', expire: '3600s' },
  { id: 2, name: '字典数据缓存', key: 'sys:dict:all', type: 'REDIS', size: '124 KB', expire: '永久' },
  { id: 3, name: '系统配置缓存', key: 'sys:config:all', type: 'REDIS', size: '12 KB', expire: '永久' },
];

export default function CacheTable() {
  const [caches] = useState(mockCaches);

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">缓存管理</h2>
          <p className="text-slate-500 mt-1">管理系统Redis缓存数据，支持按键名查询、查看详情及清理。</p>
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
            <input type="text" placeholder="搜索缓存键名..." className="text-sm outline-none w-full" />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">缓存名称</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">缓存键名</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">类型</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">大小</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">过期时间</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {caches.map((cache) => (
                <tr key={cache.id} className="hover:bg-slate-50 transition-colors">
                  <td className="px-6 py-4 text-sm font-semibold text-slate-900">{cache.name}</td>
                  <td className="px-6 py-4 text-sm font-mono text-slate-600">
                    <div className="flex items-center gap-2">
                      <Key size={14} className="text-slate-400" />
                      {cache.key}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="px-2 py-1 bg-red-50 text-red-600 text-xs font-bold rounded-md">{cache.type}</span>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-600">{cache.size}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{cache.expire}</td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><Eye size={18} /></button>
                      <button className="p-2 text-slate-400 hover:text-red-600 transition-colors"><Trash2 size={18} /></button>
                    </div>
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
