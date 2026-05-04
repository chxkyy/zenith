import React, { useState, useEffect, useRef } from 'react';
import { Search, Upload, File, Image, FileText, Trash2, Download, ExternalLink } from 'lucide-react';
import { cn, formatDateTime } from '../lib/utils';

interface FileItem {
  id: number;
  name: string;
  originalName: string;
  path: string;
  type: string;
  size: number;
  uploader: string;
  createdTime: string;
  updateTime: string;
  createUserId: number;
  updateUserId: number;
}

export default function FileTable() {
  const [files, setFiles] = useState<FileItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const fetchFiles = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/files/page', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          pageIndex: currentPage,
          pageSize: pageSize,
          keyword: searchKeyword
        })
      });
      if (!response.ok) {
        throw new Error('Failed to fetch files');
      }
      const data = await response.json();
      if (data.success && data.data) {
        setFiles(data.data);
        setTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      console.error('Error fetching files:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFiles();
  }, [currentPage, searchKeyword]);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch('/api/files/upload', {
        method: 'POST',
        body: formData
      });
      const data = await response.json();
      if (data.success) {
        fetchFiles();
      } else {
        alert(data.errMessage || '上传失败');
      }
    } catch (error) {
      console.error('Error uploading file:', error);
      alert('上传失败，请重试');
    }

    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('确定要删除该文件吗？')) return;

    try {
      const response = await fetch('/api/files/delete', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id })
      });
      const data = await response.json();
      if (data.success) {
        fetchFiles();
      } else {
        alert(data.errMessage || '删除失败');
      }
    } catch (error) {
      console.error('Error deleting file:', error);
      alert('删除失败，请重试');
    }
  };

  const handleDownload = async (id: number, fileName: string) => {
    try {
      const response = await fetch(`/api/files/download?id=${id}`);
      if (!response.ok) {
        throw new Error('Download failed');
      }
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Error downloading file:', error);
      alert('下载失败，请重试');
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  };

  const getIcon = (type: string) => {
    const upperType = type.toUpperCase();
    switch (upperType) {
      case 'IMAGE':
      case 'JPG':
      case 'JPEG':
      case 'PNG':
      case 'GIF':
      case 'BMP':
      case 'WEBP':
        return <Image size={20} className="text-blue-500" />;
      case 'PDF':
        return <FileText size={20} className="text-red-500" />;
      case 'DOC':
      case 'DOCX':
        return <FileText size={20} className="text-blue-600" />;
      case 'XLS':
      case 'XLSX':
        return <FileText size={20} className="text-emerald-600" />;
      case 'ZIP':
      case 'RAR':
      case '7Z':
        return <File size={20} className="text-amber-600" />;
      default:
        return <File size={20} className="text-slate-500" />;
    }
  };

  const totalPages = Math.ceil(totalCount / pageSize);

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">文件管理</h2>
          <p className="text-slate-500 mt-1">管理系统上传的所有资源文件，支持预览、下载及删除。</p>
        </div>
        <div>
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleUpload}
            style={{ display: 'none' }}
          />
          <button
            onClick={() => fileInputRef.current?.click()}
            className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
          >
            <Upload size={18} />
            上传文件
          </button>
        </div>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input
              type="text"
              placeholder="搜索文件名..."
              value={searchKeyword}
              onChange={(e) => {
                setSearchKeyword(e.target.value);
                setCurrentPage(1);
              }}
              className="bg-transparent border-none outline-none text-sm w-full"
            />
          </div>
          <span className="text-sm text-slate-500">共 {totalCount} 个文件</span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">文件名</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">大小</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">存储路径</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">创建人</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">创建时间</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">修改人</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">修改时间</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={8} className="px-6 py-12 text-center">
                    <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
                  </td>
                </tr>
              ) : files.length === 0 ? (
                <tr>
                  <td colSpan={8} className="px-6 py-12 text-center text-slate-500">
                    暂无文件
                  </td>
                </tr>
              ) : (
                files.map((file) => (
                  <tr key={file.id} className="hover:bg-slate-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-lg bg-slate-50 flex items-center justify-center border border-slate-100">
                          {getIcon(file.type)}
                        </div>
                        <span className="text-sm font-semibold text-slate-900">{file.originalName}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{formatFileSize(file.size)}</td>
                    <td className="px-6 py-4 text-sm font-mono text-slate-500">{file.path}</td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-600">{file.createUserName || '-'}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-600">{formatDateTime(file.createdTime)}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-600">{file.updateUserName || '-'}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-600">{formatDateTime(file.updateTime)}</span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-3">
                        <button
                          onClick={() => handleDownload(file.id, file.originalName)}
                          className="text-sm text-blue-600 hover:text-blue-800 font-medium"
                        >
                          下载
                        </button>
                        <button
                          onClick={() => handleDelete(file.id)}
                          className="text-sm text-red-600 hover:text-red-800 font-medium"
                        >
                          删除
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="p-4 border-t border-slate-100 flex items-center justify-between bg-slate-50/30">
            <span className="text-sm text-slate-500">
              第 {currentPage} / {totalPages} 页
            </span>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                disabled={currentPage === 1}
                className="px-3 py-1 border border-slate-200 rounded-md text-sm disabled:opacity-50 hover:bg-slate-100"
              >
                上一页
              </button>
              <button
                onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                disabled={currentPage === totalPages}
                className="px-3 py-1 border border-slate-200 rounded-md text-sm disabled:opacity-50 hover:bg-slate-100"
              >
                下一页
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
