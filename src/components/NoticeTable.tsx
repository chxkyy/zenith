import React, { useState } from 'react';
import { Search, Plus, Bell, Eye, Trash2, Edit, Send, RotateCcw, Pin } from 'lucide-react';
import { cn } from '../lib/utils';
import { motion } from 'motion/react';

const mockNotices = [
  { id: 1, title: '关于2026年清明节放假安排的通知', type: '系统通知', author: 'admin', time: '2026-03-20 09:00:00', status: '已发布', isPinned: true, readCount: 124 },
  { id: 2, title: '系统升级维护公告 (2026-03-25)', type: '业务公告', author: 'admin', time: '2026-03-22 14:30:00', status: '待发布', isPinned: false, readCount: 0 },
  { id: 3, title: '新版员工手册发布', type: '规则通知', author: 'editor', time: '2026-03-15 11:00:00', status: '已撤回', isPinned: false, readCount: 45 },
];

export default function NoticeTable() {
  const [notices, setNotices] = useState(mockNotices);

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">通知公告</h2>
          <p className="text-slate-500 mt-1">管理系统内的通知、公告及重要规则发布。</p>
        </div>
        <button className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
          <Plus size={18} />
          新增公告
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input 
              type="text" 
              placeholder="搜索公告标题..." 
              className="bg-transparent border-none outline-none text-sm w-full"
            />
          </div>
          <div className="flex items-center gap-2">
            <select className="bg-white border border-slate-200 text-sm rounded-lg px-3 py-1.5 outline-none focus:border-blue-500">
              <option value="">所有类型</option>
              <option value="system">系统通知</option>
              <option value="business">业务公告</option>
              <option value="rule">规则通知</option>
            </select>
            <select className="bg-white border border-slate-200 text-sm rounded-lg px-3 py-1.5 outline-none focus:border-blue-500">
              <option value="">所有状态</option>
              <option value="published">已发布</option>
              <option value="draft">草稿</option>
              <option value="withdrawn">已撤回</option>
            </select>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">公告标题</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">类型</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">发布人</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">发布时间</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">状态</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">阅读人数</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {notices.map((notice) => (
                <tr key={notice.id} className="hover:bg-slate-50 transition-colors group">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      {notice.isPinned && <Pin size={14} className="text-orange-500 fill-orange-500" />}
                      <span className="font-medium text-slate-900 group-hover:text-blue-600 transition-colors cursor-pointer">
                        {notice.title}
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="px-2.5 py-1 bg-slate-100 text-slate-600 text-xs font-bold rounded-md">
                      {notice.type}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-600">{notice.author}</td>
                  <td className="px-6 py-4 text-sm text-slate-600">{notice.time}</td>
                  <td className="px-6 py-4">
                    <span className={cn(
                      "px-2.5 py-1 text-xs font-bold rounded-md",
                      notice.status === '已发布' ? "bg-emerald-50 text-emerald-600" :
                      notice.status === '待发布' ? "bg-blue-50 text-blue-600" :
                      "bg-slate-100 text-slate-500"
                    )}>
                      {notice.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-600">{notice.readCount}</td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-blue-600" title="查看详情">
                        <Eye size={18} />
                      </button>
                      <button className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-blue-600" title="编辑">
                        <Edit size={18} />
                      </button>
                      {notice.status === '已发布' ? (
                        <button className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-orange-600" title="撤回">
                          <RotateCcw size={18} />
                        </button>
                      ) : (
                        <button className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-emerald-600" title="发布">
                          <Send size={18} />
                        </button>
                      )}
                      <button className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-red-600" title="删除">
                        <Trash2 size={18} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="p-4 border-t border-slate-100 flex items-center justify-between bg-slate-50/30">
          <span className="text-sm text-slate-500">共 {notices.length} 条记录</span>
          <div className="flex items-center gap-2">
            <button className="px-3 py-1 border border-slate-200 rounded-md text-sm disabled:opacity-50" disabled>上一页</button>
            <button className="px-3 py-1 bg-blue-600 text-white rounded-md text-sm">1</button>
            <button className="px-3 py-1 border border-slate-200 rounded-md text-sm">下一页</button>
          </div>
        </div>
      </div>
    </div>
  );
}
