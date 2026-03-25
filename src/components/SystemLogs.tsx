import React, { useEffect, useState, useRef } from 'react';
import { Terminal, RefreshCw, Download, Trash2 } from 'lucide-react';

export default function SystemLogs() {
  const [logs, setLogs] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [autoScroll, setAutoScroll] = useState(true);
  const logEndRef = useRef<HTMLDivElement>(null);

  const fetchLogs = () => {
    setLoading(true);
    fetch('/api/system/logs')
      .then(res => res.text())
      .then(text => {
        setLogs(text);
        setLoading(false);
      })
      .catch(err => {
        console.error('Failed to fetch logs:', err);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchLogs();
    const interval = setInterval(fetchLogs, 5000); // 每5秒自动刷新
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (autoScroll && logEndRef.current) {
      logEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [logs, autoScroll]);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 tracking-tight">系统日志</h2>
          <p className="text-slate-500 text-sm mt-1">实时查看 Java 后端启动及运行控制台输出</p>
        </div>
        <div className="flex items-center gap-3">
          <label className="flex items-center gap-2 text-sm text-slate-600 cursor-pointer">
            <input 
              type="checkbox" 
              checked={autoScroll} 
              onChange={(e) => setAutoScroll(e.target.checked)}
              className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
            />
            自动滚动
          </label>
          <button 
            onClick={fetchLogs}
            className="flex items-center gap-2 px-4 py-2 bg-white border border-slate-200 rounded-xl text-slate-600 hover:bg-slate-50 transition-all shadow-sm"
          >
            <RefreshCw size={16} className={loading ? "animate-spin" : ""} />
            刷新
          </button>
        </div>
      </div>

      <div className="bg-slate-900 rounded-2xl shadow-xl overflow-hidden border border-slate-800 flex flex-col h-[calc(100vh-250px)]">
        <div className="px-4 py-2 bg-slate-800 border-b border-slate-700 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="flex gap-1.5">
              <div className="w-3 h-3 rounded-full bg-rose-500"></div>
              <div className="w-3 h-3 rounded-full bg-amber-500"></div>
              <div className="w-3 h-3 rounded-full bg-emerald-500"></div>
            </div>
            <span className="ml-2 text-xs font-mono text-slate-400 uppercase tracking-widest">console.log</span>
          </div>
          <Terminal size={14} className="text-slate-500" />
        </div>
        
        <div className="flex-1 overflow-y-auto p-6 font-mono text-sm custom-scrollbar bg-slate-950">
          <pre className="text-slate-300 whitespace-pre-wrap break-all leading-relaxed">
            {logs || '正在等待日志输出...'}
          </pre>
          <div ref={logEndRef} />
        </div>
      </div>
      
      <div className="bg-blue-50 border border-blue-100 rounded-xl p-4 flex items-start gap-3">
        <div className="p-2 bg-blue-100 rounded-lg text-blue-600">
          <Terminal size={18} />
        </div>
        <div>
          <h4 className="text-sm font-semibold text-blue-900">提示</h4>
          <p className="text-sm text-blue-700 mt-0.5">
            Java 后端启动通常需要 30-60 秒。如果您看到 "Started AdminApplication" 字样，表示后端已完全就绪。
            日志每 5 秒自动同步一次。
          </p>
        </div>
      </div>
    </div>
  );
}
