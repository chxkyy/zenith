import React, { useState } from 'react';
import { Search, Plus, Menu as MenuIcon, Layout, Square, ChevronRight, ChevronDown, Trash2, Edit, MoveUp, MoveDown, Filter, X, Eye, EyeOff, Settings, Shield } from 'lucide-react';
import { cn } from '../lib/utils';
import Notification from '../components/Notification';

interface Menu {
  id: number;
  name: string;
  type: 'DIR' | 'MENU' | 'BUTTON';
  path: string;
  icon: string;
  parentId: number | null;
  parentName?: string;
  order: number;
  status: number;
  createTime: string;
  remark?: string;
  children?: Menu[];
}

interface MenuModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (menu: Partial<Menu>) => void;
  menu?: Menu;
  mode: 'add' | 'edit';
  allMenus: Menu[];
}

const MenuModal: React.FC<MenuModalProps> = ({ isOpen, onClose, onSave, menu, mode, allMenus }) => {
  const [formData, setFormData] = useState<Partial<Menu>>({
    id: menu?.id,
    name: menu?.name || '',
    type: menu?.type || 'MENU',
    parentId: menu?.parentId || null,
    path: menu?.path || '',
    icon: menu?.icon || 'LayoutDashboard',
    order: menu?.order || 1,
    status: menu?.status || 1,
    remark: menu?.remark || ''
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <h3 className="text-lg font-bold text-slate-900">
            {mode === 'add' ? '新增菜单' : '编辑菜单'}
          </h3>
          <button onClick={onClose} className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-slate-600">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">菜单名称</label>
            <input
              required
              type="text"
              value={formData.name || ''}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
              placeholder="请输入菜单名称"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">菜单类型</label>
            <select
              required
              value={formData.type || 'MENU'}
              onChange={(e) => setFormData({ ...formData, type: e.target.value as 'DIR' | 'MENU' | 'BUTTON' })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 transition-all bg-white"
            >
              <option value="DIR">目录</option>
              <option value="MENU">菜单</option>
              <option value="BUTTON">按钮</option>
            </select>
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">父菜单</label>
            <select
              value={formData.parentId || ''}
              onChange={(e) => setFormData({ ...formData, parentId: e.target.value ? parseInt(e.target.value) : null })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 transition-all bg-white"
            >
              <option value="">顶级菜单</option>
              {allMenus
                .filter(m => m.type === 'DIR' && (!formData.id || m.id !== formData.id))
                .map(parentMenu => (
                  <option key={parentMenu.id} value={parentMenu.id}>
                    {parentMenu.name}
                  </option>
                ))}
            </select>
          </div>

          {(formData.type === 'MENU' || formData.type === 'DIR') && (
            <div className="space-y-1.5">
              <label className="text-sm font-semibold text-slate-700">路由路径</label>
              <input
                required
                type="text"
                value={formData.path || ''}
                onChange={(e) => setFormData({ ...formData, path: e.target.value })}
                className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
                placeholder="请输入路由路径，如 /dashboard"
              />
            </div>
          )}

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">图标</label>
            <input
              type="text"
              value={formData.icon || ''}
              onChange={(e) => setFormData({ ...formData, icon: e.target.value })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
              placeholder="请输入图标名称，如 LayoutDashboard"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">排序</label>
            <input
              required
              type="number"
              min="0"
              value={formData.order || 1}
              onChange={(e) => setFormData({ ...formData, order: parseInt(e.target.value) || 0 })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
              placeholder="请输入排序数字"
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">状态</label>
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={() => setFormData({ ...formData, status: 1 })}
                className={`flex-1 py-2 rounded-lg font-medium transition-all ${formData.status === 1 ? 'bg-emerald-600 text-white' : 'bg-slate-100 text-slate-600'}`}
              >
                启用
              </button>
              <button
                type="button"
                onClick={() => setFormData({ ...formData, status: 0 })}
                className={`flex-1 py-2 rounded-lg font-medium transition-all ${formData.status === 0 ? 'bg-slate-600 text-white' : 'bg-slate-100 text-slate-600'}`}
              >
                禁用
              </button>
            </div>
          </div>

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
              {mode === 'add' ? '保存菜单' : '更新菜单'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

interface Permission {
  id: number;
  name: string;
  code: string;
  type: 'FUNCTION' | 'FIELD';
  menuId: number;
  status: number;
}

interface PermissionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (permission: Partial<Permission>) => void;
  permission?: Permission;
  mode: 'add' | 'edit';
  menuId: number;
  permissionType: 'FUNCTION' | 'FIELD';
}

const PermissionModal: React.FC<PermissionModalProps> = ({ isOpen, onClose, onSave, permission, mode, menuId, permissionType }) => {
  const [formData, setFormData] = useState<Partial<Permission>>({
    id: permission?.id,
    name: permission?.name || '',
    code: permission?.code || '',
    type: permissionType,
    menuId,
    status: permission?.status || 1
  });

  // 当 permission 属性变化时，更新 formData
  React.useEffect(() => {
    if (permission) {
      setFormData({
        id: permission.id,
        name: permission.name || '',
        code: permission.code || '',
        type: permission.type || permissionType,
        menuId: permission.menuId || menuId,
        status: permission.status || 1
      });
    } else {
      setFormData({
        id: undefined,
        name: '',
        code: '',
        type: permissionType,
        menuId,
        status: 1
      });
    }
  }, [permission, menuId, permissionType]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <h3 className="text-lg font-bold text-slate-900">
            {mode === 'add' ? '新增' : '编辑'} {permissionType === 'FUNCTION' ? '功能' : '字段'}权限
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
              placeholder={`请输入${permissionType === 'FUNCTION' ? '功能' : '字段'}权限名称`}
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
              placeholder={`请输入${permissionType === 'FUNCTION' ? '功能' : '字段'}权限标识（大写英文+下划线）`}
              disabled={mode === 'edit'}
            />
          </div>

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">状态</label>
            <div className="flex items-center gap-2">
              <button
                type="button"
                onClick={() => setFormData({ ...formData, status: 1 })}
                className={`flex-1 py-2 rounded-lg font-medium transition-all ${formData.status === 1 ? 'bg-emerald-600 text-white' : 'bg-slate-100 text-slate-600'}`}
              >
                启用
              </button>
              <button
                type="button"
                onClick={() => setFormData({ ...formData, status: 0 })}
                className={`flex-1 py-2 rounded-lg font-medium transition-all ${formData.status === 0 ? 'bg-slate-600 text-white' : 'bg-slate-100 text-slate-600'}`}
              >
                禁用
              </button>
            </div>
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
              {mode === 'add' ? '保存' : '更新'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

const PermissionManagement: React.FC<{ selectedMenu: Menu | null }> = ({ selectedMenu }) => {
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'FUNCTION' | 'FIELD'>('FUNCTION');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedPermission, setSelectedPermission] = useState<Permission | null>(null);
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' | 'info' } | null>(null);

  // 模拟获取权限数据
  React.useEffect(() => {
    if (selectedMenu) {
      setLoading(true);
      // 模拟API调用
      setTimeout(() => {
        setPermissions([
          {
            id: 1,
            name: '查看',
            code: 'VIEW',
            type: 'FUNCTION',
            menuId: selectedMenu.id,
            status: 1
          },
          {
            id: 2,
            name: '新增',
            code: 'ADD',
            type: 'FUNCTION',
            menuId: selectedMenu.id,
            status: 1
          },
          {
            id: 3,
            name: '编辑',
            code: 'EDIT',
            type: 'FUNCTION',
            menuId: selectedMenu.id,
            status: 1
          },
          {
            id: 4,
            name: '删除',
            code: 'DELETE',
            type: 'FUNCTION',
            menuId: selectedMenu.id,
            status: 1
          },
          {
            id: 5,
            name: '用户名',
            code: 'USERNAME',
            type: 'FIELD',
            menuId: selectedMenu.id,
            status: 1
          },
          {
            id: 6,
            name: '邮箱',
            code: 'EMAIL',
            type: 'FIELD',
            menuId: selectedMenu.id,
            status: 1
          },
          {
            id: 7,
            name: '手机号',
            code: 'PHONE',
            type: 'FIELD',
            menuId: selectedMenu.id,
            status: 0
          }
        ]);
        setLoading(false);
      }, 500);
    } else {
      setPermissions([]);
    }
  }, [selectedMenu]);

  const handleSavePermission = (permissionData: Partial<Permission>) => {
    setLoading(true);
    // 模拟API调用
    setTimeout(() => {
      if (modalMode === 'add') {
        const newPermission: Permission = {
          id: permissions.length + 1,
          name: permissionData.name || '',
          code: permissionData.code || '',
          type: permissionData.type || 'FUNCTION',
          menuId: permissionData.menuId || 0,
          status: permissionData.status || 1
        };
        setPermissions([...permissions, newPermission]);
        setNotification({ message: '权限新增成功', type: 'success' });
      } else if (modalMode === 'edit' && selectedPermission) {
        setPermissions(permissions.map(p => 
          p.id === selectedPermission.id ? { ...p, ...permissionData } : p
        ));
        setNotification({ message: '权限编辑成功', type: 'success' });
      }
      setIsModalOpen(false);
      setLoading(false);
    }, 500);
  };

  const handleDeletePermission = (id: number) => {
    if (window.confirm('删除后权限数据不可恢复，是否确认删除？')) {
      setLoading(true);
      // 模拟API调用
      setTimeout(() => {
        setPermissions(permissions.filter(p => p.id !== id));
        setNotification({ message: '权限删除成功', type: 'success' });
        setLoading(false);
      }, 500);
    }
  };

  const handleChangeStatus = (id: number, currentStatus: number) => {
    const newStatus = currentStatus === 1 ? 0 : 1;
    setLoading(true);
    // 模拟API调用
    setTimeout(() => {
      setPermissions(permissions.map(p => 
        p.id === id ? { ...p, status: newStatus } : p
      ));
      setNotification({ message: `权限状态已切换为${newStatus === 1 ? '启用' : '禁用'}`, type: 'success' });
      setLoading(false);
    }, 500);
  };

  if (!selectedMenu) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <Settings size={48} className="mx-auto text-slate-300 mb-4" />
          <p className="text-slate-500">请选择一个菜单以管理其权限</p>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-lg font-bold text-slate-900">{selectedMenu.name} - 权限管理</h3>
          <p className="text-slate-500 mt-1">管理该菜单下的功能权限和字段权限</p>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="border-b border-slate-200">
          <div className="flex">
            <button
              onClick={() => setActiveTab('FUNCTION')}
              className={`px-6 py-4 font-medium transition-colors ${activeTab === 'FUNCTION' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-slate-600 hover:text-slate-900'}`}
            >
              功能权限
            </button>
            <button
              onClick={() => setActiveTab('FIELD')}
              className={`px-6 py-4 font-medium transition-colors ${activeTab === 'FIELD' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-slate-600 hover:text-slate-900'}`}
            >
              字段权限
            </button>
          </div>
        </div>
        <div className="p-4">
          <div className="flex justify-between items-center mb-4">
            <h4 className="text-sm font-semibold text-slate-700">
              {activeTab === 'FUNCTION' ? '功能权限' : '字段权限'}
            </h4>
            <button 
              onClick={() => {
                setModalMode('add');
                setSelectedPermission(null);
                setIsModalOpen(true);
              }}
              className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition-all shadow-md"
            >
              <Plus size={16} />
              新增{activeTab === 'FUNCTION' ? '功能' : '字段'}权限
            </button>
          </div>
          {loading ? (
            <div className="flex items-center justify-center py-8">
              <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
            </div>
          ) : (
            <div className="space-y-2">
              {permissions
                .filter(p => p.type === activeTab)
                .map(permission => (
                  <div key={permission.id} className="flex items-center justify-between p-3 border border-slate-200 rounded-lg hover:bg-slate-50 transition-colors">
                    <div className="flex items-center gap-3">
                      <div className={cn(
                        "px-3 py-1 rounded-full text-xs font-medium",
                        permission.status === 1 ? "bg-emerald-100 text-emerald-700" : "bg-slate-100 text-slate-600"
                      )}>
                        {permission.code}
                      </div>
                      <span className="text-sm font-medium text-slate-900">{permission.name}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <button 
                        onClick={() => handleChangeStatus(permission.id, permission.status)}
                        className={cn(
                          "p-1.5 text-sm font-medium rounded-lg transition-colors px-3 py-1",
                          permission.status === 1 ? "text-emerald-600 bg-emerald-50" : "text-slate-600 bg-slate-100"
                        )}
                        title={permission.status === 1 ? '禁用' : '启用'}
                      >
                        {permission.status === 1 ? '启用' : '禁用'}
                      </button>
                      <button 
                        onClick={() => {
                          setSelectedPermission(permission);
                          setModalMode('edit');
                          setIsModalOpen(true);
                        }}
                        className="p-1.5 text-sm font-medium text-blue-600 bg-blue-50 rounded-lg transition-colors px-3 py-1"
                        title="编辑"
                      >
                        编辑
                      </button>
                      <button 
                        onClick={() => handleDeletePermission(permission.id)}
                        className="p-1.5 text-sm font-medium text-red-600 bg-red-50 rounded-lg transition-colors px-3 py-1"
                        title="删除"
                      >
                        删除
                      </button>
                    </div>
                  </div>
                ))}
            </div>
          )}
        </div>
      </div>

      <PermissionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSavePermission}
        permission={selectedPermission || undefined}
        mode={modalMode}
        menuId={selectedMenu.id}
        permissionType={activeTab}
      />

      {notification && (
        <div className={`fixed top-4 right-4 px-4 py-3 rounded-lg shadow-lg ${notification.type === 'success' ? 'bg-emerald-100 text-emerald-700' : notification.type === 'error' ? 'bg-red-100 text-red-700' : 'bg-blue-100 text-blue-700'}`}>
          {notification.message}
        </div>
      )}
    </div>
  );
};

export default function MenuManagement() {
  const [menus, setMenus] = useState<Menu[]>([]);
  const [expanded, setExpanded] = useState<number[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedMenu, setSelectedMenu] = useState<Menu | null>(null);
  const [rightClickMenu, setRightClickMenu] = useState<{ x: number; y: number; menu: Menu } | null>(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [filters, setFilters] = useState({
    status: '',
  });
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' | 'info' } | null>(null);
  const [loading, setLoading] = useState(true);

  const toggleExpand = (id: number) => {
    setExpanded(prev => prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]);
  };

  const fetchMenus = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/menus');
      if (!response.ok) {
        throw new Error('Failed to fetch menus');
      }
      const data = await response.json();
      
      // 使用后端返回的数据
      const menuData = data.success && data.data ? data.data : [];
      
      // 转换菜单数据为前端需要的结构
      const convertToMenus = (menuDTOs: any[]): { menus: Menu[]; expandedIds: number[] } => {
        // 首先创建所有菜单的映射
        const menuMap = new Map<number, Menu>();
        const expandedIds: number[] = [];
        
        menuDTOs.forEach(menuDTO => {
          const menu: Menu = {
            id: menuDTO.id,
            name: menuDTO.name,
            type: menuDTO.type === 'menu' ? 'MENU' : menuDTO.type === 'button' ? 'BUTTON' : 'DIR',
            path: menuDTO.path || '',
            icon: menuDTO.icon || 'LayoutDashboard',
            parentId: menuDTO.parentId,
            order: menuDTO.sort || 0,
            status: 1, // 默认为启用状态
            createTime: new Date().toLocaleString('zh-CN'),
            remark: menuDTO.remark,
            children: []
          };
          menuMap.set(menu.id, menu);
          expandedIds.push(menu.id); // 收集所有菜单ID，用于默认展开
        });

        // 构建树形结构
        const rootMenus: Menu[] = [];
        menuMap.forEach(menu => {
          if (!menu.parentId) {
            rootMenus.push(menu);
          } else {
            const parentMenu = menuMap.get(menu.parentId);
            if (parentMenu) {
              parentMenu.children?.push(menu);
              menu.parentName = parentMenu.name;
            }
          }
        });

        // 对菜单进行排序
        const sortMenus = (menuList: Menu[]) => {
          menuList.sort((a, b) => (a.order || 0) - (b.order || 0));
          menuList.forEach(menu => {
            if (menu.children && menu.children.length > 0) {
              sortMenus(menu.children);
            }
          });
        };

        sortMenus(rootMenus);
        return { menus: rootMenus, expandedIds };
      };

      const result = convertToMenus(menuData);
      setMenus(result.menus);
      setExpanded(result.expandedIds); // 设置所有菜单为默认展开状态
    } catch (error) {
      console.error('Error fetching menus:', error);
      setNotification({ 
        message: '获取菜单列表失败',
        type: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  // 组件挂载时获取菜单列表
  React.useEffect(() => {
    fetchMenus();
  }, []);

  const handleSaveMenu = async (menuData: Partial<Menu>) => {
    setLoading(true);
    try {
      // 转换前端菜单数据为后端需要的格式
      const convertToMenuDTO = (menu: Partial<Menu>) => {
        return {
          id: menu.id,
          parentId: menu.parentId,
          name: menu.name,
          path: menu.path,
          component: menu.type === 'MENU' ? 'Layout' : '',
          icon: menu.icon,
          sort: menu.order,
          type: menu.type === 'DIR' ? 'menu' : menu.type === 'MENU' ? 'menu' : 'button',
          permission: menu.type === 'BUTTON' ? menu.name : '',
          remark: menu.remark
        };
      };

      const menuDTO = convertToMenuDTO(menuData);
      let response;

      if (modalMode === 'add') {
        response = await fetch('/api/menus', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(menuDTO),
        });
      } else if (modalMode === 'edit' && selectedMenu) {
        response = await fetch('/api/menus', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(menuDTO),
        });
      } else {
        throw new Error('Invalid mode or missing menu');
      }

      if (!response.ok) {
        throw new Error('Failed to save menu');
      }

      const data = await response.json();
      if (data.success) {
        setIsModalOpen(false);
        setRightClickMenu(null);
        setNotification({ 
          message: modalMode === 'add' ? '菜单新增成功' : '菜单编辑成功',
          type: 'success'
        });
        // 重新获取菜单列表
        await fetchMenus();
      } else {
        throw new Error(data.errMessage || '保存失败');
      }
    } catch (error: any) {
      console.error('Error saving menu:', error);
      setNotification({ 
        message: error.message || '保存菜单失败',
        type: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteMenu = async (id: number) => {
    if (window.confirm('删除后，子菜单、关联权限及按钮将一并删除且不可恢复，是否确认删除？')) {
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
          throw new Error('Failed to delete menu');
        }

        const data = await response.json();
        if (data.success) {
          setRightClickMenu(null);
          setSelectedMenu(null);
          setNotification({ 
            message: '菜单删除成功',
            type: 'success'
          });
          // 重新获取菜单列表
          await fetchMenus();
        } else {
          throw new Error(data.errMessage || '删除失败');
        }
      } catch (error: any) {
        console.error('Error deleting menu:', error);
        setNotification({ 
          message: error.message || '删除菜单失败',
          type: 'error'
        });
      } finally {
        setLoading(false);
      }
    }
  };

  const handleChangeStatus = async (id: number, currentStatus: number) => {
    const newStatus = currentStatus === 1 ? 0 : 1;
    setLoading(true);
    try {
      // 这里简化处理，实际应该调用后端 API 来更新状态
      // 由于后端 API 尚未实现状态更新功能，这里只是模拟成功
      setTimeout(() => {
        setNotification({ 
          message: `菜单状态已切换为${newStatus === 1 ? '启用' : '禁用'}`,
          type: 'success'
        });
        setLoading(false);
      }, 500);
    } catch (error: any) {
      console.error('Error changing menu status:', error);
      setNotification({ 
        message: error.message || '切换菜单状态失败',
        type: 'error'
      });
      setLoading(false);
    }
  };

  const handleRightClick = (e: React.MouseEvent, menu: Menu) => {
    e.preventDefault();
    setRightClickMenu({
      x: e.clientX,
      y: e.clientY,
      menu
    });
  };

  const handleClickOutside = (e: React.MouseEvent) => {
    setRightClickMenu(null);
  };

  // 监听点击事件，点击外部关闭右键菜单
  React.useEffect(() => {
    document.addEventListener('click', handleClickOutside);
    return () => {
      document.removeEventListener('click', handleClickOutside);
    };
  }, []);

  const renderMenuTree = (menuList: Menu[], level = 0) => {
    return menuList.map(menu => (
      <React.Fragment key={menu.id}>
        <div 
          className={`flex items-center gap-2 p-2 rounded-lg transition-colors cursor-pointer ${selectedMenu?.id === menu.id ? 'bg-blue-50 text-blue-600' : 'hover:bg-slate-100'}`}
          onClick={() => setSelectedMenu(menu)}
          onContextMenu={(e) => handleRightClick(e, menu)}
        >
          {menu.children && menu.children.length > 0 && (
            <button 
              onClick={(e) => {
                e.stopPropagation();
                toggleExpand(menu.id);
              }} 
              className="p-1 hover:bg-slate-200 rounded transition-colors"
            >
              {expanded.includes(menu.id) ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
            </button>
          )}
          {!menu.children || menu.children.length === 0 && (
            <div className="w-4"></div>
          )}
          {menu.type === 'DIR' && <Layout size={16} className="text-blue-600" />}
          {menu.type === 'MENU' && <MenuIcon size={16} className="text-slate-600" />}
          {menu.type === 'BUTTON' && <Square size={16} className="text-slate-400" />}
          <span className={level === 0 ? "font-semibold text-slate-900" : "text-sm text-slate-700"}>
            {menu.name}
          </span>
        </div>
        {menu.children && menu.children.length > 0 && expanded.includes(menu.id) && (
          <div className={`ml-4 border-l-2 border-slate-200 pl-2 mt-1`}>
            {renderMenuTree(menu.children, level + 1)}
          </div>
        )}
      </React.Fragment>
    ));
  };

  return (
    <div className="flex h-screen bg-slate-50">
      {/* 左侧菜单树 */}
      <div className="w-80 bg-white border-r border-slate-200 flex flex-col">
        <div className="p-4 border-b border-slate-200">
          <h2 className="text-xl font-bold text-slate-900">菜单管理</h2>
          <p className="text-sm text-slate-500 mt-1">配置系统左侧导航菜单</p>
        </div>

        <div className="p-4 border-b border-slate-200">
          <div className="flex items-center gap-2 bg-slate-100 px-3 py-2 rounded-lg">
            <Search size={16} className="text-slate-400" />
            <input 
              type="text" 
              placeholder="搜索菜单..." 
              className="text-sm outline-none w-full bg-transparent"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
            />
          </div>
        </div>

        <div className="p-4 border-b border-slate-200 flex justify-between items-center">
          <button 
            onClick={() => {
              setModalMode('add');
              setSelectedMenu(null);
              setIsModalOpen(true);
            }}
            className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition-all shadow-md"
          >
            <Plus size={16} />
            新增菜单
          </button>
          <button 
            onClick={() => fetchMenus()}
            className="p-2 text-slate-500 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-colors"
            title="刷新"
          >
            <MoveUp size={16} />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto p-4">
          {loading ? (
            <div className="flex items-center justify-center h-40">
              <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
            </div>
          ) : menus.length === 0 ? (
            <div className="text-center py-12">
              <MenuIcon size={48} className="mx-auto text-slate-300 mb-4" />
              <p className="text-slate-500">暂无菜单数据</p>
            </div>
          ) : (
            renderMenuTree(menus)
          )}
        </div>
      </div>

      {/* 右侧权限管理 */}
      <div className="flex-1 overflow-y-auto">
        <PermissionManagement selectedMenu={selectedMenu} />
      </div>

      {/* 右键菜单 */}
      {rightClickMenu && (
        <div 
          className="fixed z-50 bg-white rounded-lg shadow-lg border border-slate-200 py-2 min-w-48"
          style={{ left: rightClickMenu.x, top: rightClickMenu.y }}
        >
          <button 
            onClick={() => {
              setSelectedMenu(rightClickMenu.menu);
              setModalMode('edit');
              setIsModalOpen(true);
            }}
            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
          >
            <Edit size={14} />
            编辑
          </button>
          <button 
            onClick={() => handleChangeStatus(rightClickMenu.menu.id, rightClickMenu.menu.status)}
            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
          >
            <Eye size={14} />
            {rightClickMenu.menu.status === 1 ? '禁用' : '启用'}
          </button>
          <div className="border-t border-slate-100 my-1"></div>
          <button 
            onClick={() => handleDeleteMenu(rightClickMenu.menu.id)}
            className="flex items-center gap-2 w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
          >
            <Trash2 size={14} />
            删除
          </button>
        </div>
      )}

      <MenuModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSaveMenu}
        menu={selectedMenu || undefined}
        mode={modalMode}
        allMenus={menus}
      />

      {notification && (
        <Notification 
          message={notification.message} 
          type={notification.type} 
        />
      )}
    </div>
  );
}
