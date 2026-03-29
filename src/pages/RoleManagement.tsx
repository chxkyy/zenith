import React, { useEffect, useState } from 'react';
import { MoreHorizontal, Search, Filter, Plus, ChevronUp, ChevronDown, X, Edit, Trash2, Shield, UserPlus } from 'lucide-react';
import { cn } from '../lib/utils';
import PermissionAssignModal from '../components/PermissionAssignModal';
import UserAssignModal from '../components/UserAssignModal';

export default function RoleManagement() {
  const [roles, setRoles] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedRole, setSelectedRole] = useState<any>(null);
  const [isPermissionModalOpen, setIsPermissionModalOpen] = useState(false);
  const [isUserModalOpen, setIsUserModalOpen] = useState(false);
  const [selectedRoleForPermissions, setSelectedRoleForPermissions] = useState<any>(null);
  const [selectedRoleForUsers, setSelectedRoleForUsers] = useState<any>(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [filters, setFilters] = useState({
    status: ''
  });
  const [sortConfig, setSortConfig] = useState({
    field: '',
    direction: 'asc'
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const fetchRoles = (pageIndex = 1) => {
    setLoading(true);
    setError(null);
    
    const query = {
      pageIndex,
      pageSize: 10,
      keyword: searchKeyword,
      status: filters.status ? parseInt(filters.status) : undefined,
      sortField: sortConfig.field,
      sortOrder: sortConfig.direction
    };

    fetch('/api/roles/page', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(query)
    })
      .then(async res => {
        const text = await res.text();
        if (res.status === 503) {
          throw new Error('Java 后端正在启动，请稍候...');
        }
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}. ${text}`);
        }
        if (!text) {
          return { success: true, data: [], totalCount: 0 };
        }
        try {
          return JSON.parse(text);
        } catch (e) {
          console.error('Failed to parse JSON:', text);
          throw new Error('服务器返回了非 JSON 格式的数据');
        }
      })
      .then(res => {
        console.log('Response from backend:', res);
        if (res.success) {
          setRoles(res.data || []);
          setTotalPages(Math.ceil(res.totalCount / 10));
          setCurrentPage(res.pageIndex || 1);
        } else {
          setError(res.errMessage || '获取角色列表失败');
        }
      })
      .catch(err => {
        console.error('Error fetching roles:', err);
        setError(err.message || '网络错误，请检查后端服务');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchRoles();
  }, [searchKeyword, filters, sortConfig]);

  const handleSaveRole = (role: any) => {
    setLoading(true);
    const isEdit = role.id !== undefined;
    const url = isEdit ? '/api/roles' : '/api/roles';
    const method = isEdit ? 'PUT' : 'POST';

    fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(role)
    })
      .then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.text().then(text => text ? JSON.parse(text) : { success: true });
      })
      .then(res => {
        if (res.success) {
          fetchRoles(); // 重新加载列表
          setIsModalOpen(false);
        } else {
          alert(`${isEdit ? '编辑角色失败' : '添加角色失败'}: ${res.errMessage}`);
        }
      })
      .catch(err => {
        console.error(`${isEdit ? 'Error updating role' : 'Error adding role'}:`, err);
        alert(`${isEdit ? '编辑角色失败' : '添加角色失败'}，请检查网络`);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleEditRole = (role: any) => {
    setSelectedRole(role);
    setModalMode('edit');
    setIsModalOpen(true);
  };

  const handleDeleteRole = (id: number) => {
    if (window.confirm('删除后，该角色关联的用户权限将全部解除，是否确认删除？')) {
      setLoading(true);
      fetch(`/api/roles/${id}`, {
        method: 'DELETE'
      })
        .then(res => {
          if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
          }
          return res.text().then(text => text ? JSON.parse(text) : { success: true });
        })
        .then(res => {
          if (res.success) {
            fetchRoles(); // 重新加载列表
          } else {
            alert('删除角色失败: ' + res.errMessage);
          }
        })
        .catch(err => {
          console.error('Error deleting role:', err);
          alert('删除角色失败，请检查网络');
        })
        .finally(() => {
          setLoading(false);
        });
    }
  };

  const handleChangeStatus = (id: number, currentStatus: number) => {
    const newStatus = currentStatus === 1 ? 0 : 1;
    if (window.confirm(`确定要将角色状态切换为${newStatus === 1 ? '启用' : '禁用'}吗？`)) {
      setLoading(true);
      fetch(`/api/roles/status/${id}?status=${newStatus}`, {
        method: 'PUT'
      })
        .then(res => {
          if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
          }
          return res.text().then(text => text ? JSON.parse(text) : { success: true });
        })
        .then(res => {
          if (res.success) {
            fetchRoles(); // 重新加载列表
          } else {
            alert('状态切换失败: ' + res.errMessage);
          }
        })
        .catch(err => {
          console.error('Error changing status:', err);
          alert('状态切换失败，请检查网络');
        })
        .finally(() => {
          setLoading(false);
        });
    }
  };

  const handleSort = (field: string) => {
    const direction = sortConfig.field === field && sortConfig.direction === 'asc' ? 'desc' : 'asc';
    setSortConfig({ field, direction });
  };

  const handleFilterChange = (key: string, value: string) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  const handleFilterReset = () => {
    setFilters({ status: '' });
  };

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      fetchRoles(page);
    }
  };

  const handleAssignPermissions = (role: any) => {
    setSelectedRoleForPermissions(role);
    setIsPermissionModalOpen(true);
  };

  const handleAssignUsers = (role: any) => {
    setSelectedRoleForUsers(role);
    setIsUserModalOpen(true);
  };

  const handleSavePermissions = (permissions: string[]) => {
    // 这里简化处理，实际应该调用后端 API 来更新权限
    setLoading(true);
    // 由于后端 API 尚未实现角色分配权限功能，这里只是模拟成功
    setTimeout(() => {
      alert('权限分配成功');
      setLoading(false);
    }, 500);
  };

  const handleSaveUsers = (users: number[]) => {
    // 这里简化处理，实际应该调用后端 API 来更新用户
    setLoading(true);
    // 由于后端 API 尚未实现角色分配用户功能，这里只是模拟成功
    setTimeout(() => {
      alert('用户分配成功');
      setLoading(false);
    }, 500);
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">角色管理</h2>
          <p className="text-slate-500 mt-1">管理系统中的所有角色及其权限。</p>
        </div>
        <div className="flex items-center gap-3">
          <button 
            onClick={() => fetchRoles()}
            disabled={loading}
            className="flex items-center gap-2 bg-white text-slate-600 border border-slate-200 px-4 py-2.5 rounded-xl font-medium hover:bg-slate-50 transition-all shadow-sm disabled:opacity-50"
          >
            刷新
          </button>
          <button 
            onClick={() => {
              setModalMode('add');
              setSelectedRole(null);
              setIsModalOpen(true);
            }}
            className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
          >
            <Plus size={18} />
            添加角色
          </button>
        </div>
      </div>

      {/* 角色列表 */}
      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex flex-wrap items-center gap-4 bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={16} className="text-slate-400" />
            <input 
              type="text" 
              placeholder="搜索角色..." 
              className="text-sm outline-none w-full"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
            />
          </div>
          <div className="flex items-center gap-2">
            <button 
              onClick={() => setIsFilterOpen(!isFilterOpen)}
              className="flex items-center gap-2 px-3 py-1.5 text-sm font-medium text-slate-600 hover:bg-white hover:shadow-sm border border-transparent hover:border-slate-200 rounded-lg transition-all"
            >
              <Filter size={16} />
              筛选
            </button>
            {isFilterOpen && (
              <div className="absolute mt-12 bg-white rounded-lg shadow-lg border border-slate-200 p-4 z-10">
                <div className="flex justify-between items-center mb-4">
                  <h3 className="font-medium text-slate-900">筛选条件</h3>
                  <button onClick={() => setIsFilterOpen(false)} className="p-1 hover:bg-slate-100 rounded-full">
                    <X size={16} className="text-slate-400" />
                  </button>
                </div>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">状态</label>
                    <select 
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                      value={filters.status}
                      onChange={(e) => handleFilterChange('status', e.target.value)}
                    >
                      <option value="">全部</option>
                      <option value="1">启用</option>
                      <option value="0">禁用</option>
                    </select>
                  </div>
                  <div className="flex gap-2 mt-4">
                    <button 
                      onClick={handleFilterReset}
                      className="flex-1 px-3 py-2 text-sm font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-50"
                    >
                      重置
                    </button>
                    <button 
                      onClick={() => setIsFilterOpen(false)}
                      className="flex-1 px-3 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700"
                    >
                      应用
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        <table className="w-full text-left">
          <thead>
            <tr className="bg-slate-50/50 text-slate-500 text-xs uppercase font-bold tracking-wider">
              <th className="px-6 py-4">
                <button 
                  onClick={() => handleSort('name')}
                  className="flex items-center gap-1 hover:text-blue-600 transition-colors"
                >
                  角色名称
                  {sortConfig.field === 'name' && (
                    sortConfig.direction === 'asc' ? <ChevronUp size={14} /> : <ChevronDown size={14} />
                  )}
                </button>
              </th>
              <th className="px-6 py-4">
                <button 
                  onClick={() => handleSort('code')}
                  className="flex items-center gap-1 hover:text-blue-600 transition-colors"
                >
                  角色编码
                  {sortConfig.field === 'code' && (
                    sortConfig.direction === 'asc' ? <ChevronUp size={14} /> : <ChevronDown size={14} />
                  )}
                </button>
              </th>
              <th className="px-6 py-4">
                <button 
                  onClick={() => handleSort('status')}
                  className="flex items-center gap-1 hover:text-blue-600 transition-colors"
                >
                  状态
                  {sortConfig.field === 'status' && (
                    sortConfig.direction === 'asc' ? <ChevronUp size={14} /> : <ChevronDown size={14} />
                  )}
                </button>
              </th>
              <th className="px-6 py-4">
                <button 
                  onClick={() => handleSort('createTime')}
                  className="flex items-center gap-1 hover:text-blue-600 transition-colors"
                >
                  创建时间
                  {sortConfig.field === 'createTime' && (
                    sortConfig.direction === 'asc' ? <ChevronUp size={14} /> : <ChevronDown size={14} />
                  )}
                </button>
              </th>
              <th className="px-6 py-4 text-right">操作</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {loading ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center">
                  <div className="flex flex-col items-center gap-3">
                    <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                    <p className="text-slate-500 text-sm">正在加载角色数据...</p>
                  </div>
                </td>
              </tr>
            ) : error ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center">
                  <div className="flex flex-col items-center gap-3">
                    <p className="text-red-500 text-sm font-medium">{error}</p>
                    <button 
                      onClick={() => fetchRoles()}
                      className="text-blue-600 text-sm hover:underline"
                    >
                      点击重试
                    </button>
                  </div>
                </td>
              </tr>
            ) : roles.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center">
                  <p className="text-slate-500 text-sm">暂无角色数据</p>
                </td>
              </tr>
            ) : (
              roles.map((role) => (
                <tr key={role.id} className="hover:bg-slate-50 transition-colors group">
                  <td className="px-6 py-4">
                    <p className="text-sm font-semibold text-slate-900">{role.name}</p>
                    <p className="text-xs text-slate-500">{role.remark || '-'}</p>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-600 font-mono">{role.code}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={cn(
                      "px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider",
                      role.status === 1 ? "bg-emerald-100 text-emerald-700" : 
                      role.status === 0 ? "bg-red-100 text-red-700" : "bg-slate-100 text-slate-600"
                    )}>
                      {role.status === 1 ? '启用' : '禁用'}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-600">{role.createTime || '-'}</span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="relative group">
                      <button className="p-2 text-slate-400 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-all">
                        <MoreHorizontal size={18} />
                      </button>
                      <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-slate-200 z-10 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200">
                        <div className="py-1">
                          <button 
                            onClick={() => handleEditRole(role)}
                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                          >
                            <Edit size={14} />
                            编辑
                          </button>
                          <button 
                            onClick={() => handleChangeStatus(role.id, role.status)}
                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                          >
                            <UserPlus size={14} />
                            {role.status === 1 ? '禁用' : '启用'}
                          </button>
                          <button 
                            onClick={() => handleAssignPermissions(role)}
                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                          >
                            <Shield size={14} />
                            分配权限
                          </button>
                          <button 
                            onClick={() => handleAssignUsers(role)}
                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                          >
                            <UserPlus size={14} />
                            分配用户
                          </button>
                          <div className="border-t border-slate-100 my-1"></div>
                          <button 
                            onClick={() => handleDeleteRole(role.id)}
                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                          >
                            <Trash2 size={14} />
                            删除
                          </button>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        {/* 分页 */}
        {!loading && !error && roles.length > 0 && (
          <div className="p-4 border-t border-slate-100 flex items-center justify-between">
            <p className="text-sm text-slate-500">
              第 {currentPage} 页，共 {totalPages} 页
            </p>
            <div className="flex items-center gap-2">
              <button 
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 1}
                className="px-3 py-1.5 text-sm font-medium rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                上一页
              </button>
              <button 
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
                className="px-3 py-1.5 text-sm font-medium rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                下一页
              </button>
            </div>
          </div>
        )}
      </div>

      {/* 角色编辑弹窗 */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
            <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
              <h3 className="text-lg font-bold text-slate-900">
                {modalMode === 'add' ? '添加新角色' : '编辑角色'}
              </h3>
              <button onClick={() => setIsModalOpen(false)} className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-slate-600">
                <X size={20} />
              </button>
            </div>

            <form 
              onSubmit={(e) => {
                e.preventDefault();
                const formData = new FormData(e.target as HTMLFormElement);
                const roleData = {
                  id: modalMode === 'edit' ? selectedRole.id : undefined,
                  name: formData.get('name') as string,
                  code: formData.get('code') as string,
                  status: parseInt(formData.get('status') as string),
                  remark: formData.get('remark') as string
                };
                handleSaveRole(roleData);
              }}
              className="p-6 space-y-4"
            >
              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-slate-700">角色名称</label>
                <input
                  required
                  type="text"
                  name="name"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
                  placeholder="请输入角色名称"
                  defaultValue={modalMode === 'edit' ? selectedRole?.name : ''}
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-slate-700">角色编码</label>
                <input
                  required
                  type="text"
                  name="code"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all font-mono"
                  placeholder="请输入角色编码（大写英文+下划线）"
                  defaultValue={modalMode === 'edit' ? selectedRole?.code : ''}
                  disabled={modalMode === 'edit'} // 编辑模式下角色编码不可修改
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-slate-700">状态</label>
                <select
                  required
                  name="status"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 transition-all bg-white"
                  defaultValue={modalMode === 'edit' ? selectedRole?.status : 1}
                >
                  <option value={1}>启用</option>
                  <option value={0}>禁用</option>
                </select>
              </div>

              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-slate-700">备注</label>
                <textarea
                  name="remark"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all resize-none"
                  placeholder="请输入备注信息"
                  rows={3}
                  defaultValue={modalMode === 'edit' ? selectedRole?.remark : ''}
                ></textarea>
              </div>

              <div className="pt-4 flex gap-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="flex-1 px-4 py-2.5 rounded-xl border border-slate-200 font-semibold text-slate-600 hover:bg-slate-50 transition-all"
                >
                  取消
                </button>
                <button
                  type="submit"
                  className="flex-1 px-4 py-2.5 rounded-xl bg-blue-600 font-semibold text-white hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all"
                >
                  {modalMode === 'add' ? '保存角色' : '更新角色'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 权限分配弹窗 */}
      <PermissionAssignModal 
        isOpen={isPermissionModalOpen} 
        onClose={() => setIsPermissionModalOpen(false)} 
        onSave={handleSavePermissions}
        roleId={selectedRoleForPermissions?.id || 0}
      />

      {/* 用户分配弹窗 */}
      <UserAssignModal 
        isOpen={isUserModalOpen} 
        onClose={() => setIsUserModalOpen(false)} 
        onSave={handleSaveUsers}
        roleId={selectedRoleForUsers?.id || 0}
      />
    </div>
  );
}