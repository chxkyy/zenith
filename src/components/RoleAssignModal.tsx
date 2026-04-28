import React, { useState, useEffect } from 'react';
import { X, Search } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

// 默认可用角色
const DEFAULT_AVAILABLE_ROLES = ['ADMIN', 'EDITOR', 'USER'];

interface RoleAssignModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (roles: string[]) => void;
  userRoles?: string[]; // 当前用户已分配的角色
  availableRoles?: string[]; // 所有可用角色
}

export default function RoleAssignModal({ isOpen, onClose, onSave, userRoles = [], availableRoles = DEFAULT_AVAILABLE_ROLES }: RoleAssignModalProps) {
  const [selectedRoles, setSelectedRoles] = useState<string[]>(userRoles);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredRoles, setFilteredRoles] = useState<string[]>(availableRoles);

  // 当用户角色变化时，更新选中状态
  useEffect(() => {
    setSelectedRoles(userRoles);
  }, [userRoles]);

  // 当搜索词变化时，过滤角色
  useEffect(() => {
    if (searchTerm) {
      const filtered = availableRoles.filter(role => 
        role.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setFilteredRoles(filtered);
    } else {
      setFilteredRoles(availableRoles);
    }
  }, [searchTerm, availableRoles]);

  const handleRoleToggle = (role: string) => {
    setSelectedRoles(prev => {
      if (prev.includes(role)) {
        // 确保至少保留一个角色
        if (prev.length === 1) {
          alert('用户至少需要分配一个角色');
          return prev;
        }
        return prev.filter(r => r !== role);
      } else {
        return [...prev, role];
      }
    });
  };

  const handleSubmit = () => {
    onSave(selectedRoles);
    onClose();
  };

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
                {filteredRoles.map((role) => (
                  <div key={role} className="flex items-center gap-2">
                    <input
                      type="checkbox"
                      id={`role-${role}`}
                      checked={selectedRoles.includes(role)}
                      onChange={() => handleRoleToggle(role)}
                      className="w-4 h-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                    />
                    <label 
                      htmlFor={`role-${role}`} 
                      className="text-sm font-medium text-slate-700 cursor-pointer"
                    >
                      {role === 'ADMIN' ? '管理员' : role === 'EDITOR' ? '编辑者' : '普通用户'}
                    </label>
                  </div>
                ))}
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