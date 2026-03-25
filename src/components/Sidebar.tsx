import React from 'react';
import { 
  LayoutDashboard, 
  Users, 
  Settings, 
  BarChart3, 
  Package, 
  LogOut,
  ChevronRight,
  ShieldCheck,
  Building2,
  Key,
  Menu as MenuIcon,
  BookOpen,
  FileText,
  Database,
  Bell,
  UserCheck,
  FolderOpen,
  Activity,
  User,
  Server,
  RefreshCw
} from 'lucide-react';
import { cn } from '../lib/utils';

const menuGroups = [
  {
    title: '概览',
    items: [
      { icon: LayoutDashboard, label: '仪表盘', id: 'dashboard' },
    ]
  },
  {
    title: '核心管理',
    items: [
      { icon: Users, label: '用户管理', id: 'users' },
      { icon: ShieldCheck, label: '角色管理', id: 'roles' },
      { icon: Key, label: '权限管理', id: 'permissions' },
      { icon: MenuIcon, label: '菜单管理', id: 'menus' },
      { icon: Building2, label: '部门管理', id: 'orgs' },
    ]
  },
  {
    title: '系统运维',
    items: [
      { icon: BookOpen, label: '字典管理', id: 'dicts' },
      { icon: FileText, label: '操作日志', id: 'logs_oper' },
      { icon: FileText, label: '登录日志', id: 'logs_login' },
      { icon: FileText, label: '异常日志', id: 'logs_error' },
      { icon: Settings, label: '系统配置', id: 'config' },
      { icon: Database, label: '缓存管理', id: 'cache' },
    ]
  },
  {
    title: '辅助功能',
    items: [
      { icon: FolderOpen, label: '文件管理', id: 'files' },
      { icon: Bell, label: '通知公告', id: 'notices' },
      { icon: UserCheck, label: '在线用户', id: 'online' },
    ]
  },
  {
    title: '监控统计',
    items: [
      { icon: Activity, label: '数据监控', id: 'monitoring' },
      { icon: FileText, label: '系统日志', id: 'system_logs' },
    ]
  },
  {
    title: '个人中心',
    items: [
      { icon: User, label: '账户设置', id: 'profile' },
    ]
  }
];

interface SidebarProps {
  activeTab: string;
  setActiveTab: (id: string) => void;
}

export default function Sidebar({ activeTab, setActiveTab }: SidebarProps) {
  const [backendStatus, setBackendStatus] = React.useState<'loading' | 'online' | 'offline'>('loading');

  const checkStatus = React.useCallback(() => {
    setBackendStatus('loading');
    fetch('/api/stats/health')
      .then(res => {
        if (res.ok) setBackendStatus('online');
        else setBackendStatus('offline');
      })
      .catch(() => setBackendStatus('offline'));
  }, []);

  React.useEffect(() => {
    checkStatus();
    const timer = setInterval(checkStatus, 30000); // 每30秒检查一次
    return () => clearInterval(timer);
  }, [checkStatus]);

  return (
    <aside className="w-64 bg-white border-r border-slate-200 h-screen flex flex-col sticky top-0 overflow-y-auto custom-scrollbar">
      <div className="p-6 flex items-center gap-3 shrink-0">
        <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
          <span className="text-white font-bold text-xl">Z</span>
        </div>
        <h1 className="text-xl font-bold text-slate-900 tracking-tight">Zenith Admin</h1>
      </div>

      <nav className="flex-1 px-4 py-4 space-y-6">
        {menuGroups.map((group) => (
          <div key={group.title} className="space-y-1">
            <h3 className="px-4 text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
              {group.title}
            </h3>
            {group.items.map((item) => (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                className={cn(
                  "w-full flex items-center justify-between px-4 py-2.5 rounded-xl transition-all duration-200 group",
                  activeTab === item.id 
                    ? "bg-blue-50 text-blue-600 shadow-sm" 
                    : "text-slate-500 hover:bg-slate-50 hover:text-slate-900"
                )}
              >
                <div className="flex items-center gap-3">
                  <item.icon size={18} className={cn(
                    "transition-colors",
                    activeTab === item.id ? "text-blue-600" : "text-slate-400 group-hover:text-slate-600"
                  )} />
                  <span className="font-medium text-sm">{item.label}</span>
                </div>
                {activeTab === item.id && <ChevronRight size={14} />}
              </button>
            ))}
          </div>
        ))}
      </nav>

      <div className="p-4 border-t border-slate-100 shrink-0 space-y-2">
        <div className="px-4 py-2 rounded-xl bg-slate-50 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Server size={14} className={cn(
              backendStatus === 'online' ? "text-emerald-500" : 
              backendStatus === 'loading' ? "text-amber-500 animate-pulse" : "text-rose-500"
            )} />
            <span className="text-[10px] font-semibold text-slate-500 uppercase tracking-wider">
              后端状态: {
                backendStatus === 'online' ? "已连接" : 
                backendStatus === 'loading' ? "启动中..." : "未连接"
              }
            </span>
          </div>
          <button 
            onClick={checkStatus}
            className="p-1 hover:bg-slate-200 rounded-md transition-colors"
            title="刷新状态"
          >
            <RefreshCw size={12} className={cn(backendStatus === 'loading' && "animate-spin")} />
          </button>
        </div>
        <button className="w-full flex items-center gap-3 px-4 py-3 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all">
          <LogOut size={20} />
          <span className="font-medium">退出登录</span>
        </button>
      </div>
    </aside>
  );
}
