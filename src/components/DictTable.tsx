import React, { useState, useEffect } from 'react';
import { Search, Plus, Book, Trash2, Edit, List, ChevronLeft, ChevronRight, X, Check, AlertTriangle } from 'lucide-react';
import { cn } from '../lib/utils';

// 字典类型接口
interface DictType {
  id: number;
  name: string;
  type: string;
  status: number;
  remark: string;
  createdAt: string;
}

// 字典项接口
interface DictItem {
  id: number;
  type: string;
  label: string;
  dictValue: string;
  sort: number;
  status: number;
  remark: string;
}

// 模态框状态接口
interface ModalState {
  isOpen: boolean;
  type: 'add' | 'edit' | 'delete';
  data?: DictType | DictItem;
}

// 搜索参数接口
interface SearchParams {
  keyword: string;
  dictType: string;
}

export default function DictTable() {
  // 字典类型相关状态
  const [dictTypes, setDictTypes] = useState<DictType[]>([]);
  const [selectedDictType, setSelectedDictType] = useState<DictType | null>(null);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);

  // 字典项相关状态
  const [dictItems, setDictItems] = useState<DictItem[]>([]);
  const [itemsLoading, setItemsLoading] = useState(false);
  const [itemsCurrentPage, setItemsCurrentPage] = useState(1);
  const [itemsPageSize, setItemsPageSize] = useState(10);
  const [itemsTotalCount, setItemsTotalCount] = useState(0);

  // 搜索参数状态
  const [searchParams, setSearchParams] = useState<SearchParams>({
    keyword: '',
    dictType: ''
  });

  // 模态框状态
  const [dictTypeModal, setDictTypeModal] = useState<ModalState>({
    isOpen: false,
    type: 'add'
  });
  const [dictItemModal, setDictItemModal] = useState<ModalState>({
    isOpen: false,
    type: 'add'
  });

  // 表单数据状态
  const [dictTypeForm, setDictTypeForm] = useState({
    name: '',
    type: '',
    status: 1,
    remark: ''
  });
  const [dictItemForm, setDictItemForm] = useState({
    type: '',
    label: '',
    dictValue: '',
    sort: 1,
    status: 1,
    remark: ''
  });

  // 从后端获取字典类型数据（带分页和搜索）
  useEffect(() => {
    const fetchDictTypes = async () => {
      setLoading(true);
      try {
        const params = new URLSearchParams({
          pageIndex: currentPage.toString(),
          pageSize: pageSize.toString()
        });
        if (searchParams.keyword) {
          params.append('keyword', searchParams.keyword);
        }
        const response = await fetch(`/api/dicts/page?${params.toString()}`);
        if (!response.ok) {
          throw new Error('Failed to fetch dict types');
        }
        const data = await response.json();
        if (data.success && data.data) {
          setDictTypes(data.data);
          setTotalCount(data.totalCount || 0);
        }
      } catch (error) {
        console.error('Error fetching dict types:', error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchDictTypes();
  }, [currentPage, pageSize, searchParams]);

  // 处理字典类型删除前检查
  const handleDictTypeDeleteCheck = async () => {
    if (!dictTypeModal.data) return;
    try {
      // 检查是否有字典项
      const response = await fetch(`/api/dict/items/list?` + new URLSearchParams({ type: dictTypeModal.data.type }));
      if (!response.ok) {
        throw new Error('Failed to check dict items');
      }
      const data = await response.json();
      if (data.success && data.data && data.data.length > 0) {
        // 有字典项，提示用户
        alert('该字典类型下存在字典项，请先删除所有字典项后再删除类型');
        return false;
      }
      // 没有字典项，执行删除
      handleDictTypeDelete();
      return true;
    } catch (error) {
      console.error('Error checking dict items:', error);
      return false;
    }
  };

  // 当选择字典类型时，获取字典项数据
  useEffect(() => {
    if (selectedDictType) {
      setSearchParams(prev => ({
        ...prev,
        dictType: selectedDictType.type
      }));
      fetchDictItems(selectedDictType.type, itemsCurrentPage, itemsPageSize);
    } else {
      setDictItems([]);
      setItemsTotalCount(0);
    }
  }, [selectedDictType, itemsCurrentPage, itemsPageSize]);

  // 从后端获取字典项数据（带分页和搜索）
  const fetchDictItems = async (type: string, pageIndex: number, pageSize: number) => {
    setItemsLoading(true);
    try {
      const url = new URL(`/api/dict/items/page?type=${type}&pageIndex=${pageIndex}&pageSize=${pageSize}`);
      if (searchParams.keyword) {
        url.searchParams.append('keyword', searchParams.keyword);
      }
      const response = await fetch(url.toString());
      if (!response.ok) {
        throw new Error('Failed to fetch dict items');
      }
      const data = await response.json();
      if (data.success && data.data) {
        setDictItems(data.data);
        setItemsTotalCount(data.totalCount || 0);
      }
    } catch (error) {
      console.error('Error fetching dict items:', error);
    } finally {
      setItemsLoading(false);
    }
  };

  // 处理字典类型选择
  const handleDictTypeSelect = (dictType: DictType) => {
    setSelectedDictType(dictType);
  };

  // 处理分页变化
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  // 处理每页大小变化
  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(1);
  };

  // 处理字典项分页变化
  const handleItemsPageChange = (page: number) => {
    setItemsCurrentPage(page);
  };

  // 处理字典项每页大小变化
  const handleItemsPageSizeChange = (size: number) => {
    setItemsPageSize(size);
    setItemsCurrentPage(1);
  };

  // 处理搜索参数变化
  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchParams(prev => ({
      ...prev,
      keyword: e.target.value
    }));
  };

  // 处理搜索提交
  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(1);
    if (selectedDictType) {
      setItemsCurrentPage(1);
    }
  };

  // 处理字典类型模态框打开
  const handleDictTypeModalOpen = (type: 'add' | 'edit' | 'delete', data?: DictType) => {
    setDictTypeModal({
      isOpen: true,
      type,
      data
    });
    if (type === 'edit' && data) {
      setDictTypeForm({
        name: data.name,
        type: data.type,
        status: data.status,
        remark: data.remark
      });
    } else if (type === 'add') {
      setDictTypeForm({
        name: '',
        type: '',
        status: 1,
        remark: ''
      });
    }
  };

  // 处理字典类型模态框关闭
  const handleDictTypeModalClose = () => {
    setDictTypeModal(prev => ({ ...prev, isOpen: false }));
  };

  // 处理字典项模态框打开
  const handleDictItemModalOpen = (type: 'add' | 'edit' | 'delete', data?: DictItem) => {
    setDictItemModal({
      isOpen: true,
      type,
      data
    });
    if (type === 'edit' && data) {
      setDictItemForm({
        type: data.type,
        label: data.label,
        dictValue: data.dictValue,
        sort: data.sort,
        status: data.status,
        remark: data.remark
      });
    } else if (type === 'add' && selectedDictType) {
      setDictItemForm({
        type: selectedDictType.type,
        label: '',
        dictValue: '',
        sort: 1,
        status: 1,
        remark: ''
      });
    }
  };

  // 处理字典项模态框关闭
  const handleDictItemModalClose = () => {
    setDictItemModal(prev => ({ ...prev, isOpen: false }));
  };

  // 处理字典类型表单变化
  const handleDictTypeFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setDictTypeForm(prev => ({
      ...prev,
      [name]: name === 'status' ? parseInt(value) : value
    }));
  };

  // 处理字典项表单变化
  const handleDictItemFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setDictItemForm(prev => ({
      ...prev,
      [name]: name === 'sort' || name === 'status' ? parseInt(value) : value
    }));
  };

  // 处理字典类型保存
  const handleDictTypeSave = async () => {
    try {
      const url = dictTypeModal.type === 'add' ? '/api/dicts' : '/api/dicts';
      const method = dictTypeModal.type === 'add' ? 'POST' : 'PUT';
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          ...dictTypeForm,
          id: dictTypeModal.type === 'edit' && dictTypeModal.data ? dictTypeModal.data.id : null
        })
      });
      if (!response.ok) {
        throw new Error('Failed to save dict type');
      }
      const data = await response.json();
      if (data.success) {
        handleDictTypeModalClose();
        // 重新获取字典类型列表
        setCurrentPage(1);
      }
    } catch (error) {
      console.error('Error saving dict type:', error);
    }
  };

  // 处理字典类型删除
  const handleDictTypeDelete = async () => {
    if (!dictTypeModal.data) return;
    try {
      const response = await fetch(`/api/dicts?` + new URLSearchParams({ id: dictTypeModal.data.id.toString() }), {
        method: 'DELETE'
      });
      if (!response.ok) {
        throw new Error('Failed to delete dict type');
      }
      const data = await response.json();
      if (data.success) {
        handleDictTypeModalClose();
        // 重新获取字典类型列表
        setCurrentPage(1);
        if (selectedDictType && selectedDictType.id === dictTypeModal.data?.id) {
          setSelectedDictType(null);
        }
      } else {
        alert(data.errMessage || '删除失败');
      }
    } catch (error) {
      console.error('Error deleting dict type:', error);
    }
  };

  // 处理字典项保存
  const handleDictItemSave = async () => {
    try {
      const url = dictItemModal.type === 'add' ? '/api/dict/items' : '/api/dict/items';
      const method = dictItemModal.type === 'add' ? 'POST' : 'PUT';
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          ...dictItemForm,
          id: dictItemModal.type === 'edit' && dictItemModal.data ? dictItemModal.data.id : null
        })
      });
      if (!response.ok) {
        throw new Error('Failed to save dict item');
      }
      const data = await response.json();
      if (data.success) {
        handleDictItemModalClose();
        // 重新获取字典项列表
        if (selectedDictType) {
          fetchDictItems(selectedDictType.type, itemsCurrentPage, itemsPageSize);
        }
      }
    } catch (error) {
      console.error('Error saving dict item:', error);
    }
  };

  // 处理字典项删除
  const handleDictItemDelete = async () => {
    if (!dictItemModal.data) return;
    try {
      const response = await fetch(`/api/dict/items?` + new URLSearchParams({ id: dictItemModal.data.id.toString() }), {
        method: 'DELETE'
      });
      if (!response.ok) {
        throw new Error('Failed to delete dict item');
      }
      const data = await response.json();
      if (data.success) {
        handleDictItemModalClose();
        // 重新获取字典项列表
        if (selectedDictType) {
          fetchDictItems(selectedDictType.type, itemsCurrentPage, itemsPageSize);
        }
      } else {
        alert(data.errMessage || '删除失败');
      }
    } catch (error) {
      console.error('Error deleting dict item:', error);
    }
  };

  // 计算总页数
  const totalPages = Math.ceil(totalCount / pageSize);
  const itemsTotalPages = Math.ceil(itemsTotalCount / itemsPageSize);

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">字典管理</h2>
        </div>
        <button 
          onClick={() => handleDictTypeModalOpen('add')}
          className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
        >
          <Plus size={18} />
          新增字典类型
        </button>
      </div>

      {/* 搜索栏 */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-4">
        <form onSubmit={handleSearchSubmit} className="flex items-center gap-4">
          <div className="flex items-center gap-4 bg-slate-50 px-3 py-2 rounded-lg border border-slate-200 w-96">
            <Search size={18} className="text-slate-400" />
            <input 
              type="text" 
              placeholder="搜索字典名称或编码..." 
              className="text-sm outline-none w-full bg-transparent"
              value={searchParams.keyword}
              onChange={handleSearchChange}
            />
          </div>
          <button 
            type="submit"
            className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 text-sm"
          >
            <Search size={16} />
            搜索
          </button>
        </form>
      </div>

      {/* 字典类型表格 */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 bg-slate-50/50">
          <h3 className="text-lg font-semibold text-slate-900">字典类型管理</h3>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">ID</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">类型名称</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">编码</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">状态</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">创建时间</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">备注</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-6 py-12 text-center">
                    <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
                  </td>
                </tr>
              ) : dictTypes.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-6 py-12 text-center text-slate-500">
                    暂无字典类型数据
                  </td>
                </tr>
              ) : (
                dictTypes.map((dictType) => (
                  <tr 
                    key={dictType.id} 
                    className={cn(
                      "hover:bg-slate-50 transition-colors cursor-pointer",
                      selectedDictType?.id === dictType.id && "bg-blue-50"
                    )}
                    onClick={() => handleDictTypeSelect(dictType)}
                  >
                    <td className="px-6 py-4 text-sm text-slate-600">{dictType.id}</td>
                    <td className="px-6 py-4 text-sm font-semibold text-slate-900">{dictType.name}</td>
                    <td className="px-6 py-4 text-sm font-mono text-slate-600">{dictType.type}</td>
                    <td className="px-6 py-4">
                      <span className={cn(
                        "px-2 py-1 text-xs font-bold rounded-md",
                        dictType.status === 1 ? "bg-emerald-50 text-emerald-600" : "bg-slate-100 text-slate-500"
                      )}>
                        {dictType.status === 1 ? '正常' : '禁用'}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-slate-600">{dictType.createdAt}</td>
                    <td className="px-6 py-4 text-sm text-slate-500">{dictType.remark}</td>
                    <td className="px-6 py-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button 
                          onClick={(e) => {
                            e.stopPropagation();
                            handleDictTypeModalOpen('edit', dictType);
                          }}
                          className="p-2 text-slate-400 hover:text-blue-600 transition-colors"
                        >
                          <Edit size={18} />
                        </button>
                        <button 
                          onClick={(e) => {
                            e.stopPropagation();
                            handleDictTypeModalOpen('delete', dictType);
                          }}
                          className="p-2 text-slate-400 hover:text-red-600 transition-colors"
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

        {/* 分页组件 */}
        <div className="p-4 border-t border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="text-sm text-slate-500">
            共 {totalCount} 条记录，当前第 {currentPage} / {totalPages} 页
          </div>
          <div className="flex items-center gap-2">
            <select 
              value={pageSize} 
              onChange={(e) => handlePageSizeChange(Number(e.target.value))}
              className="px-3 py-1.5 border border-slate-200 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value={5}>5条/页</option>
              <option value={10}>10条/页</option>
              <option value={20}>20条/页</option>
              <option value={50}>50条/页</option>
            </select>
            <div className="flex items-center gap-1">
              <button 
                onClick={() => handlePageChange(Math.max(1, currentPage - 1))}
                disabled={currentPage === 1}
                className={cn(
                  "p-2 rounded-md transition-colors",
                  currentPage === 1 ? "text-slate-300 cursor-not-allowed" : "text-slate-500 hover:bg-slate-100"
                )}
              >
                <ChevronLeft size={16} />
              </button>
              <button 
                onClick={() => handlePageChange(Math.min(totalPages, currentPage + 1))}
                disabled={currentPage === totalPages || totalPages === 0}
                className={cn(
                  "p-2 rounded-md transition-colors",
                  (currentPage === totalPages || totalPages === 0) ? "text-slate-300 cursor-not-allowed" : "text-slate-500 hover:bg-slate-100"
                )}
              >
                <ChevronRight size={16} />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* 字典项表格 */}
      {selectedDictType && (
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
            <h3 className="text-lg font-semibold text-slate-900">{selectedDictType.name} - 字典项管理</h3>
            <button 
              onClick={() => handleDictItemModalOpen('add')}
              className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200 text-sm"
            >
              <Plus size={16} />
              新增字典项
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-slate-50 border-b border-slate-200">
                <tr>
                  <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">ID</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">类型</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">标签</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">值</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">排序</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">状态</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">备注</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {itemsLoading ? (
                  <tr>
                    <td colSpan={8} className="px-6 py-12 text-center">
                      <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
                    </td>
                  </tr>
                ) : dictItems.length === 0 ? (
                  <tr>
                    <td colSpan={8} className="px-6 py-12 text-center text-slate-500">
                      暂无字典项数据
                    </td>
                  </tr>
                ) : (
                  dictItems.map((item) => (
                    <tr key={item.id} className="hover:bg-slate-50 transition-colors">
                      <td className="px-6 py-4 text-sm text-slate-600">{item.id}</td>
                      <td className="px-6 py-4 text-sm font-mono text-slate-600">{item.type}</td>
                      <td className="px-6 py-4 text-sm font-semibold text-slate-900">{item.label}</td>
                      <td className="px-6 py-4 text-sm font-mono text-slate-600">{item.dictValue}</td>
                      <td className="px-6 py-4 text-sm text-slate-600">{item.sort}</td>
                      <td className="px-6 py-4">
                        <span className={cn(
                          "px-2 py-1 text-xs font-bold rounded-md",
                          item.status === 1 ? "bg-emerald-50 text-emerald-600" : "bg-slate-100 text-slate-500"
                        )}>
                          {item.status === 1 ? '正常' : '禁用'}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-slate-500">{item.remark}</td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-2">
                          <button 
                            onClick={() => handleDictItemModalOpen('edit', item)}
                            className="p-2 text-slate-400 hover:text-blue-600 transition-colors"
                          >
                            <Edit size={18} />
                          </button>
                          <button 
                            onClick={() => handleDictItemModalOpen('delete', item)}
                            className="p-2 text-slate-400 hover:text-red-600 transition-colors"
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

          {/* 字典项分页组件 */}
          <div className="p-4 border-t border-slate-100 flex items-center justify-between bg-slate-50/50">
            <div className="text-sm text-slate-500">
              共 {itemsTotalCount} 条记录，当前第 {itemsCurrentPage} / {itemsTotalPages} 页
            </div>
            <div className="flex items-center gap-2">
              <select 
                value={itemsPageSize} 
                onChange={(e) => handleItemsPageSizeChange(Number(e.target.value))}
                className="px-3 py-1.5 border border-slate-200 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value={5}>5条/页</option>
                <option value={10}>10条/页</option>
                <option value={20}>20条/页</option>
                <option value={50}>50条/页</option>
              </select>
              <div className="flex items-center gap-1">
                <button 
                  onClick={() => handleItemsPageChange(Math.max(1, itemsCurrentPage - 1))}
                  disabled={itemsCurrentPage === 1}
                  className={cn(
                    "p-2 rounded-md transition-colors",
                    itemsCurrentPage === 1 ? "text-slate-300 cursor-not-allowed" : "text-slate-500 hover:bg-slate-100"
                  )}
                >
                  <ChevronLeft size={16} />
                </button>
                <button 
                  onClick={() => handleItemsPageChange(Math.min(itemsTotalPages, itemsCurrentPage + 1))}
                  disabled={itemsCurrentPage === itemsTotalPages || itemsTotalPages === 0}
                  className={cn(
                    "p-2 rounded-md transition-colors",
                    (itemsCurrentPage === itemsTotalPages || itemsTotalPages === 0) ? "text-slate-300 cursor-not-allowed" : "text-slate-500 hover:bg-slate-100"
                  )}
                >
                  <ChevronRight size={16} />
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 字典类型模态框 */}
      {dictTypeModal.isOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-xl font-bold text-slate-900">
                {dictTypeModal.type === 'add' ? '新增字典类型' : dictTypeModal.type === 'edit' ? '编辑字典类型' : '删除字典类型'}
              </h3>
              <button 
                onClick={handleDictTypeModalClose}
                className="p-2 text-slate-400 hover:text-slate-600 transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            {dictTypeModal.type === 'delete' ? (
              <div className="space-y-4">
                <div className="flex items-center gap-3 p-4 bg-amber-50 rounded-lg">
                  <AlertTriangle size={24} className="text-amber-500" />
                  <div>
                    <h4 className="font-semibold text-amber-800">确认删除</h4>
                    <p className="text-amber-700 text-sm">
                      您确定要删除字典类型 <span className="font-mono">{dictTypeModal.data?.name}</span> 吗？
                      {" "}
                      <br />
                      <span className="text-amber-600 font-medium">注意：删除前请确保该字典类型下没有字典项。</span>
                    </p>
                  </div>
                </div>
                <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                  <button 
                    onClick={handleDictTypeModalClose}
                    className="px-4 py-2 border border-slate-200 rounded-lg font-medium text-slate-700 hover:bg-slate-50 transition-colors"
                  >
                    取消
                  </button>
                  <button 
                    onClick={handleDictTypeDeleteCheck}
                    className="px-4 py-2 bg-red-600 text-white rounded-lg font-medium hover:bg-red-700 transition-colors"
                  >
                    确认删除
                  </button>
                </div>
              </div>
            ) : (
              <form onSubmit={(e) => {
                e.preventDefault();
                handleDictTypeSave();
              }} className="space-y-4">
                <div>
                  <label htmlFor="name" className="block text-sm font-medium text-slate-700 mb-1">
                    类型名称 <span className="text-red-500">*</span>
                  </label>
                  <input 
                    type="text" 
                    id="name" 
                    name="name" 
                    value={dictTypeForm.name}
                    onChange={handleDictTypeFormChange}
                    required
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label htmlFor="type" className="block text-sm font-medium text-slate-700 mb-1">
                    编码 <span className="text-red-500">*</span>
                  </label>
                  <input 
                    type="text" 
                    id="type" 
                    name="type" 
                    value={dictTypeForm.type}
                    onChange={handleDictTypeFormChange}
                    disabled={dictTypeModal.type === 'edit'}
                    required
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                  {dictTypeModal.type === 'edit' && (
                    <p className="text-xs text-slate-500 mt-1">编码创建后不可编辑</p>
                  )}
                </div>
                <div>
                  <label htmlFor="status" className="block text-sm font-medium text-slate-700 mb-1">
                    状态
                  </label>
                  <select 
                    id="status" 
                    name="status" 
                    value={dictTypeForm.status}
                    onChange={handleDictTypeFormChange}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value={1}>正常</option>
                    <option value={0}>禁用</option>
                  </select>
                </div>
                <div>
                  <label htmlFor="remark" className="block text-sm font-medium text-slate-700 mb-1">
                    备注
                  </label>
                  <textarea 
                    id="remark" 
                    name="remark" 
                    value={dictTypeForm.remark}
                    onChange={handleDictTypeFormChange}
                    rows={3}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  ></textarea>
                </div>
                <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                  <button 
                    type="button"
                    onClick={handleDictTypeModalClose}
                    className="px-4 py-2 border border-slate-200 rounded-lg font-medium text-slate-700 hover:bg-slate-50 transition-colors"
                  >
                    取消
                  </button>
                  <button 
                    type="submit"
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors"
                  >
                    保存
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}

      {/* 字典项模态框 */}
      {dictItemModal.isOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-xl font-bold text-slate-900">
                {dictItemModal.type === 'add' ? '新增字典项' : dictItemModal.type === 'edit' ? '编辑字典项' : '删除字典项'}
              </h3>
              <button 
                onClick={handleDictItemModalClose}
                className="p-2 text-slate-400 hover:text-slate-600 transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            {dictItemModal.type === 'delete' ? (
              <div className="space-y-4">
                <div className="flex items-center gap-3 p-4 bg-amber-50 rounded-lg">
                  <AlertTriangle size={24} className="text-amber-500" />
                  <div>
                    <h4 className="font-semibold text-amber-800">确认删除</h4>
                    <p className="text-amber-700 text-sm">
                      您确定要删除字典项 <span className="font-mono">{dictItemModal.data?.label}</span> 吗？
                    </p>
                  </div>
                </div>
                <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                  <button 
                    onClick={handleDictItemModalClose}
                    className="px-4 py-2 border border-slate-200 rounded-lg font-medium text-slate-700 hover:bg-slate-50 transition-colors"
                  >
                    取消
                  </button>
                  <button 
                    onClick={handleDictItemDelete}
                    className="px-4 py-2 bg-red-600 text-white rounded-lg font-medium hover:bg-red-700 transition-colors"
                  >
                    确认删除
                  </button>
                </div>
              </div>
            ) : (
              <form onSubmit={(e) => {
                e.preventDefault();
                handleDictItemSave();
              }} className="space-y-4">
                <div>
                  <label htmlFor="type" className="block text-sm font-medium text-slate-700 mb-1">
                    类型
                  </label>
                  <input 
                    type="text" 
                    id="type" 
                    name="type" 
                    value={dictItemForm.type}
                    disabled
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 bg-slate-50"
                  />
                </div>
                <div>
                  <label htmlFor="label" className="block text-sm font-medium text-slate-700 mb-1">
                    标签 <span className="text-red-500">*</span>
                  </label>
                  <input 
                    type="text" 
                    id="label" 
                    name="label" 
                    value={dictItemForm.label}
                    onChange={handleDictItemFormChange}
                    required
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label htmlFor="dictValue" className="block text-sm font-medium text-slate-700 mb-1">
                    值 <span className="text-red-500">*</span>
                  </label>
                  <input 
                    type="text" 
                    id="dictValue" 
                    name="dictValue" 
                    value={dictItemForm.dictValue}
                    onChange={handleDictItemFormChange}
                    required
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label htmlFor="sort" className="block text-sm font-medium text-slate-700 mb-1">
                    排序
                  </label>
                  <input 
                    type="number" 
                    id="sort" 
                    name="sort" 
                    value={dictItemForm.sort}
                    onChange={handleDictItemFormChange}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label htmlFor="status" className="block text-sm font-medium text-slate-700 mb-1">
                    状态
                  </label>
                  <select 
                    id="status" 
                    name="status" 
                    value={dictItemForm.status}
                    onChange={handleDictItemFormChange}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value={1}>正常</option>
                    <option value={0}>禁用</option>
                  </select>
                </div>
                <div>
                  <label htmlFor="remark" className="block text-sm font-medium text-slate-700 mb-1">
                    备注
                  </label>
                  <textarea 
                    id="remark" 
                    name="remark" 
                    value={dictItemForm.remark}
                    onChange={handleDictItemFormChange}
                    rows={3}
                    className="w-full px-3 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  ></textarea>
                </div>
                <div className="flex items-center justify-end gap-3 pt-4 border-t border-slate-100">
                  <button 
                    type="button"
                    onClick={handleDictItemModalClose}
                    className="px-4 py-2 border border-slate-200 rounded-lg font-medium text-slate-700 hover:bg-slate-50 transition-colors"
                  >
                    取消
                  </button>
                  <button 
                    type="submit"
                    className="px-4 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors"
                  >
                    保存
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
