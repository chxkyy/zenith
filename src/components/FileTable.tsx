import React, { useState } from 'react';
import { Search, Upload, File, Image, FileText, Trash2, Download, ExternalLink } from 'lucide-react';
import { cn } from '../lib/utils';

const mockFiles = [
  { id: 1, name: 'company_logo.png', type: 'IMAGE', size: '1.2 MB', path: '/uploads/logo.png', time: '2026-03-23 10:00:00' },
  { id: 2, name: 'annual_report_2025.pdf', type: 'PDF', size: '4.5 MB', path: '/uploads/report.pdf', time: '2026-03-22 15:30:00' },
  { id: 3, name: 'user_avatar_default.jpg', type: 'IMAGE', size: '45 KB', path: '/uploads/avatar.jpg', time: '2026-03-21 09:12:45' },
];

export default function FileTable() {
  const [files] = useState(mockFiles);

  const getIcon = (type: string) => {
    switch (type) {
      case 'IMAGE': return <Image size={20} className="text-blue-500" />;
      case 'PDF': return <FileText size={20} className="text-red-500" />;
      default: return <File size={20} className="text-slate-500" />;
    }
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">文件管理</h2>
          <p className="text-slate-500 mt-1">管理系统上传的所有资源文件，支持预览、下载及删除。</p>
        </div>
        <button className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
          <Upload size={18} />
          上传文件
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input type="text" placeholder="搜索文件名..." className="text-sm outline-none w-full" />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">文件名</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">大小</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">存储路径</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">上传时间</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {files.map((file) => (
                <tr key={file.id} className="hover:bg-slate-50 transition-colors">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-lg bg-slate-50 flex items-center justify-center border border-slate-100">
                        {getIcon(file.type)}
                      </div>
                      <span className="text-sm font-semibold text-slate-900">{file.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-600">{file.size}</td>
                  <td className="px-6 py-4 text-sm font-mono text-slate-500">{file.path}</td>
                  <td className="px-6 py-4 text-sm text-slate-500">{file.time}</td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><ExternalLink size={18} /></button>
                      <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><Download size={18} /></button>
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
