import React, { useEffect, useState } from 'react';
import { 
  AreaChart, 
  Area, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer 
} from 'recharts';
import { Users, ShoppingBag, Activity } from 'lucide-react';
import StatsCard from './StatsCard';

export default function Dashboard() {
  const [stats, setStats] = useState<any>(null);

  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch('/api/stats')
      .then(async res => {
        const text = await res.text();
        if (res.status === 503) {
          throw new Error('Java 后端正在启动，请稍候...');
        }
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}. ${text}`);
        }
        if (!text) {
          throw new Error('服务器返回了空响应');
        }
        try {
          return JSON.parse(text);
        } catch (e) {
          console.error('Failed to parse JSON:', text);
          throw new Error('服务器返回了非 JSON 格式的数据');
        }
      })
      .then(data => {
        if (data.success) {
          setStats(data);
        } else {
          setError(data.errMessage || '获取统计数据失败');
        }
      })
      .catch(err => {
        console.error('Error fetching stats:', err);
        setError(err.message || '网络错误');
      });
  }, []);

  if (error) return (
    <div className="p-8 flex flex-col items-center justify-center h-[calc(100vh-64px)] text-slate-500">
      <p className="text-red-500 font-medium mb-4">{error}</p>
      <button 
        onClick={() => window.location.reload()}
        className="text-blue-600 hover:underline"
      >
        刷新页面重试
      </button>
    </div>
  );

  if (!stats || !stats.success) return (
    <div className="p-8 flex flex-col items-center justify-center h-[calc(100vh-64px)] text-slate-500">
      <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"></div>
      <p>正在加载统计数据...</p>
    </div>
  );

  const data = stats.data;

  return (
    <div className="p-8 space-y-8">
      <div className="flex items-end justify-between">
        <div>
          <h2 className="text-3xl font-bold text-slate-900">欢迎回来, 管理员</h2>
          <p className="text-slate-500 mt-1">系统已准备就绪。后端已升级为阿里 COLA 分层架构。</p>
        </div>
        <button className="bg-blue-600 text-white px-6 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
          导出报告
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatsCard 
          title="总用户数" 
          value={data.totalUsers.toLocaleString()} 
          change={12.5} 
          icon={Users} 
          color="bg-blue-600"
        />
        <StatsCard 
          title="系统角色" 
          value={data.totalRoles.toLocaleString()} 
          change={0} 
          icon={ShoppingBag} 
          color="bg-purple-600"
        />
        <StatsCard 
          title="操作日志" 
          value={data.operLogs.toLocaleString()} 
          change={5.4} 
          icon={Activity} 
          color="bg-amber-600"
        />
        <StatsCard 
          title="异常日志" 
          value={data.errorLogs.toLocaleString()} 
          change={-10.2} 
          icon={Activity} 
          color="bg-red-600"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-8">
            <h3 className="text-lg font-bold text-slate-900">收入趋势</h3>
            <select className="bg-slate-50 border border-slate-200 rounded-lg px-3 py-1.5 text-sm outline-none">
              <option>最近 6 个月</option>
              <option>最近 12 个月</option>
            </select>
          </div>
          <div className="h-[300px] w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={data.chartData}>
                <defs>
                  <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.1}/>
                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#f1f5f9" />
                <XAxis 
                  dataKey="name" 
                  axisLine={false} 
                  tickLine={false} 
                  tick={{ fill: '#64748b', fontSize: 12 }}
                  dy={10}
                />
                <YAxis 
                  axisLine={false} 
                  tickLine={false} 
                  tick={{ fill: '#64748b', fontSize: 12 }}
                />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#fff', 
                    borderRadius: '12px', 
                    border: '1px solid #e2e8f0',
                    boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'
                  }} 
                />
                <Area 
                  type="monotone" 
                  dataKey="value" 
                  stroke="#3b82f6" 
                  strokeWidth={3}
                  fillOpacity={1} 
                  fill="url(#colorValue)" 
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
          <h3 className="text-lg font-bold text-slate-900 mb-6">最近动态</h3>
          <div className="space-y-6">
            {[1, 2, 3, 4].map((i) => (
              <div key={i} className="flex gap-4">
                <div className="w-10 h-10 rounded-full bg-slate-100 flex-shrink-0 flex items-center justify-center">
                  <Users size={18} className="text-slate-600" />
                </div>
                <div>
                  <p className="text-sm font-medium text-slate-900">新用户注册</p>
                  <p className="text-xs text-slate-500 mt-0.5">用户 ID #120{i} 刚刚加入了系统。</p>
                  <p className="text-[10px] text-slate-400 mt-1 uppercase font-bold tracking-wider">2 分钟前</p>
                </div>
              </div>
            ))}
          </div>
          <button className="w-full mt-8 py-2.5 text-sm font-semibold text-blue-600 hover:bg-blue-50 rounded-xl transition-all">
            查看全部动态
          </button>
        </div>
      </div>
    </div>
  );
}
