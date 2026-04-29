import React, { useState, useEffect } from 'react';
import { Search, Plus, Key, Shield, ChevronRight, ChevronDown, Trash2, Edit, Users, Filter, X, ExternalLink } from 'lucide-react';
import { cn } from '../lib/utils';
import Notification from './Notification';

interface Permission {
  id: number;
  name: string;
  code: string;
  type: 'MENU' | 'BUTTON' | 'API';
  parentId: number | null;
  parentName?: string;
  apiPath?: string;
  apiMethod?: string;
  remark?: string;
  createTime: string;
  children?: Permission[];
  associatedRoles?: string[];
}

interface PermissionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (permission: Partial<Permission>) => void;
  permission?: Permission;
  mode: 'add' | 'edit';
  allPermissions: Permission[];
}

const PermissionModal: React.FC<PermissionModalProps> = ({ isOpen, onClose, onSave, permission, mode, allPermissions }) => {
  const [formData, setFormData] = useState<Partial<Permission>>({
    id: permission?.id,
    name: permission?.name || '',
    code: permission?.code || '',
    type: permission?.type || 'MENU',
    parentId: permission?.parentId || null,
    apiPath: permission?.apiPath || '',
    apiMethod: permission?.apiMethod || 'GET',
    remark: permission?.remark || ''
  });

  // 当 permission 属性变化时，更新 formData
  useEffect(() => {
    if (permission) {
      setFormData({
        id: permission.id,
        name: permission.name || '',
        code: permission.code || '',
        type: permission.type || 'MENU',
        parentId: permission.parentId || null,
        apiPath: permission.apiPath || '',
        apiMethod: permission.apiMethod || 'GET',
        remark: permission.remark || ''
      });
    }
  }, [permission]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/15 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <h3 className="text-lg font-bold text-slate-900">
            {mode === 'add' ? '新增权限' : '编辑权限'}
          </h3>
          <button onClick={onClose} className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-slate-600">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">权限名称</label>
            <input
              required
              type="text"
              value={formData.name || ''}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
              placeholder="请输入权限名称"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">权限标识</label>
            <input
              required
              type="text"
              value={formData.code || ''}
              onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all font-mono"
              placeholder="请输入权限标识（大写英文+下划线）"
              disabled={mode === 'edit'}
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">权限类型</label>
            <select
              required
              value={formData.type || 'MENU'}
              onChange={(e) => setFormData({ ...formData, type: e.target.value as 'MENU' | 'BUTTON' | 'API' })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 transition-all bg-white"
            >
              <option value="MENU">菜单</option>
              <option value="BUTTON">按钮</option>
              <option value="API">接口</option>
            </select>
          </div>

          {(formData.type === 'BUTTON' || formData.type === 'API') && (
            <div className="space-y-1.5">
              <label className="text-sm font-semibold text-slate-700">所属菜单</label>
              <select
                required
                value={formData.parentId || ''}
                onChange={(e) => setFormData({ ...formData, parentId: e.target.value ? parseInt(e.target.value) : null })}
                className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 transition-all bg-white"
              >
                <option value="">请选择所属菜单</option>
                {allPermissions
                  .filter(p => p.type === 'MENU')
                  .map(menu => (
                    <option key={menu.id} value={menu.id}>
                      {menu.name}
                    </option>
                  ))}
              </select>
            </div>
          )}

          {formData.type === 'API' && (
            <>
              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-slate-700">接口路径</label>
                <input
                  required
                  type="text"
                  value={formData.apiPath || ''}
                  onChange={(e) => setFormData({ ...formData, apiPath: e.target.value })}
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all font-mono"
                  placeholder="请输入接口路径，如 /api/users"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-slate-700">请求方式</label>
                <select
                  required
                  value={formData.apiMethod || 'GET'}
                  onChange={(e) => setFormData({ ...formData, apiMethod: e.target.value })}
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 transition-all bg-white"
                >
                  <option value="GET">GET</option>
                  <option value="POST">POST</option>
                  <option value="PUT">PUT</option>
                  <option value="DELETE">DELETE</option>
                  <option value="PATCH">PATCH</option>
                </select>
              </div>
            </>
          )}

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">备注</label>
            <textarea
              value={formData.remark || ''}
              onChange={(e) => setFormData({ ...formData, remark: e.target.value })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all resize-none"
              placeholder="请输入备注信息"
              rows={3}
            ></textarea>
          </div>

          <div className="pt-4 flex gap-3">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2.5 rounded-xl border border-slate-200 font-semibold text-slate-600 hover:bg-slate-50 transition-all"
            >
              取消
            </button>
            <button
              type="submit"
              className="flex-1 px-4 py-2.5 rounded-xl bg-blue-600 font-semibold text-white hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all"
            >
              {mode === 'add' ? '保存权限' : '更新权限'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

interface RoleAssociationModalProps {
  isOpen: boolean;
  onClose: () => void;
  permission: Permission;
}

const RoleAssociationModal: React.FC<RoleAssociationModalProps> = ({ isOpen, onClose, permission }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/15 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <h3 className="text-lg font-bold text-slate-900">
            权限关联角色 - {permission.name}
          </h3>
          <button onClick={onClose} className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-slate-600">
            <X size={20} />
          </button>
        </div>

        <div className="p-6 space-y-4">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200">
            <Search size={16} className="text-slate-400" />
            <input 
              type="text" 
              placeholder="搜索角色..." 
              className="text-sm outline-none w-full"
            />
          </div>

          <div className="space-y-2 max-h-96 overflow-y-auto">
            {permission.associatedRoles && permission.associatedRoles.length > 0 ? (
              permission.associatedRoles.map((role, index) => (
                <div key={index} className="flex items-center justify-between p-3 bg-slate-50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-full bg-slate-100 flex items-center justify-center">
                      <Users size={16} className="text-slate-600" />
                    </div>
                    <span className="text-sm font-medium text-slate-900">{role}</span>
                  </div>
                </div>
              ))
            ) : (
              <div className="flex items-center justify-center py-12">
                <p className="text-slate-500 text-sm">暂无关联角色</p>
              </div>
            )}
          </div>

          <div className="pt-4 flex justify-end">
            <button
              onClick={onClose}
              className="px-4 py-2.5 rounded-xl border border-slate-200 font-semibold text-slate-600 hover:bg-slate-50 transition-all"
            >
              关闭
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default function PermissionTable() {
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [expanded, setExpanded] = useState<number[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedPermission, setSelectedPermission] = useState<Permission | undefined>();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [filters, setFilters] = useState({
    type: '',
  });
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' | 'info' } | null>(null);
  const [loading, setLoading] = useState(true);

  const toggleExpand = (id: number) => {
    setExpanded(prev => prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]);
  };

  const fetchPermissions = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/menus');
      if (!response.ok) {
        throw new Error('Failed to fetch permissions');
      }
      const data = await response.json();
      if (data.success && data.data) {
        // 转换后端返回的菜单数据为前端需要的权限数据结构
        const convertToPermissions = (menus: any[]): Permission[] => {
          return menus.map(menu => {
            const permission: Permission = {
              id: menu.id,
              name: menu.name,
              code: menu.permission || '',
              type: menu.type === 'menu' ? 'MENU' : menu.type === 'button' ? 'BUTTON' : 'API',
              parentId: menu.parentId,
              createTime: new Date().toLocaleString('zh-CN'),
              associatedRoles: []
            };
            if (menu.children && menu.children.length > 0) {
              permission.children = convertToPermissions(menu.children);
              permission.children.forEach(child => {
                child.parentName = menu.name;
              });
            }
            return permission;
          });
        };
        setPermissions(convertToPermissions(data.data));
      }
    } catch (error) {
      console.error('Error fetching permissions:', error);
      setNotification({ 
        message: '获取权限列表失败',
        type: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  // 组件挂载时获取权限列表
  useEffect(() => {
    fetchPermissions();
  }, []);

  const handleSavePermission = async (permissionData: Partial<Permission>) => {
    setLoading(true);
    try {
      // 转换前端权限数据为后端需要的菜单数据结构
      const convertToMenuDTO = (permission: Partial<Permission>) => {
        return {
          id: permission.id,
          parentId: permission.parentId,
          name: permission.name,
          permission: permission.code,
          type: permission.type === 'MENU' ? 'menu' : permission.type === 'BUTTON' ? 'button' : 'api',
        };
      };

      const menuDTO = convertToMenuDTO(permissionData);
      let response;

      if (modalMode === 'add') {
        response = await fetch('/api/menus', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(menuDTO),
        });
      } else if (modalMode === 'edit' && selectedPermission) {
        response = await fetch('/api/menus', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(menuDTO),
        });
      } else {
        throw new Error('Invalid mode or missing permission');
      }

      if (!response.ok) {
        throw new Error('Failed to save permission');
      }

      const data = await response.json();
      if (data.success) {
        setIsModalOpen(false);
        setNotification({ 
          message: modalMode === 'add' ? '权限新增成功' : '权限编辑成功',
          type: 'success'
        });
        // 重新获取权限列表
        await fetchPermissions();
      } else {
        throw new Error(data.errMessage || '保存失败');
      }
    } catch (error: any) {
      console.error('Error saving permission:', error);
      setNotification({ 
        message: error.message || '保存权限失败',
        type: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleDeletePermission = async (id: number) => {
    if (window.confirm('删除后，关联该权限的角色将直接失去对应操作权限，是否确认删除？')) {
      setLoading(true);
      try {
        const response = await fetch(`/api/menus/delete`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ id })
        });

        if (!response.ok) {
          throw new Error('Failed to delete permission');
        }

        const data = await response.json();
        if (data.success) {
          setNotification({ 
            message: '权限删除成功',
            type: 'success'
          });
          // 重新获取权限列表
          await fetchPermissions();
        } else {
          throw new Error(data.errMessage || '删除失败');
        }
      } catch (error: any) {
        console.error('Error deleting permission:', error);
        setNotification({ 
          message: error.message || '删除权限失败',
          type: 'error'
        });
      } finally {
        setLoading(false);
      }
    }
  };

  const handleViewAssociatedRoles = (permission: Permission) => {
    setSelectedPermission(permission);
    setIsRoleModalOpen(true);
  };

  const flattenPermissions = (perms: Permission[]): Permission[] => {
    return perms.flatMap(perm => {
      return [perm, ...(perm.children ? flattenPermissions(perm.children) : [])];
    });
  };

  const filteredPermissions = flattenPermissions(permissions).filter(perm => {
    const matchesSearch = !searchKeyword || 
      perm.name.toLowerCase().includes(searchKeyword.toLowerCase()) ||
      perm.code.toLowerCase().includes(searchKeyword.toLowerCase());
    const matchesType = !filters.type || perm.type === filters.type;
    return matchesSearch && matchesType;
  });

  const renderPermissionTree = (perms: Permission[], level = 0) => {
    return perms.map(perm => (
      <React.Fragment key={perm.id}>
        <tr className="hover:bg-slate-50 transition-colors">
          <td className={`px-6 py-4 pl-${level * 4 + 6}`}>
            <div className="flex items-center gap-2">
              {perm.children && perm.children.length > 0 && (
                <button onClick={() => toggleExpand(perm.id)} className="p-1 hover:bg-slate-200 rounded transition-colors">
                  {expanded.includes(perm.id) ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
                </button>
              )}
              {!perm.children || perm.children.length === 0 && (
                <div className="w-4"></div>
              )}
              {perm.type === 'MENU' && <Key size={16} className="text-blue-600" />}
              {perm.type === 'BUTTON' && <Shield size={16} className="text-slate-400" />}
              {perm.type === 'API' && <ExternalLink size={16} className="text-green-600" />}
              <span className={level === 0 ? "font-semibold text-slate-900" : "text-sm text-slate-700"}>
                {perm.name}
              </span>
            </div>
          </td>
          <td className="px-6 py-4 text-sm font-mono text-slate-600">{perm.code}</td>
          <td className="px-6 py-4">
            <span className={cn(
              "px-2 py-1 text-xs font-bold rounded-md",
              perm.type === 'MENU' ? "bg-blue-50 text-blue-600" :
              perm.type === 'BUTTON' ? "bg-slate-100 text-slate-500" :
              "bg-green-50 text-green-600"
            )}>
              {perm.type === 'MENU' ? '菜单' : perm.type === 'BUTTON' ? '按钮' : '接口'}
            </span>
          </td>
          <td className="px-6 py-4">
            <span className="text-sm text-slate-600">{perm.parentName || '-'}</span>
          </td>
          <td className="px-6 py-4 text-sm text-slate-500">{perm.createTime}</td>
          <td className="px-6 py-4 text-right">
            <div className="flex items-center justify-end gap-3">
              <button 
                onClick={() => {
                  setSelectedPermission(perm);
                  setModalMode('edit');
                  setIsModalOpen(true);
                }}
                className="text-sm text-blue-600 hover:text-blue-800 font-medium"
              >
                编辑
              </button>
              <button 
                onClick={() => handleViewAssociatedRoles(perm)}
                className="text-sm text-purple-600 hover:text-purple-800 font-medium"
              >
                关联角色
              </button>
              <button 
                onClick={() => handleDeletePermission(perm.id)}
                className="text-sm text-red-600 hover:text-red-800 font-medium"
              >
                删除
              </button>
            </div>
          </td>
        </tr>
        {perm.children && perm.children.length > 0 && expanded.includes(perm.id) && (
          renderPermissionTree(perm.children, level + 1)
        )}
      </React.Fragment>
    ));
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">权限管理</h2>
          <p className="text-slate-500 mt-1">基于RBAC模型，精细化管控菜单、按钮及接口权限。</p>
        </div>
        <button 
          onClick={() => {
            setModalMode('add');
            setSelectedPermission(undefined);
            setIsModalOpen(true);
          }}
          className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
        >
          <Plus size={18} />
          新增权限
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex flex-wrap items-center gap-4 bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={16} className="text-slate-400" />
            <input 
              type="text" 
              placeholder="搜索权限名称或标识..." 
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
                    <label className="block text-sm font-medium text-slate-700 mb-1">权限类型</label>
                    <select 
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                      value={filters.type}
                      onChange={(e) => setFilters({ ...filters, type: e.target.value })}
                    >
                      <option value="">全部</option>
                      <option value="MENU">菜单</option>
                      <option value="BUTTON">按钮</option>
                      <option value="API">接口</option>
                    </select>
                  </div>
                  <div className="flex gap-2 mt-4">
                    <button 
                      onClick={() => setFilters({ type: '' })}
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

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b border-slate-200">
              <tr>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">权限名称2</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">权限标识</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">类型</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">所属菜单</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">创建时间</th>
                <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center">
                    <div className="flex items-center justify-center gap-2">
                      <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                      <span className="text-slate-600">加载中...</span>
                    </div>
                  </td>
                </tr>
              ) : permissions.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center">
                    <span className="text-slate-500">暂无权限数据</span>
                  </td>
                </tr>
              ) : (
                renderPermissionTree(permissions)
              )}
            </tbody>
          </table>
        </div>
      </div>

      <PermissionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSavePermission}
        permission={selectedPermission}
        mode={modalMode}
        allPermissions={flattenPermissions(permissions)}
      />

      {selectedPermission && (
        <RoleAssociationModal
          isOpen={isRoleModalOpen}
          onClose={() => setIsRoleModalOpen(false)}
          permission={selectedPermission}
        />
      )}

      {notification && (
        <Notification 
          message={notification.message} 
          type={notification.type} 
        />
      )}
    </div>
  );
}