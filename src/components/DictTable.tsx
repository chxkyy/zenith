import React, { useState, useEffect } from 'react';
import { Search, Plus, Book, Trash2, Edit, List } from 'lucide-react';
import { cn } from '../lib/utils';

interface Dict {
  id: number;
  name: string;
  type: string;
  status: number;
  remark: string;
}

export default function DictTable() {
  const [dicts, setDicts] = useState<Dict[]>([]);
  const [loading, setLoading] = useState(true);

  // 从后端获取字典数据
  useEffect(() => {
    const fetchDicts = async () => {
      setLoading(true);
      try {
        const response = await fetch('/api/dicts');
        if (!response.ok) {
          throw new Error('Failed to fetch dicts');
        }
        const data = await response.json();
        if (data.success && data.data) {
          setDicts(data.data);
        }
      } catch (error) {
        console.error('Error fetching dicts:', error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchDicts();
  }, []);

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">字典管理</h2>
          <p className="text-slate-500 mt-1">维护系统常用的枚举数据，如性别、状态、类型等。</p>
        </div>
        <button className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
          <Plus size={18} />
          新增字典
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input type="text" placeholder="搜索字典名称或类型..." className="text-sm outline-none w-full" />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">字典名称</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">字典类型</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">状态</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">备注</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={5} className="px-6 py-12 text-center">
                    <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
                  </td>
                </tr>
              ) : dicts.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-12 text-center text-slate-500">
                    暂无字典数据
                  </td>
                </tr>
              ) : (
                dicts.map((dict) => (
                  <tr key={dict.id} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4 text-sm font-semibold text-slate-900">{dict.name}</td>
                    <td className="px-6 py-4 text-sm font-mono text-slate-600">{dict.type}</td>
                    <td className="px-6 py-4">
                      <span className={cn(
                        "px-2 py-1 text-xs font-bold rounded-md",
                        dict.status === 1 ? "bg-emerald-50 text-emerald-600" : "bg-slate-100 text-slate-500"
                      )}>
                        {dict.status === 1 ? '正常' : '禁用'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-500">{dict.remark}</td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button className="flex items-center gap-1 p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors text-xs font-bold">
                          <List size={14} />
                          数据
                        </button>
                        <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><Edit size={18} /></button>
                        <button className="p-2 text-slate-400 hover:text-red-600 transition-colors"><Trash2 size={18} /></button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
