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
  User
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
      { icon: ShieldCheck, label: '角色管理', id: 'roles' },
      { icon: MenuIcon, label: '菜单管理', id: 'menus' },
      { icon: Building2, label: '组织管理', id: 'orgs' },
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
  isCollapsed: boolean;
}

export default function Sidebar({ activeTab, setActiveTab, isCollapsed }: SidebarProps) {
  return (
    <aside className={cn(
      "bg-white border-r border-slate-200 h-screen flex flex-col sticky top-0 overflow-y-auto custom-scrollbar transition-all duration-300",
      isCollapsed ? "w-16" : "w-64"
    )}>
      <div className={cn(
        "p-6 flex items-center shrink-0 transition-all duration-300",
        isCollapsed ? "justify-center" : "gap-3"
      )}>
        <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center shrink-0">
          <span className="text-white font-bold text-xl">Z</span>
        </div>
        {!isCollapsed && (
          <h1 className="text-xl font-bold text-slate-900 tracking-tight">Zenith Admin</h1>
        )}
      </div>

      <nav className={cn(
        "flex-1 px-4 py-4 space-y-6 transition-all duration-300",
        isCollapsed ? "px-2" : "px-4"
      )}>
        {menuGroups.map((group) => (
          <div key={group.title} className="space-y-1">
            {!isCollapsed && (
              <h3 className="px-4 text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">
                {group.title}
              </h3>
            )}
            {isCollapsed && (
              <div className="mb-2 mx-auto w-8 h-px bg-slate-200"></div>
            )}
            {group.items.map((item) => (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                title={isCollapsed ? item.label : undefined}
                className={cn(
                  "w-full flex items-center justify-between py-2.5 rounded-xl transition-all duration-200 group relative",
                  isCollapsed ? "px-2 justify-center" : "px-4",
                  activeTab === item.id 
                    ? "bg-blue-50 text-blue-600 shadow-sm" 
                    : "text-slate-500 hover:bg-slate-50 hover:text-slate-900"
                )}
              >
                <div className={cn("flex items-center", isCollapsed ? "" : "gap-3")}>
                  <item.icon size={18} className={cn(
                    "transition-colors shrink-0",
                    activeTab === item.id ? "text-blue-600" : "text-slate-400 group-hover:text-slate-600"
                  )} />
                  {!isCollapsed && (
                    <span className="font-medium text-sm">{item.label}</span>
                  )}
                </div>
                {!isCollapsed && activeTab === item.id && <ChevronRight size={14} />}
              </button>
            ))}
          </div>
        ))}
      </nav>

      <div className={cn(
        "border-t border-slate-100 shrink-0 space-y-2 transition-all duration-300",
        isCollapsed ? "p-2" : "p-4"
      )}>
        <button 
          className={cn(
            "flex items-center text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all w-full",
            isCollapsed ? "py-3 px-2 justify-center" : "gap-3 px-4 py-3"
          )}
          title={isCollapsed ? "退出登录" : undefined}
        >
          <LogOut size={20} className="shrink-0" />
          {!isCollapsed && (
            <span className="font-medium">退出登录</span>
          )}
        </button>
      </div>
    </aside>
  );
}
