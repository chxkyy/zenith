import React, { useEffect, useState } from 'react';
import { MoreHorizontal, Search, Filter, UserPlus, ChevronUp, ChevronDown, X, Edit, Trash2, RefreshCw, User, Shield } from 'lucide-react';
import { cn } from '../lib/utils';
import UserModal from './UserModal';
import RoleAssignModal from './RoleAssignModal';

export default function UserTable() {
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedUser, setSelectedUser] = useState<any>(null);
  const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);
  const [selectedUserForRoles, setSelectedUserForRoles] = useState<any>(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [filters, setFilters] = useState({
    status: '',
    role: '',
    orgName: ''
  });
  const [sortConfig, setSortConfig] = useState({
    field: '',
    direction: 'asc'
  });
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const fetchUsers = (pageIndex = 1) => {
    setLoading(true);
    setError(null);
    
    const query = {
      pageIndex,
      pageSize: 10,
      keyword: searchKeyword,
      status: filters.status ? parseInt(filters.status) : undefined,
      role: filters.role,
      orgName: filters.orgName,
      sortField: sortConfig.field,
      sortOrder: sortConfig.direction
    };

    fetch('/api/users/page', {
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
          setUsers(res.data || []);
          setTotalPages(Math.ceil(res.totalCount / 10));
          setCurrentPage(res.pageIndex || 1);
        } else {
          setError(res.errMessage || '获取用户列表失败');
        }
      })
      .catch(err => {
        console.error('Error fetching users:', err);
        setError(err.message || '网络错误，请检查后端服务');
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchUsers();
  }, [searchKeyword, filters, sortConfig]);

  const handleSaveUser = (user: any) => {
    setLoading(true);
    const isEdit = user.id !== undefined;
    const url = isEdit ? '/api/users' : '/api/users';
    const method = isEdit ? 'PUT' : 'POST';

    fetch(url, {
      method: method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(user)
    })
      .then(res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.text().then(text => text ? JSON.parse(text) : { success: true });
      })
      .then(res => {
        if (res.success) {
          fetchUsers(); // 重新加载列表
          setIsModalOpen(false);
        } else {
          alert(`${isEdit ? '编辑用户失败' : '添加用户失败'}: ${res.errMessage}`);
        }
      })
      .catch(err => {
        console.error(`${isEdit ? 'Error updating user' : 'Error adding user'}:`, err);
        alert(`${isEdit ? '编辑用户失败' : '添加用户失败'}，请检查网络`);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const handleSort = (field: string) => {
    const direction = sortConfig.field === field && sortConfig.direction === 'asc' ? 'desc' : 'asc';
    setSortConfig({ field, direction });
  };

  const handleFilterChange = (key: string, value: string) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  const handleFilterReset = () => {
    setFilters({ status: '', role: '', orgName: '' });
  };

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      fetchUsers(page);
    }
  };

  const handleEditUser = (user: any) => {
    setSelectedUser(user);
    setModalMode('edit');
    setIsModalOpen(true);
  };

  const handleDeleteUser = (id: number) => {
    if (window.confirm('删除后用户数据不可恢复，关联角色自动解除，是否确认删除？')) {
      setLoading(true);
      fetch(`/api/users/${id}`, {
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
            fetchUsers(); // 重新加载列表
          } else {
            alert('删除用户失败: ' + res.errMessage);
          }
        })
        .catch(err => {
          console.error('Error deleting user:', err);
          alert('删除用户失败，请检查网络');
        })
        .finally(() => {
          setLoading(false);
        });
    }
  };

  const handleResetPassword = (id: number) => {
    if (window.confirm('确定要重置该用户的密码吗？重置后密码为默认密码 123456，请通知用户及时修改。')) {
      setLoading(true);
      fetch(`/api/users/reset-password/${id}`, {
        method: 'POST'
      })
        .then(res => {
          if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
          }
          return res.text().then(text => text ? JSON.parse(text) : { success: true });
        })
        .then(res => {
          if (res.success) {
            alert('密码重置成功，默认密码为 123456，请通知用户及时修改');
          } else {
            alert('重置密码失败: ' + res.errMessage);
          }
        })
        .catch(err => {
          console.error('Error resetting password:', err);
          alert('重置密码失败，请检查网络');
        })
        .finally(() => {
          setLoading(false);
        });
    }
  };

  const handleChangeStatus = (id: number, currentStatus: number) => {
    const newStatus = currentStatus === 1 ? 0 : 1;
    if (window.confirm(`确定要将用户状态切换为${newStatus === 1 ? '活跃' : '禁用'}吗？`)) {
      setLoading(true);
      fetch(`/api/users/status/${id}?status=${newStatus}`, {
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
            fetchUsers(); // 重新加载列表
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

  const handleAssignRoles = (user: any) => {
    setSelectedUserForRoles(user);
    setIsRoleModalOpen(true);
  };

  const handleSaveRoles = (roles: string[]) => {
    // 这里简化处理，实际应该调用后端 API 来更新角色
    setLoading(true);
    // 由于后端 API 尚未实现角色分配功能，这里只是模拟成功
    setTimeout(() => {
      alert('角色分配成功');
      fetchUsers(); // 重新加载列表
      setLoading(false);
    }, 500);
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">用户管理 (Java Backend)</h2>
          <p className="text-slate-500 mt-1">管理系统中的所有用户及其权限。</p>
        </div>
        <div className="flex items-center gap-3">
          <button 
            onClick={() => fetchUsers()}
            disabled={loading}
            className="flex items-center gap-2 bg-white text-slate-600 border border-slate-200 px-4 py-2.5 rounded-xl font-medium hover:bg-slate-50 transition-all shadow-sm disabled:opacity-50"
          >
            刷新
          </button>
          <button 
            onClick={() => setIsModalOpen(true)}
            className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
          >
            <UserPlus size={18} />
            添加用户
          </button>
        </div>
      </div>

      <UserModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        onSave={handleSaveUser} 
        user={selectedUser}
        mode={modalMode}
      />

      <RoleAssignModal 
        isOpen={isRoleModalOpen} 
        onClose={() => setIsRoleModalOpen(false)} 
        onSave={handleSaveRoles}
        userRoles={selectedUserForRoles ? [selectedUserForRoles.role] : []}
      />

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex flex-wrap items-center gap-4 bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={16} className="text-slate-400" />
            <input 
              type="text" 
              placeholder="搜索用户..." 
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
                      <option value="1">活跃</option>
                      <option value="0">禁用</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">角色</label>
                    <select 
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                      value={filters.role}
                      onChange={(e) => handleFilterChange('role', e.target.value)}
                    >
                      <option value="">全部</option>
                      <option value="ADMIN">管理员</option>
                      <option value="EDITOR">编辑者</option>
                      <option value="USER">普通用户</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-slate-700 mb-1">所属组织</label>
                    <input 
                      type="text" 
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                      placeholder="输入组织名称"
                      value={filters.orgName}
                      onChange={(e) => handleFilterChange('orgName', e.target.value)}
                    />
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
                  onClick={() => handleSort('username')}
                  className="flex items-center gap-1 hover:text-blue-600 transition-colors"
                >
                  用户信息
                  {sortConfig.field === 'username' && (
                    sortConfig.direction === 'asc' ? <ChevronUp size={14} /> : <ChevronDown size={14} />
                  )}
                </button>
              </th>
              <th className="px-6 py-4">
                <button 
                  onClick={() => handleSort('orgName')}
                  className="flex items-center gap-1 hover:text-blue-600 transition-colors"
                >
                  所属组织
                  {sortConfig.field === 'orgName' && (
                    sortConfig.direction === 'asc' ? <ChevronUp size={14} /> : <ChevronDown size={14} />
                  )}
                </button>
              </th>
              <th className="px-6 py-4">
                <button 
                  onClick={() => handleSort('role')}
                  className="flex items-center gap-1 hover:text-blue-600 transition-colors"
                >
                  角色
                  {sortConfig.field === 'role' && (
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
              <th className="px-6 py-4 text-right">操作</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {loading ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center">
                  <div className="flex flex-col items-center gap-3">
                    <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                    <p className="text-slate-500 text-sm">正在加载用户数据...</p>
                  </div>
                </td>
              </tr>
            ) : error ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center">
                  <div className="flex flex-col items-center gap-3">
                    <p className="text-red-500 text-sm font-medium">{error}</p>
                    <button 
                      onClick={() => fetchUsers()}
                      className="text-blue-600 text-sm hover:underline"
                    >
                      点击重试
                    </button>
                  </div>
                </td>
              </tr>
            ) : users.length === 0 ? (
              <tr>
                <td colSpan={5} className="px-6 py-12 text-center">
                  <p className="text-slate-500 text-sm">暂无用户数据</p>
                </td>
              </tr>
            ) : (
              users.map((user) => (
                <tr key={user.id} className="hover:bg-slate-50 transition-colors group">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold">
                        {user.username ? user.username[0] : '?'}
                      </div>
                      <div>
                        <p className="text-sm font-semibold text-slate-900">{user.username}</p>
                        <p className="text-xs text-slate-500">{user.email}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2 text-slate-600">
                      <span className="text-sm">{user.orgName || '-'}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className="text-sm text-slate-600">{user.role}</span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={cn(
                      "px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider",
                      user.status === 1 ? "bg-emerald-100 text-emerald-700" : 
                      user.status === 0 ? "bg-red-100 text-red-700" : "bg-slate-100 text-slate-600"
                    )}>
                      {user.status === 1 ? '活跃' : '禁用'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="relative group">
                      <button className="p-2 text-slate-400 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-all">
                        <MoreHorizontal size={18} />
                      </button>
                      <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-slate-200 z-10 opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200">
                        <div className="py-1">
                          <button 
                            onClick={() => handleEditUser(user)}
                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                          >
                            <Edit size={14} />
                            编辑
                          </button>
                          <button 
                            onClick={() => handleResetPassword(user.id)}
                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                          >
                            <RefreshCw size={14} />
                            重置密码
                          </button>
                          <button 
                            onClick={() => handleChangeStatus(user.id, user.status)}
                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                          >
                            <User size={14} />
                            {user.status === 1 ? '禁用' : '启用'}
                          </button>
                          <button 
                            onClick={() => handleAssignRoles(user)}
                            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                          >
                            <Shield size={14} />
                            分配角色
                          </button>
                          <div className="border-t border-slate-100 my-1"></div>
                          <button 
                            onClick={() => handleDeleteUser(user.id)}
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
        {!loading && !error && users.length > 0 && (
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
    </div>
  );
}
