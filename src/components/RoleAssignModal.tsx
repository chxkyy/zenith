import React, { useState, useEffect } from 'react';
import { X, Search } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

interface Role {
  id: number;
  name: string;
  code: string;
  status: number;
}

interface RoleAssignModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (roleIds: string[]) => void;
  userId?: number;
}

export default function RoleAssignModal({ isOpen, onClose, onSave, userId }: RoleAssignModalProps) {
  const [availableRoles, setAvailableRoles] = useState<Role[]>([]);
  const [userCurrentRoleIds, setUserCurrentRoleIds] = useState<string[]>([]);
  const [selectedRoleIds, setSelectedRoleIds] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredRoles, setFilteredRoles] = useState<Role[]>([]);

  const fetchActiveRoles = () => {
    fetch('/api/roles/list-active', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(async res => {
        const text = await res.text();
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}. ${text}`);
        }
        if (!text) {
          return { success: true, data: [] };
        }
        try {
          return JSON.parse(text);
        } catch (e) {
          console.error('Failed to parse JSON:', text);
          throw new Error('服务器返回了非 JSON 格式的数据');
        }
      })
      .then(res => {
        if (res.success) {
          setAvailableRoles(res.data || []);
          setFilteredRoles(res.data || []);
        }
      })
      .catch(err => {
        console.error('获取角色列表失败:', err);
      });
  };

  const fetchUserRoles = () => {
    if (!userId) return;

    fetch(`/api/user-roles?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(async res => {
        const text = await res.text();
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}. ${text}`);
        }
        if (!text) {
          return { success: true, data: [] };
        }
        try {
          return JSON.parse(text);
        } catch (e) {
          console.error('Failed to parse JSON:', text);
          throw new Error('服务器返回了非 JSON 格式的数据');
        }
      })
      .then(res => {
        if (res.success) {
          const roleIds = res.data || [];
          setUserCurrentRoleIds(roleIds);
          setSelectedRoleIds(roleIds);
        }
      })
      .catch(err => {
        console.error('获取用户角色失败:', err);
      });
  };

  useEffect(() => {
    if (isOpen) {
      setLoading(true);

      Promise.all([
        fetchActiveRoles(),
        fetchUserRoles()
      ]).finally(() => {
        setLoading(false);
      });
    }
  }, [isOpen, userId]);

  useEffect(() => {
    if (!searchTerm) {
      setFilteredRoles(availableRoles);
      return;
    }

    const filtered = availableRoles.filter(role =>
      role.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      role.code.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredRoles(filtered);
  }, [searchTerm, availableRoles]);

  const handleRoleToggle = (roleId: string) => {
    setSelectedRoleIds(prev => {
      if (prev.includes(roleId)) {
        return prev.filter(id => id !== roleId);
      } else {
        return [...prev, roleId];
      }
    });
  };

  const handleSubmit = () => {
    onSave(selectedRoleIds);
    onClose();
  };

  const addedCount = selectedRoleIds.filter(r => !userCurrentRoleIds.includes(r)).length;
  const removedCount = userCurrentRoleIds.filter(r => !selectedRoleIds.includes(r)).length;

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/15 backdrop-blur-sm">
          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 20 }}
            className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden"
          >
            <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
              <h3 className="text-lg font-bold text-slate-900">分配角色</h3>
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
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>

              <div className="space-y-2 max-h-60 overflow-y-auto">
                {loading ? (
                  <div className="flex items-center justify-center py-8">
                    <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                    <span className="ml-2 text-sm text-slate-500">加载中...</span>
                  </div>
                ) : filteredRoles.length === 0 ? (
                  <div className="text-center py-8 text-slate-500 text-sm">
                    {searchTerm ? '未找到匹配的角色' : '暂无可用的有效角色'}
                  </div>
                ) : (
                  filteredRoles.map((role) => (
                    <div key={role.id} className="flex items-center gap-3 p-2 hover:bg-slate-50 rounded-lg transition-colors">
                      <input
                        type="checkbox"
                        id={`role-${role.id}`}
                        checked={selectedRoleIds.includes(String(role.id))}
                        onChange={() => handleRoleToggle(String(role.id))}
                        className="w-4 h-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                      />
                      <label
                        htmlFor={`role-${role.id}`}
                        className="flex-1 text-sm font-medium text-slate-700 cursor-pointer flex items-center gap-2"
                      >
                        <span>{role.name}</span>
                        <span className="text-xs text-slate-400 font-mono">({role.code})</span>
                      </label>

                      {userCurrentRoleIds.includes(String(role.id)) && (
                        <span className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">
                          当前
                        </span>
                      )}
                    </div>
                  ))
                )}
              </div>

              {!loading && selectedRoleIds.length > 0 && (
                <div className="text-xs text-slate-500 bg-slate-50 p-2 rounded-lg">
                  已选择 <span className="font-semibold text-blue-600">{selectedRoleIds.length}</span> 个角色
                  {addedCount > 0 && (
                    <span className="ml-2 text-emerald-600">(新增 {addedCount} 个)</span>
                  )}
                  {removedCount > 0 && (
                    <span className="ml-2 text-red-600">(移除 {removedCount} 个)</span>
                  )}
                </div>
              )}

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
                  disabled={loading}
                  className="flex-1 px-4 py-2.5 rounded-xl bg-blue-600 font-semibold text-white hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all disabled:opacity-50"
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
