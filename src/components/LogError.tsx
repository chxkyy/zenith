import React, { useState, useEffect } from 'react';
import { Search, RotateCcw, Trash2, Eye, Eraser } from 'lucide-react';

interface ErrorLog {
  id: number;
  module: string;
  ip: string;
  errorMsg: string;
  stackTrace: string;
  createdAt: string;
}

const LogError: React.FC = () => {
  const [logs, setLogs] = useState<ErrorLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchParams, setSearchParams] = useState({
    module: '',
    ip: ''
  });
  const [selectedLog, setSelectedLog] = useState<ErrorLog | null>(null);

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams();
      if (searchParams.module) params.append('module', searchParams.module);
      if (searchParams.ip) params.append('ip', searchParams.ip);
      
      const response = await fetch(`/api/logs/error?${params.toString()}`);
      const text = await response.text();
      if (response.status === 503) {
        throw new Error('Java 后端正在启动，请稍候...');
      }
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}. ${text}`);
      }
      if (!text) {
        setLogs([]);
        return;
      }
      try {
        const data = JSON.parse(text);
        if (data.success) {
          setLogs(data.data);
        } else {
          setError(data.errMessage || '获取日志失败');
        }
      } catch (e) {
        console.error('Failed to parse JSON:', text);
        throw new Error('服务器返回了非 JSON 格式的数据');
      }
    } catch (error: any) {
      console.error('Failed to fetch error logs:', error);
      setError(error.message || '网络错误');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, []);

  const handleSearch = () => fetchLogs();
  const handleReset = () => {
    setSearchParams({ module: '', ip: '' });
  };

  const handleDelete = async (id: number) => {
    if (!confirm('确定删除该异常日志吗？')) return;
    try {
      const response = await fetch(`/api/logs/error/${id}`, { method: 'DELETE' });
      const data = await response.json();
      if (data.success) {
        fetchLogs();
      }
    } catch (error) {
      console.error('Failed to delete log:', error);
    }
  };

  const handleClear = async () => {
    if (!confirm('确定清理3个月前的所有异常日志吗？')) return;
    try {
      const response = await fetch('/api/logs/error/clear?months=3', { method: 'POST' });
      const data = await response.json();
      if (data.success) {
        fetchLogs();
        alert('清理成功');
      }
    } catch (error) {
      console.error('Failed to clear logs:', error);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-4 bg-white p-4 rounded-lg shadow-sm">
        <div className="flex items-center gap-2">
          <span className="text-sm text-gray-500">异常模块</span>
          <input
            type="text"
            className="px-3 py-1 border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={searchParams.module}
            onChange={(e) => setSearchParams({ ...searchParams, module: e.target.value })}
            placeholder="请输入模块名称"
          />
        </div>
        <div className="flex items-center gap-2">
          <span className="text-sm text-gray-500">异常IP</span>
          <input
            type="text"
            className="px-3 py-1 border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={searchParams.ip}
            onChange={(e) => setSearchParams({ ...searchParams, ip: e.target.value })}
            placeholder="请输入异常IP"
          />
        </div>
        <div className="flex gap-2">
          <button
            onClick={handleSearch}
            className="flex items-center gap-1 px-4 py-1.5 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm"
          >
            <Search size={16} /> 查询
          </button>
          <button
            onClick={handleReset}
            className="flex items-center gap-1 px-4 py-1.5 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 transition-colors text-sm"
          >
            <RotateCcw size={16} /> 重置
          </button>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-sm overflow-hidden">
        <div className="p-4 border-b flex justify-between items-center">
          <h3 className="font-medium text-gray-800">异常日志列表</h3>
          <button
            onClick={handleClear}
            className="flex items-center gap-1 px-3 py-1.5 border border-red-500 text-red-600 rounded-md hover:bg-red-50 transition-colors text-sm"
          >
            <Eraser size={16} /> 清理日志
          </button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 text-gray-600 text-sm uppercase">
                <th className="px-6 py-3 font-medium">日志ID</th>
                <th className="px-6 py-3 font-medium">异常IP</th>
                <th className="px-6 py-3 font-medium">异常模块</th>
                <th className="px-6 py-3 font-medium">异常时间</th>
                <th className="px-6 py-3 font-medium">异常信息</th>
                <th className="px-6 py-3 font-medium">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-10 text-center text-gray-400">加载中...</td>
                </tr>
              ) : error ? (
                <tr>
                  <td colSpan={6} className="px-6 py-10 text-center text-red-500">{error}</td>
                </tr>
              ) : logs.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-10 text-center text-gray-400">暂无数据</td>
                </tr>
              ) : (
                logs.map((log) => (
                  <tr key={log.id} className="hover:bg-gray-50 transition-colors text-sm">
                    <td className="px-6 py-4 text-gray-500">{log.id}</td>
                    <td className="px-6 py-4 text-gray-600 font-mono">{log.ip}</td>
                    <td className="px-6 py-4">
                      <span className="px-2 py-1 bg-red-50 text-red-600 rounded text-xs">{log.module}</span>
                    </td>
                    <td className="px-6 py-4 text-gray-500">{new Date(log.createdAt).toLocaleString()}</td>
                    <td className="px-6 py-4 text-red-600 font-medium truncate max-w-xs" title={log.errorMsg}>
                      {log.errorMsg}
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex gap-3">
                        <button 
                          onClick={() => setSelectedLog(log)}
                          className="text-blue-600 hover:text-blue-800" 
                          title="查看详情"
                        >
                          <Eye size={18} />
                        </button>
                        <button 
                          onClick={() => handleDelete(log.id)}
                          className="text-red-600 hover:text-red-800" 
                          title="删除"
                        >
                          <Trash2 size={18} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {selectedLog && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl w-full max-w-4xl max-h-[80vh] flex flex-col">
            <div className="p-6 border-b flex justify-between items-center bg-gray-50 rounded-t-xl">
              <h3 className="text-xl font-bold text-gray-900">异常详情</h3>
              <button 
                onClick={() => setSelectedLog(null)}
                className="text-gray-400 hover:text-gray-600 transition-colors"
              >
                <RotateCcw size={24} className="rotate-45" />
              </button>
            </div>
            <div className="p-6 overflow-y-auto space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-500">异常模块</label>
                  <div className="mt-1 text-gray-900">{selectedLog.module}</div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-500">异常时间</label>
                  <div className="mt-1 text-gray-900">{new Date(selectedLog.createdAt).toLocaleString()}</div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-500">异常IP</label>
                  <div className="mt-1 text-gray-900">{selectedLog.ip}</div>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-500">异常信息</label>
                <div className="mt-1 p-3 bg-red-50 text-red-700 rounded-md font-mono text-sm border border-red-100">
                  {selectedLog.errorMsg}
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-500">堆栈信息</label>
                <pre className="mt-1 p-4 bg-gray-900 text-gray-300 rounded-md font-mono text-xs overflow-x-auto whitespace-pre-wrap leading-relaxed">
                  {selectedLog.stackTrace}
                </pre>
              </div>
            </div>
            <div className="p-6 border-t bg-gray-50 rounded-b-xl flex justify-end">
              <button
                onClick={() => setSelectedLog(null)}
                className="px-6 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors font-medium"
              >
                关闭
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default LogError;
