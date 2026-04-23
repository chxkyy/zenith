import React, { useState, useEffect } from 'react';
import { Search, Plus, Menu as MenuIcon, Layout, Square, ChevronRight, ChevronDown, Trash2, Edit, MoveUp, MoveDown, Filter, X, Eye, EyeOff } from 'lucide-react';
import { cn } from '../lib/utils';
import Notification from './Notification';

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

  // 当 menu 属性变化时，更新 formData
  useEffect(() => {
    if (menu) {
      setFormData({
        id: menu.id,
        name: menu.name || '',
        type: menu.type || 'MENU',
        parentId: menu.parentId || null,
        path: menu.path || '',
        icon: menu.icon || 'LayoutDashboard',
        order: menu.order || 1,
        status: menu.status || 1,
        remark: menu.remark || ''
      });
    }
  }, [menu]);

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

export default function MenuTable() {
  const [menus, setMenus] = useState<Menu[]>([]);
  const [expanded, setExpanded] = useState<number[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
  const [selectedMenu, setSelectedMenu] = useState<Menu | undefined>();
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
      if (data.success && data.data) {
        // 转换后端返回的菜单数据为前端需要的结构
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

        const result = convertToMenus(data.data);
        setMenus(result.menus);
        setExpanded(result.expandedIds); // 设置所有菜单为默认展开状态
      }
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
  useEffect(() => {
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

  const handleMoveUp = async (menu: Menu) => {
    // 这里简化处理，实际应该调用后端 API 来更新排序
    // 由于后端 API 尚未实现排序功能，这里只是模拟成功
    setLoading(true);
    try {
      setTimeout(() => {
        setNotification({ 
          message: '菜单排序已更新',
          type: 'success'
        });
        setLoading(false);
      }, 500);
    } catch (error: any) {
      console.error('Error moving menu up:', error);
      setNotification({ 
        message: error.message || '菜单上移失败',
        type: 'error'
      });
      setLoading(false);
    }
  };

  const handleMoveDown = async (menu: Menu) => {
    // 这里简化处理，实际应该调用后端 API 来更新排序
    // 由于后端 API 尚未实现排序功能，这里只是模拟成功
    setLoading(true);
    try {
      setTimeout(() => {
        setNotification({ 
          message: '菜单排序已更新',
          type: 'success'
        });
        setLoading(false);
      }, 500);
    } catch (error: any) {
      console.error('Error moving menu down:', error);
      setNotification({ 
        message: error.message || '菜单下移失败',
        type: 'error'
      });
      setLoading(false);
    }
  };

  const flattenMenus = (menuList: Menu[]): Menu[] => {
    return menuList.flatMap(menu => {
      return [menu, ...(menu.children ? flattenMenus(menu.children) : [])];
    });
  };

  const filteredMenus = flattenMenus(menus).filter(menu => {
    const matchesSearch = !searchKeyword || 
      menu.name.toLowerCase().includes(searchKeyword.toLowerCase()) ||
      menu.path.toLowerCase().includes(searchKeyword.toLowerCase());
    const matchesStatus = !filters.status || menu.status.toString() === filters.status;
    return matchesSearch && matchesStatus;
  });

  const renderMenuTree = (menuList: Menu[], level = 0) => {
    return menuList.map(menu => (
      <React.Fragment key={menu.id}>
        <tr className="hover:bg-slate-50 transition-colors">
          <td className={`px-6 py-4 pl-${level * 4 + 6}`}>
            <div className="flex items-center gap-2">
              {menu.children && menu.children.length > 0 && (
                <button onClick={() => toggleExpand(menu.id)} className="p-1 hover:bg-slate-200 rounded transition-colors">
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
          </td>
          <td className="px-6 py-4">
            <span className={cn(
              "px-2 py-1 text-xs font-bold rounded-md",
              menu.type === 'DIR' ? "bg-slate-100 text-slate-600" :
              menu.type === 'MENU' ? "bg-blue-50 text-blue-600" :
              "bg-green-50 text-green-600"
            )}>
              {menu.type === 'DIR' ? '目录' : menu.type === 'MENU' ? '菜单' : '按钮'}
            </span>
          </td>
          <td className="px-6 py-4 text-sm text-slate-500">{menu.path || '-'}</td>
          <td className="px-6 py-4 text-sm text-slate-500">{menu.icon || '-'}</td>
          <td className="px-6 py-4 text-sm text-slate-500">{menu.order}</td>
          <td className="px-6 py-4">
            <button 
              onClick={() => handleChangeStatus(menu.id, menu.status)}
              className={cn(
                "px-2 py-1 text-xs font-bold rounded-md transition-colors",
                menu.status === 1 ? "bg-emerald-50 text-emerald-600 hover:bg-emerald-100" : "bg-slate-50 text-slate-600 hover:bg-slate-100"
              )}
            >
              {menu.status === 1 ? '启用' : '禁用'}
            </button>
          </td>
          <td className="px-6 py-4 text-sm text-slate-500">{menu.createUserId || '-'}</td>
          <td className="px-6 py-4 text-sm text-slate-500">{formatDateTime(menu.createdTime)}</td>
          <td className="px-6 py-4 text-sm text-slate-500">{menu.updateUserId || '-'}</td>
          <td className="px-6 py-4 text-sm text-slate-500">{formatDateTime(menu.updateTime)}</td>
          <td className="px-6 py-4 text-right">
            <div className="flex items-center justify-end gap-1">
              {level === 0 && (
                <>
                  <button 
                    onClick={() => handleMoveUp(menu)}
                    className="p-1.5 text-slate-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                    title="上移"
                  >
                    <MoveUp size={16} />
                  </button>
                  <button 
                    onClick={() => handleMoveDown(menu)}
                    className="p-1.5 text-slate-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                    title="下移"
                  >
                    <MoveDown size={16} />
                  </button>
                </>
              )}
              <button 
                onClick={() => {
                  setSelectedMenu(menu);
                  setModalMode('edit');
                  setIsModalOpen(true);
                }}
                className="p-1.5 text-slate-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                title="编辑"
              >
                <Edit size={16} />
              </button>
              <button 
                onClick={() => handleDeleteMenu(menu.id)}
                className="p-1.5 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                title="删除"
              >
                <Trash2 size={16} />
              </button>
            </div>
          </td>
        </tr>
        {menu.children && menu.children.length > 0 && expanded.includes(menu.id) && (
          renderMenuTree(menu.children, level + 1)
        )}
      </React.Fragment>
    ));
  };

  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">菜单管理</h2>
          <p className="text-slate-500 mt-1">配置系统左侧导航菜单，支持多级目录及按钮权限关联。</p>
        </div>
        <button 
          onClick={() => {
            setModalMode('add');
            setSelectedMenu(undefined);
            setIsModalOpen(true);
          }}
          className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
        >
          <Plus size={18} />
          新增菜单
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex flex-wrap items-center gap-4 bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={16} className="text-slate-400" />
            <input 
              type="text" 
              placeholder="搜索菜单名称或路径..." 
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
                    <label className="block text-sm font-medium text-slate-700 mb-1">菜单状态</label>
                    <select 
                      className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm"
                      value={filters.status}
                      onChange={(e) => setFilters({ ...filters, status: e.target.value })}
                    >
                      <option value="">全部</option>
                      <option value="1">启用</option>
                      <option value="0">禁用</option>
                    </select>
                  </div>
                  <div className="flex gap-2 mt-4">
                    <button 
                      onClick={() => setFilters({ status: '' })}
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
            <thead>
              <tr className="bg-slate-50 text-slate-500 text-xs uppercase font-bold tracking-wider">
                <th className="px-6 py-4">菜单名称</th>
                <th className="px-6 py-4">路径</th>
                <th className="px-6 py-4">组件</th>
                <th className="px-6 py-4">图标</th>
                <th className="px-6 py-4">排序</th>
                <th className="px-6 py-4">类型</th>
                <th className="px-6 py-4">权限标识</th>
                <th className="px-6 py-4">创建人</th>
                <th className="px-6 py-4">创建时间</th>
                <th className="px-6 py-4">修改人</th>
                <th className="px-6 py-4">修改时间</th>
                <th className="px-6 py-4 text-right">操作</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={12} className="px-6 py-12 text-center">
                    <div className="flex items-center justify-center gap-2">
                      <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                      <span className="text-slate-600">加载中...</span>
                    </div>
                  </td>
                </tr>
              ) : menus.length === 0 ? (
                <tr>
                  <td colSpan={12} className="px-6 py-12 text-center">
                    <span className="text-slate-500">暂无菜单数据</span>
                  </td>
                </tr>
              ) : (
                renderMenuTree(menus)
              )}
            </tbody>
          </table>
        </div>
      </div>

      <MenuModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSaveMenu}
        menu={selectedMenu}
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