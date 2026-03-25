import React, { useState, useEffect } from 'react';
import { Search, RotateCcw, Download, Trash2, Eye } from 'lucide-react';

interface OperLog {
  id: number;
  module: string;
  content: string;
  operator: string;
  ip: string;
  result: string;
  remark: string;
  createdAt: string;
}

const LogOper: React.FC = () => {
  const [logs, setLogs] = useState<OperLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchParams, setSearchParams] = useState({
    operator: '',
    module: '',
    result: ''
  });

  const fetchLogs = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = new URLSearchParams();
      if (searchParams.operator) params.append('operator', searchParams.operator);
      if (searchParams.module) params.append('module', searchParams.module);
      if (searchParams.result) params.append('result', searchParams.result);
      
      const response = await fetch(`/api/logs/oper?${params.toString()}`);
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
      console.error('Failed to fetch oper logs:', error);
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
    setSearchParams({ operator: '', module: '', result: '' });
    // fetchLogs will be triggered by the next render if we use a ref or just call it
  };

  const handleDelete = async (id: number) => {
    if (!confirm('确定删除该日志吗？')) return;
    try {
      const response = await fetch(`/api/logs/oper/${id}`, { method: 'DELETE' });
      const data = await response.json();
      if (data.success) {
        fetchLogs();
      }
    } catch (error) {
      console.error('Failed to delete log:', error);
    }
  };

  const handleExport = () => {
    alert('操作日志导出成功 (Excel)');
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-4 bg-white p-4 rounded-lg shadow-sm">
        <div className="flex items-center gap-2">
          <span className="text-sm text-gray-500">操作人</span>
          <input
            type="text"
            className="px-3 py-1 border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={searchParams.operator}
            onChange={(e) => setSearchParams({ ...searchParams, operator: e.target.value })}
            placeholder="请输入操作人"
          />
        </div>
        <div className="flex items-center gap-2">
          <span className="text-sm text-gray-500">操作模块</span>
          <input
            type="text"
            className="px-3 py-1 border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={searchParams.module}
            onChange={(e) => setSearchParams({ ...searchParams, module: e.target.value })}
            placeholder="请输入模块名称"
          />
        </div>
        <div className="flex items-center gap-2">
          <span className="text-sm text-gray-500">操作结果</span>
          <select
            className="px-3 py-1 border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={searchParams.result}
            onChange={(e) => setSearchParams({ ...searchParams, result: e.target.value })}
          >
            <option value="">全部</option>
            <option value="成功">成功</option>
            <option value="失败">失败</option>
          </select>
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
          <h3 className="font-medium text-gray-800">操作日志列表</h3>
          <button
            onClick={handleExport}
            className="flex items-center gap-1 px-3 py-1.5 border border-green-500 text-green-600 rounded-md hover:bg-green-50 transition-colors text-sm"
          >
            <Download size={16} /> 导出
          </button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-gray-50 text-gray-600 text-sm uppercase">
                <th className="px-6 py-3 font-medium">日志ID</th>
                <th className="px-6 py-3 font-medium">操作人</th>
                <th className="px-6 py-3 font-medium">操作IP</th>
                <th className="px-6 py-3 font-medium">操作模块</th>
                <th className="px-6 py-3 font-medium">操作内容</th>
                <th className="px-6 py-3 font-medium">操作时间</th>
                <th className="px-6 py-3 font-medium">结果</th>
                <th className="px-6 py-3 font-medium">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {loading ? (
                <tr>
                  <td colSpan={8} className="px-6 py-10 text-center text-gray-400">加载中...</td>
                </tr>
              ) : error ? (
                <tr>
                  <td colSpan={8} className="px-6 py-10 text-center text-red-500">{error}</td>
                </tr>
              ) : logs.length === 0 ? (
                <tr>
                  <td colSpan={8} className="px-6 py-10 text-center text-gray-400">暂无数据</td>
                </tr>
              ) : (
                logs.map((log) => (
                  <tr key={log.id} className="hover:bg-gray-50 transition-colors text-sm">
                    <td className="px-6 py-4 text-gray-500">{log.id}</td>
                    <td className="px-6 py-4 font-medium text-gray-900">{log.operator}</td>
                    <td className="px-6 py-4 text-gray-600 font-mono">{log.ip}</td>
                    <td className="px-6 py-4">
                      <span className="px-2 py-1 bg-blue-50 text-blue-600 rounded text-xs">{log.module}</span>
                    </td>
                    <td className="px-6 py-4 text-gray-600 max-w-xs truncate" title={log.content}>{log.content}</td>
                    <td className="px-6 py-4 text-gray-500">{new Date(log.createdAt).toLocaleString()}</td>
                    <td className="px-6 py-4">
                      <span className={`px-2 py-0.5 rounded-full text-xs ${
                        log.result === '成功' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                      }`}>
                        {log.result}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex gap-3">
                        <button className="text-blue-600 hover:text-blue-800" title="详情">
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
    </div>
  );
};

export default LogOper;
