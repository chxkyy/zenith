import React, { useState, useEffect } from 'react';
import { Search, Plus, Bell, Eye, Trash2, Edit, Send, RotateCcw, Pin, X, Check, AlertTriangle } from 'lucide-react';
import { cn, formatDateTime } from '../lib/utils';
import Notification from './Notification';

interface Notice {
  id: number;
  title: string;
  type: string;
  author: string;
  time: string;
  status: string;
  statusName: string;
  isPinned: boolean;
  readCount: number;
  content?: string;
  remark?: string;
  createUserId: number;
  updateUserId: number;
  createdTime: string;
  updateTime: string;
}

interface NoticeForm {
  id: number | null;
  title: string;
  type: string;
  author: string;
  content: string;
  status: string;
  remark: string;
}

export default function NoticeTable() {
  const [notices, setNotices] = useState<Notice[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchParams, setSearchParams] = useState({
    keyword: '',
    type: '',
    status: ''
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  
  // 模态框状态
  const [modalType, setModalType] = useState<'add' | 'edit' | 'delete' | 'view' | null>(null);
  const [currentNotice, setCurrentNotice] = useState<Notice | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  // 表单数据
  const [formData, setFormData] = useState<NoticeForm>({
    id: null,
    title: '',
    type: 'system',
    author: 'admin',
    content: '',
    status: '0',
    remark: ''
  });
  
  // 通知状态
  const [notification, setNotification] = useState<{
    message: string;
    type: 'success' | 'error' | 'info';
    key: number;
  } | null>(null);

  // 从后端获取通知数据
  const fetchNotices = async () => {
    setLoading(true);
    try {
      const query = {
        pageIndex: currentPage,
        pageSize: pageSize,
        keyword: searchParams.keyword,
        type: searchParams.type,
        status: searchParams.status
      };
      
      const response = await fetch('/api/notices/page', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(query)
      });
      if (!response.ok) {
        throw new Error('Failed to fetch notices');
      }
      const data = await response.json();
      if (data.success && data.data) {
        // 转换后端返回的数据格式
        const formattedNotices = data.data.map((notice: any) => ({
          id: notice.id,
          title: notice.title,
          type: notice.type,
          author: notice.author,
          time: formatDateTime(notice.createdTime || notice.createdAt || notice.time),
          status: notice.status,
          statusName: notice.statusName,
          isPinned: notice.isPinned || false,
          readCount: notice.readCount || 0,
          content: notice.content,
          remark: notice.remark,
          createUserId: notice.createUserId,
          updateUserId: notice.updateUserId,
          createdTime: notice.createdTime,
          updateTime: notice.updateTime
        }));
        setNotices(formattedNotices);
        setTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      console.error('Error fetching notices:', error);
    } finally {
      setLoading(false);
    }
  };

  // 当分页或搜索参数变化时，重新获取数据
  useEffect(() => {
    fetchNotices();
  }, [currentPage, pageSize, searchParams]);

  // 处理搜索参数变化
  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setSearchParams(prev => ({
      ...prev,
      [name]: value
    }));
    setCurrentPage(1); // 搜索时重置到第一页
  };

  // 处理表单输入变化
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // 打开新增公告模态框
  const handleAddNotice = () => {
    setFormData({
      id: null,
      title: '',
      type: 'system',
      author: 'admin',
      content: '',
      status: '0',
      remark: ''
    });
    setModalType('add');
    setIsModalOpen(true);
  };

  // 打开编辑公告模态框
  const handleEditNotice = (notice: Notice) => {
    setCurrentNotice(notice);
    setFormData({
      id: notice.id,
      title: notice.title,
      type: notice.type,
      author: notice.author,
      content: notice.content || '',
      status: notice.status,
      remark: notice.remark || ''
    });
    setModalType('edit');
    setIsModalOpen(true);
  };

  // 打开查看详情模态框
  const handleViewNotice = (notice: Notice) => {
    setCurrentNotice(notice);
    setModalType('view');
    setIsModalOpen(true);
  };

  // 打开删除公告模态框
  const handleDeleteNotice = (notice: Notice) => {
    setCurrentNotice(notice);
    setModalType('delete');
    setIsModalOpen(true);
  };

  // 关闭模态框
  const handleCloseModal = () => {
    setIsModalOpen(false);
    setModalType(null);
    setCurrentNotice(null);
  };

  // 保存公告
  const handleSaveNotice = async () => {
    try {
      const url = formData.id ? '/api/notices' : '/api/notices';
      const method = formData.id ? 'POST' : 'POST';
      
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });
      
      if (!response.ok) {
        throw new Error('Failed to save notice');
      }
      
      const data = await response.json();
      if (data.success) {
        handleCloseModal();
        fetchNotices();
        setNotification({
          message: formData.id ? '公告编辑成功' : '公告新增成功',
          type: 'success',
          key: Date.now()
        });
      } else {
        setNotification({
          message: data.errMessage || '保存失败',
          type: 'error',
          key: Date.now()
        });
      }
    } catch (error) {
      console.error('Error saving notice:', error);
      setNotification({
        message: '保存失败，请重试',
        type: 'error',
        key: Date.now()
      });
    }
  };

  // 删除公告
  const handleDeleteNoticeConfirm = async () => {
    if (!currentNotice) return;
    
    try {
      const response = await fetch(`/api/notices/delete`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: currentNotice.id })
      });
      
      if (!response.ok) {
        throw new Error('Failed to delete notice');
      }
      
      const data = await response.json();
      if (data.success) {
        handleCloseModal();
        fetchNotices();
        setNotification({
          message: '公告删除成功',
          type: 'success',
          key: Date.now()
        });
      } else {
        setNotification({
          message: data.errMessage || '删除失败',
          type: 'error',
          key: Date.now()
        });
      }
    } catch (error) {
      console.error('Error deleting notice:', error);
      setNotification({
        message: '删除失败，请重试',
        type: 'error',
        key: Date.now()
      });
    }
  };

  // 发布/撤回公告
  const handlePublishNotice = async (notice: Notice) => {
    try {
      const newStatus = notice.status === '1' ? '0' : '1';
      const response = await fetch(`/api/notices/status`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ 
          id: notice.id, 
          status: newStatus 
        })
      });
      
      if (!response.ok) {
        throw new Error('Failed to update notice status');
      }
      
      const data = await response.json();
      if (data.success) {
        fetchNotices();
        setNotification({
          message: notice.status === '1' ? '公告已撤回' : '公告已发布',
          type: 'success',
          key: Date.now()
        });
      } else {
        setNotification({
          message: data.errMessage || '操作失败',
          type: 'error',
          key: Date.now()
        });
      }
    } catch (error) {
      console.error('Error updating notice status:', error);
      setNotification({
        message: '操作失败，请重试',
        type: 'error',
        key: Date.now()
      });
    }
  };

  // 计算总页数
  const totalPages = Math.ceil(totalCount / pageSize);

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">通知公告</h2>
          <p className="text-slate-500 mt-1">管理系统内的通知、公告及重要规则发布。</p>
        </div>
        <button 
          className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
          onClick={handleAddNotice}
        >
          <Plus size={18} />
          新增公告
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={18} className="text-slate-400" />
            <input 
              type="text" 
              name="keyword"
              value={searchParams.keyword}
              onChange={handleSearchChange}
              placeholder="搜索公告标题..." 
              className="bg-transparent border-none outline-none text-sm w-full"
            />
          </div>
          <div className="flex items-center gap-2">
            <select 
              name="type"
              value={searchParams.type}
              onChange={handleSearchChange}
              className="bg-white border border-slate-200 text-sm rounded-lg px-3 py-1.5 outline-none focus:border-blue-500"
            >
              <option value="">所有类型</option>
              <option value="system">系统通知</option>
              <option value="business">业务公告</option>
              <option value="rule">规则通知</option>
            </select>
            <select 
              name="status"
              value={searchParams.status}
              onChange={handleSearchChange}
              className="bg-white border border-slate-200 text-sm rounded-lg px-3 py-1.5 outline-none focus:border-blue-500"
            >
              <option value="">所有状态</option>
              <option value="1">已发布</option>
              <option value="0">草稿</option>
            </select>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">公告标题</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">类型</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">发布人</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">状态</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">阅读人数</th>
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
                  <td colSpan={10} className="px-6 py-12 text-center">
                    <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
                  </td>
                </tr>
              ) : notices.length === 0 ? (
                <tr>
                  <td colSpan={10} className="px-6 py-12 text-center text-slate-500">
                    暂无通知数据
                  </td>
                </tr>
              ) : (
                notices.map((notice) => (
                  <tr key={notice.id} className="hover:bg-slate-50 transition-colors group">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        {notice.isPinned && <Pin size={14} className="text-orange-500 fill-orange-500" />}
                        <span className="font-medium text-slate-900 group-hover:text-blue-600 transition-colors cursor-pointer">
                          {notice.title}
                        </span>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="px-2.5 py-1 bg-slate-100 text-slate-600 text-xs font-bold rounded-md">
                        {notice.type === 'system' ? '系统通知' : 
                         notice.type === 'business' ? '业务公告' : '规则通知'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{notice.author}</td>
                    <td className="px-6 py-4">
                      <span className={cn(
                        "px-2.5 py-1 text-xs font-bold rounded-md",
                        notice.status === '1' ? "bg-emerald-50 text-emerald-600" :
                        notice.status === '0' ? "bg-blue-50 text-blue-600" :
                        "bg-slate-100 text-slate-500"
                      )}>
                        {notice.statusName}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{notice.readCount}</td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-600">{notice.createUserId || '-'}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-600">{formatDateTime(notice.createdTime)}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-600">{notice.updateUserId || '-'}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-600">{formatDateTime(notice.updateTime)}</span>
                    </td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button 
                          className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-blue-600"
                          title="查看详情"
                          onClick={() => handleViewNotice(notice)}
                        >
                          <Eye size={18} />
                        </button>
                        <button 
                          className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-blue-600"
                          title="编辑"
                          onClick={() => handleEditNotice(notice)}
                        >
                          <Edit size={18} />
                        </button>
                        {notice.status === '1' ? (
                          <button 
                            className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-orange-600"
                            title="撤回"
                            onClick={() => handlePublishNotice(notice)}
                          >
                            <RotateCcw size={18} />
                          </button>
                        ) : (
                          <button 
                            className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-emerald-600"
                            title="发布"
                            onClick={() => handlePublishNotice(notice)}
                          >
                            <Send size={18} />
                          </button>
                        )}
                        <button 
                          className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-red-600"
                          title="删除"
                          onClick={() => handleDeleteNotice(notice)}
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

        <div className="p-4 border-t border-slate-100 flex items-center justify-between bg-slate-50/30">
          <span className="text-sm text-slate-500">共 {totalCount} 条记录</span>
          <div className="flex items-center gap-2">
            <button 
              className="px-3 py-1 border border-slate-200 rounded-md text-sm disabled:opacity-50"
              disabled={currentPage === 1}
              onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
            >
              上一页
            </button>
            <button className="px-3 py-1 bg-blue-600 text-white rounded-md text-sm">
              {currentPage}
            </button>
            <button 
              className="px-3 py-1 border border-slate-200 rounded-md text-sm disabled:opacity-50"
              disabled={currentPage === totalPages || totalPages === 0}
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
            >
              下一页
            </button>
          </div>
        </div>
      </div>

      {/* 模态框 */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-xl max-w-2xl w-full max-h-[80vh] overflow-y-auto">
            <div className="p-6 border-b border-slate-100 flex items-center justify-between">
              <h3 className="text-lg font-bold text-slate-900">
                {modalType === 'add' && '新增公告'}
                {modalType === 'edit' && '编辑公告'}
                {modalType === 'delete' && '删除公告'}
                {modalType === 'view' && '查看详情'}
              </h3>
              <button 
                className="p-2 hover:bg-slate-100 rounded-lg transition-colors"
                onClick={handleCloseModal}
              >
                <X size={20} className="text-slate-400" />
              </button>
            </div>
            
            {modalType === 'add' || modalType === 'edit' ? (
              <div className="p-6 space-y-4">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">公告标题 *</label>
                  <input 
                    type="text" 
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    required
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">公告类型 *</label>
                  <select 
                    name="type"
                    value={formData.type}
                    onChange={handleInputChange}
                    required
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="system">系统通知</option>
                    <option value="business">业务公告</option>
                    <option value="rule">规则通知</option>
                  </select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">发布人 *</label>
                  <input 
                    type="text" 
                    name="author"
                    value={formData.author}
                    onChange={handleInputChange}
                    required
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">公告内容 *</label>
                  <textarea 
                    name="content"
                    value={formData.content}
                    onChange={handleInputChange}
                    required
                    rows={6}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  ></textarea>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">状态 *</label>
                  <select 
                    name="status"
                    value={formData.status}
                    onChange={handleInputChange}
                    required
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="0">草稿</option>
                    <option value="1">已发布</option>
                  </select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-1">备注</label>
                  <textarea 
                    name="remark"
                    value={formData.remark}
                    onChange={handleInputChange}
                    rows={3}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  ></textarea>
                </div>
              </div>
            ) : modalType === 'view' && currentNotice ? (
              <div className="p-6 space-y-4">
                <div className="border-b border-slate-100 pb-4">
                  <h4 className="text-xl font-bold text-slate-900 mb-2">{currentNotice.title}</h4>
                  <div className="flex items-center gap-4 text-sm text-slate-500">
                    <span>类型：{currentNotice.type === 'system' ? '系统通知' : 
                           currentNotice.type === 'business' ? '业务公告' : '规则通知'}</span>
                    <span>发布人：{currentNotice.author}</span>
                    <span>发布时间：{currentNotice.time}</span>
                    <span className={cn(
                      "px-2 py-0.5 text-xs font-bold rounded",
                      currentNotice.status === '1' ? "bg-emerald-50 text-emerald-600" :
                      currentNotice.status === '0' ? "bg-blue-50 text-blue-600" :
                      "bg-slate-100 text-slate-500"
                    )}>
                      {currentNotice.statusName}
                    </span>
                  </div>
                </div>
                
                <div>
                  <h5 className="text-sm font-medium text-slate-700 mb-2">公告内容</h5>
                  <div className="p-4 bg-slate-50 rounded-lg border border-slate-100">
                    {currentNotice.content || '无内容'}
                  </div>
                </div>
                
                {currentNotice.remark && (
                  <div>
                    <h5 className="text-sm font-medium text-slate-700 mb-2">备注</h5>
                    <div className="p-4 bg-slate-50 rounded-lg border border-slate-100">
                      {currentNotice.remark}
                    </div>
                  </div>
                )}
              </div>
            ) : modalType === 'delete' && currentNotice ? (
              <div className="p-6 space-y-4">
                <div className="flex items-start gap-3">
                  <AlertTriangle size={24} className="text-orange-500 mt-1 flex-shrink-0" />
                  <div>
                    <h4 className="text-lg font-medium text-slate-900 mb-2">确认删除</h4>
                    <p className="text-slate-600">
                      您确定要删除公告 "{currentNotice.title}" 吗？此操作不可撤销。
                    </p>
                  </div>
                </div>
              </div>
            ) : null}
            
            <div className="p-6 border-t border-slate-100 flex items-center justify-end gap-3">
              {modalType === 'add' || modalType === 'edit' ? (
                <>
                  <button 
                    className="px-4 py-2 border border-slate-200 rounded-lg font-medium hover:bg-slate-50 transition-colors"
                    onClick={handleCloseModal}
                  >
                    取消
                  </button>
                  <button 
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors"
                    onClick={handleSaveNotice}
                  >
                    保存
                  </button>
                </>
              ) : modalType === 'delete' ? (
                <>
                  <button 
                    className="px-4 py-2 border border-slate-200 rounded-lg font-medium hover:bg-slate-50 transition-colors"
                    onClick={handleCloseModal}
                  >
                    取消
                  </button>
                  <button 
                    className="px-4 py-2 bg-red-600 text-white rounded-lg font-medium hover:bg-red-700 transition-colors"
                    onClick={handleDeleteNoticeConfirm}
                  >
                    确认删除
                  </button>
                </>
              ) : modalType === 'view' ? (
                <button 
                  className="px-4 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors"
                  onClick={handleCloseModal}
                >
                  关闭
                </button>
              ) : null}
            </div>
          </div>
        </div>
      )}

      {/* 通知组件 */}
      {notification && (
        <Notification 
          key={notification.key}
          message={notification.message} 
          type={notification.type} 
        />
      )}
    </div>
  );
}
