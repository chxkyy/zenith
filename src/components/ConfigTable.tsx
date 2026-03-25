import React, { useState } from 'react';
import { Search, Plus, Settings, Trash2, Edit, RefreshCw } from 'lucide-react';
import { cn } from '../lib/utils';

const mockConfigs = [
  { id: 1, name: '主页标题', key: 'sys.index.title', value: 'Zenith Enterprise Admin', type: 'SYSTEM', remark: '系统主页显示的标题' },
  { id: 2, name: '用户默认密码', key: 'sys.user.defaultPassword', value: '123456', type: 'SYSTEM', remark: '新增用户时的初始密码' },
  { id: 3, name: '验证码开关', key: 'sys.login.captchaEnabled', value: 'true', type: 'SYSTEM', remark: '登录时是否开启验证码' },
];

export default function ConfigTable() {
  const [configs] = useState(mockConfigs);

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">参数设置</h2>
          <p className="text-slate-500 mt-1">动态配置系统运行参数，无需重启服务即可生效。</p>
        </div>
        <div className="flex gap-2">
          <button className="flex items-center gap-2 bg-white border border-slate-200 text-slate-700 px-4 py-2 rounded-xl font-medium hover:bg-slate-50 transition-all">
            <RefreshCw size={18} />
            刷新缓存
          </button>
          <button className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
            <Plus size={18} />
            新增参数
          </button>
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input type="text" placeholder="搜索参数名称或键名..." className="text-sm outline-none w-full" />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">参数名称</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">参数键名</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">参数键值</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">系统内置</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">备注</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {configs.map((config) => (
                <tr key={config.id} className="hover:bg-slate-50 transition-colors">
                  <td className="px-6 py-4 text-sm font-semibold text-slate-900">{config.name}</td>
                  <td className="px-6 py-4 text-sm font-mono text-slate-600">{config.key}</td>
                  <td className="px-6 py-4 text-sm text-slate-600">{config.value}</td>
                  <td className="px-6 py-4">
                    <span className="px-2 py-1 bg-blue-50 text-blue-600 text-xs font-bold rounded-md">是</span>
                  </td>
                  <td className="px-6 py-4 text-sm text-slate-500 truncate max-w-xs">{config.remark}</td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button className="p-2 text-slate-400 hover:text-blue-600 transition-colors"><Edit size={18} /></button>
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
