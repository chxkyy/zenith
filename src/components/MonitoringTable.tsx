import React from 'react';
import { Activity, Cpu, HardDrive, Zap, RefreshCw, Server } from 'lucide-react';
import { cn } from '../lib/utils';

const metrics = [
  { label: 'CPU 使用率', value: '12.5%', status: 'success', icon: Cpu, color: 'text-blue-600', bg: 'bg-blue-50' },
  { label: '内存使用率', value: '45.2%', status: 'warning', icon: Zap, color: 'text-amber-600', bg: 'bg-amber-50' },
  { label: '磁盘使用率', value: '68.1%', status: 'warning', icon: HardDrive, color: 'text-indigo-600', bg: 'bg-indigo-50' },
  { label: '系统负载', value: '0.85', status: 'success', icon: Activity, color: 'text-emerald-600', bg: 'bg-emerald-50' },
];

export default function MonitoringTable() {
  return (
    <div className="p-8 space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">数据监控</h2>
          <p className="text-slate-500 mt-1">实时监控系统核心资源运行状态，保障服务稳定性。</p>
        </div>
        <button className="flex items-center gap-2 bg-white border border-slate-200 text-slate-700 px-4 py-2 rounded-xl font-medium hover:bg-slate-50 transition-all">
          <RefreshCw size={18} />
          刷新数据
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {metrics.map((metric, index) => (
          <div key={index} className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm space-y-4">
            <div className="flex items-center justify-between">
              <div className={cn("p-3 rounded-xl", metric.bg, metric.color)}>
                <metric.icon size={24} />
              </div>
              <span className={cn(
                "text-xs font-bold px-2 py-1 rounded-md uppercase tracking-wider",
                metric.status === 'success' ? "bg-emerald-100 text-emerald-700" : "bg-amber-100 text-amber-700"
              )}>
                {metric.status === 'success' ? '正常' : '警告'}
              </span>
            </div>
            <div>
              <p className="text-sm font-medium text-slate-500">{metric.label}</p>
              <h3 className="text-3xl font-bold text-slate-900 mt-1">{metric.value}</h3>
            </div>
            <div className="w-full bg-slate-100 h-1.5 rounded-full overflow-hidden">
              <div 
                className={cn("h-full rounded-full transition-all duration-1000", 
                  metric.status === 'success' ? "bg-emerald-500" : "bg-amber-500"
                )} 
                style={{ width: metric.value }} 
              />
            </div>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-6 border-b border-slate-100 flex items-center gap-3">
          <Server size={20} className="text-slate-400" />
          <h3 className="font-bold text-slate-900">服务器信息</h3>
        </div>
        <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-y-6 gap-x-12">
          {[
            { label: '服务器名称', value: 'Zenith-Prod-01' },
            { label: '操作系统', value: 'Linux (Ubuntu 22.04.3 LTS)' },
            { label: '服务器IP', value: '172.16.0.10' },
            { label: 'Java版本', value: 'OpenJDK 17.0.8' },
            { label: '启动时间', value: '2026-03-01 00:00:00' },
            { label: '运行时间', value: '22天 13小时 23分' },
          ].map((item, index) => (
            <div key={index} className="flex items-center justify-between py-2 border-b border-slate-50 last:border-0">
              <span className="text-sm text-slate-500 font-medium">{item.label}</span>
              <span className="text-sm text-slate-900 font-semibold">{item.value}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
