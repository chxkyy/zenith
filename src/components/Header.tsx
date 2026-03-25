import React from 'react';
import { Search, Bell, User } from 'lucide-react';

export default function Header() {
  return (
    <header className="h-16 bg-white border-bottom border-slate-200 px-8 flex items-center justify-between sticky top-0 z-10">
      <div className="flex items-center gap-4 bg-slate-50 px-4 py-2 rounded-xl border border-slate-200 w-96">
        <Search size={18} className="text-slate-400" />
        <input 
          type="text" 
          placeholder="搜索任何内容..." 
          className="bg-transparent border-none outline-none text-sm w-full text-slate-600 placeholder:text-slate-400"
        />
      </div>

      <div className="flex items-center gap-6">
        <button className="relative p-2 text-slate-500 hover:bg-slate-50 rounded-full transition-all">
          <Bell size={20} />
          <span className="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span>
        </button>
        
        <div className="flex items-center gap-3 pl-4 border-l border-slate-200">
          <div className="text-right">
            <p className="text-sm font-semibold text-slate-900">管理员</p>
            <p className="text-xs text-slate-500">chxkyy@gmail.com</p>
          </div>
          <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center border border-slate-200">
            <User size={20} className="text-slate-600" />
          </div>
        </div>
      </div>
    </header>
  );
}
