import React, { useState } from 'react';
import { User, Shield, History, LogOut, Monitor, MapPin, Clock, Key, Mail, Phone, Building } from 'lucide-react';
import { motion } from 'motion/react';
import { cn } from '../lib/utils';

const tabs = [
  { id: 'info', label: '个人信息', icon: User },
  { id: 'security', label: '账户安全', icon: Shield },
  { id: 'logs', label: '操作记录', icon: History },
];

export default function Profile() {
  const [activeTab, setActiveTab] = useState('info');

  const renderTabContent = () => {
    switch (activeTab) {
      case 'info':
        return (
          <div className="space-y-8">
            <div className="flex items-center gap-6">
              <div className="w-24 h-24 bg-blue-100 rounded-2xl flex items-center justify-center text-blue-600">
                <User size={48} />
              </div>
              <div>
                <h3 className="text-2xl font-bold text-slate-900">admin</h3>
                <p className="text-slate-500">超级管理员</p>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-4">
                <div className="flex items-center gap-3 text-slate-600">
                  <Mail size={18} />
                  <span className="font-medium">电子邮箱:</span>
                  <span>admin@example.com</span>
                </div>
                <div className="flex items-center gap-3 text-slate-600">
                  <Phone size={18} />
                  <span className="font-medium">手机号码:</span>
                  <span>13800138000</span>
                </div>
                <div className="flex items-center gap-3 text-slate-600">
                  <Building size={18} />
                  <span className="font-medium">所属部门:</span>
                  <span>Zenith 集团总部</span>
                </div>
              </div>
              <div className="space-y-4">
                <div className="flex items-center gap-3 text-slate-600">
                  <Clock size={18} />
                  <span className="font-medium">创建时间:</span>
                  <span>2026-03-01 10:00:00</span>
                </div>
                <div className="flex items-center gap-3 text-slate-600">
                  <MapPin size={18} />
                  <span className="font-medium">最后登录IP:</span>
                  <span>127.0.0.1</span>
                </div>
              </div>
            </div>

            <div className="pt-6 border-t border-slate-100">
              <button className="px-6 py-2.5 bg-blue-600 text-white rounded-xl font-semibold hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
                编辑个人信息
              </button>
            </div>
          </div>
        );
      case 'security':
        return (
          <div className="space-y-8">
            <div className="bg-slate-50 p-6 rounded-2xl border border-slate-100 flex items-center justify-between">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-white rounded-xl border border-slate-200 text-slate-600">
                  <Key size={24} />
                </div>
                <div>
                  <h4 className="font-bold text-slate-900">登录密码</h4>
                  <p className="text-sm text-slate-500">建议定期修改密码以保障账户安全</p>
                </div>
              </div>
              <button className="px-4 py-2 bg-white border border-slate-200 rounded-lg font-semibold text-slate-700 hover:bg-slate-50 transition-all">
                修改密码
              </button>
            </div>

            <div className="space-y-4">
              <h4 className="font-bold text-slate-900 flex items-center gap-2">
                <Monitor size={20} />
                当前在线会话
              </h4>
              <div className="border border-slate-200 rounded-2xl overflow-hidden">
                <table className="w-full text-left">
                  <thead className="bg-slate-50 border-b border-slate-200">
                    <tr>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">登录时间</th>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">IP地址</th>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">设备</th>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">状态</th>
                      <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase text-right">操作</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    <tr>
                      <td className="px-6 py-4 text-sm text-slate-600">2026-03-23 13:23:53</td>
                      <td className="px-6 py-4 text-sm text-slate-600">127.0.0.1</td>
                      <td className="px-6 py-4 text-sm text-slate-600">Chrome / macOS</td>
                      <td className="px-6 py-4">
                        <span className="px-2 py-1 bg-emerald-50 text-emerald-600 text-xs font-bold rounded-md">当前在线</span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <button className="text-slate-400 hover:text-red-600 transition-colors">
                          <LogOut size={18} />
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        );
      case 'logs':
        return (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h4 className="font-bold text-slate-900">最近操作记录</h4>
              <button className="text-sm text-blue-600 font-medium hover:underline">导出记录</button>
            </div>
            <div className="border border-slate-200 rounded-2xl overflow-hidden">
              <table className="w-full text-left">
                <thead className="bg-slate-50 border-b border-slate-200">
                  <tr>
                    <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">时间</th>
                    <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">模块</th>
                    <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">操作内容</th>
                    <th className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase">结果</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {[
                    { time: '2026-03-23 13:15:20', module: '用户管理', content: '新增用户: test_user', status: '成功' },
                    { time: '2026-03-23 13:10:05', module: '系统配置', content: '修改站点标题', status: '成功' },
                    { time: '2026-03-23 12:45:12', module: '账户安全', content: '登录系统', status: '成功' },
                  ].map((log, i) => (
                    <tr key={i}>
                      <td className="px-6 py-4 text-sm text-slate-600">{log.time}</td>
                      <td className="px-6 py-4 text-sm text-slate-600">{log.module}</td>
                      <td className="px-6 py-4 text-sm text-slate-600">{log.content}</td>
                      <td className="px-6 py-4">
                        <span className="px-2 py-1 bg-emerald-50 text-emerald-600 text-xs font-bold rounded-md">{log.status}</span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="p-8 max-w-5xl mx-auto space-y-8">
      <div>
        <h2 className="text-3xl font-bold text-slate-900">个人中心</h2>
        <p className="text-slate-500 mt-2">管理您的个人信息、账户安全及查看操作历史。</p>
      </div>

      <div className="flex gap-2 p-1 bg-slate-100 rounded-2xl w-fit">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={cn(
              "flex items-center gap-2 px-6 py-2.5 rounded-xl font-semibold transition-all",
              activeTab === tab.id
                ? "bg-white text-blue-600 shadow-sm"
                : "text-slate-500 hover:text-slate-700"
            )}
          >
            <tab.icon size={18} />
            {tab.label}
          </button>
        ))}
      </div>

      <motion.div
        key={activeTab}
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm"
      >
        {renderTabContent()}
      </motion.div>
    </div>
  );
}
