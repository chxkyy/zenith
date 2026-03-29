import React, { useState, useEffect } from 'react';
import { X, CheckSquare, Square, ChevronDown, ChevronRight, Search } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

interface Permission {
  id: string;
  name: string;
  code: string;
  type: 'menu' | 'button';
  children?: Permission[];
}

// 模拟权限数据
const mockPermissions: Permission[] = [
  {
    id: '1',
    name: '系统管理',
    code: 'SYSTEM_MANAGE',
    type: 'menu',
    children: [
      {
        id: '1-1',
        name: '用户管理',
        code: 'USER_MANAGE',
        type: 'menu',
        children: [
          {
            id: '1-1-1',
            name: '查看用户',
            code: 'USER_VIEW',
            type: 'button'
          },
          {
            id: '1-1-2',
            name: '添加用户',
            code: 'USER_ADD',
            type: 'button'
          },
          {
            id: '1-1-3',
            name: '编辑用户',
            code: 'USER_EDIT',
            type: 'button'
          },
          {
            id: '1-1-4',
            name: '删除用户',
            code: 'USER_DELETE',
            type: 'button'
          }
        ]
      },
      {
        id: '1-2',
        name: '角色管理',
        code: 'ROLE_MANAGE',
        type: 'menu',
        children: [
          {
            id: '1-2-1',
            name: '查看角色',
            code: 'ROLE_VIEW',
            type: 'button'
          },
          {
            id: '1-2-2',
            name: '添加角色',
            code: 'ROLE_ADD',
            type: 'button'
          },
          {
            id: '1-2-3',
            name: '编辑角色',
            code: 'ROLE_EDIT',
            type: 'button'
          },
          {
            id: '1-2-4',
            name: '删除角色',
            code: 'ROLE_DELETE',
            type: 'button'
          }
        ]
      }
    ]
  },
  {
    id: '2',
    name: '内容管理',
    code: 'CONTENT_MANAGE',
    type: 'menu',
    children: [
      {
        id: '2-1',
        name: '文章管理',
        code: 'ARTICLE_MANAGE',
        type: 'menu',
        children: [
          {
            id: '2-1-1',
            name: '查看文章',
            code: 'ARTICLE_VIEW',
            type: 'button'
          },
          {
            id: '2-1-2',
            name: '添加文章',
            code: 'ARTICLE_ADD',
            type: 'button'
          },
          {
            id: '2-1-3',
            name: '编辑文章',
            code: 'ARTICLE_EDIT',
            type: 'button'
          },
          {
            id: '2-1-4',
            name: '删除文章',
            code: 'ARTICLE_DELETE',
            type: 'button'
          }
        ]
      }
    ]
  }
];

// 默认空数组
const DEFAULT_ASSIGNED_PERMISSIONS: string[] = [];

interface PermissionAssignModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (permissions: string[]) => void;
  roleId: number;
  assignedPermissions?: string[];
}

export default function PermissionAssignModal({ isOpen, onClose, onSave, roleId, assignedPermissions = DEFAULT_ASSIGNED_PERMISSIONS }: PermissionAssignModalProps) {
  const [selectedPermissions, setSelectedPermissions] = useState<string[]>(assignedPermissions);
  const [expandedMenus, setExpandedMenus] = useState<Set<string>>(new Set());
  const [searchTerm, setSearchTerm] = useState('');
  const [permissions, setPermissions] = useState<Permission[]>(mockPermissions);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setSelectedPermissions(assignedPermissions);
  }, [assignedPermissions]);

  const toggleMenu = (menuId: string) => {
    setExpandedMenus(prev => {
      const newSet = new Set(prev);
      if (newSet.has(menuId)) {
        newSet.delete(menuId);
      } else {
        newSet.add(menuId);
      }
      return newSet;
    });
  };

  const handlePermissionToggle = (permissionCode: string, parentCode?: string) => {
    setSelectedPermissions(prev => {
      const newSet = new Set(prev);
      if (newSet.has(permissionCode)) {
        newSet.delete(permissionCode);
      } else {
        newSet.add(permissionCode);
        // 如果是按钮权限，自动勾选父菜单
        if (parentCode && !newSet.has(parentCode)) {
          newSet.add(parentCode);
        }
      }
      return Array.from(newSet);
    });
  };

  const handleMenuToggle = (menuCode: string, children: Permission[]) => {
    setSelectedPermissions(prev => {
      const newSet = new Set(prev);
      const isChecked = newSet.has(menuCode);
      
      // 递归处理子权限
      const processChildren = (perms: Permission[]) => {
        perms.forEach(perm => {
          if (isChecked) {
            newSet.delete(perm.code);
          } else {
            newSet.add(perm.code);
          }
          if (perm.children) {
            processChildren(perm.children);
          }
        });
      };
      
      if (isChecked) {
        newSet.delete(menuCode);
      } else {
        newSet.add(menuCode);
      }
      
      if (children) {
        processChildren(children);
      }
      
      return Array.from(newSet);
    });
  };

  const handleSubmit = () => {
    onSave(selectedPermissions);
    onClose();
  };

  const renderPermissionTree = (permissions: Permission[], level = 0) => {
    return permissions.map(permission => (
      <div key={permission.id} className={`ml-${level * 4}`}>
        <div className="flex items-center gap-2 py-2">
          {permission.type === 'menu' && (
            <button
              onClick={() => toggleMenu(permission.id)}
              className="p-1 hover:bg-slate-100 rounded-full"
            >
              {expandedMenus.has(permission.id) ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
            </button>
          )}
          {permission.type === 'button' && <div className="w-4"></div>}
          <button
            onClick={() => {
              if (permission.type === 'menu' && permission.children) {
                handleMenuToggle(permission.code, permission.children);
              } else {
                handlePermissionToggle(permission.code);
              }
            }}
            className="flex items-center gap-2 w-full text-left"
          >
            {selectedPermissions.includes(permission.code) ? (
              <CheckSquare size={16} className="text-blue-600" />
            ) : (
              <Square size={16} className="text-slate-400" />
            )}
            <span className="text-sm font-medium text-slate-700">{permission.name}</span>
            <span className="text-xs text-slate-400 font-mono ml-auto">{permission.code}</span>
          </button>
        </div>
        {permission.type === 'menu' && permission.children && expandedMenus.has(permission.id) && (
          <div className="pl-4 border-l border-slate-200">
            {renderPermissionTree(permission.children, level + 1)}
          </div>
        )}
      </div>
    ));
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm">
          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 20 }}
            className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[80vh] overflow-hidden"
          >
            <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
              <h3 className="text-lg font-bold text-slate-900">分配权限</h3>
              <button onClick={onClose} className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-slate-600">
                <X size={20} />
              </button>
            </div>

            <div className="p-6 space-y-4">
              <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200">
                <Search size={16} className="text-slate-400" />
                <input 
                  type="text" 
                  placeholder="搜索权限..." 
                  className="text-sm outline-none w-full"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>

              <div className="space-y-2 max-h-96 overflow-y-auto">
                {loading ? (
                  <div className="flex items-center justify-center py-12">
                    <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                  </div>
                ) : (
                  renderPermissionTree(permissions)
                )}
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
                  type="button"
                  onClick={handleSubmit}
                  className="flex-1 px-4 py-2.5 rounded-xl bg-blue-600 font-semibold text-white hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all"
                >
                  保存
                </button>
              </div>
            </div>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
}