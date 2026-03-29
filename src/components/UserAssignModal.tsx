import React, { useState, useEffect } from 'react';
import { X, CheckSquare, Square, Search } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

interface User {
  id: number;
  username: string;
  email: string;
  orgName: string;
  role: string;
  status: number;
}

// 默认空数组
const DEFAULT_ASSIGNED_USERS: number[] = [];

interface UserAssignModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (users: number[]) => void;
  roleId: number;
  assignedUsers?: number[];
}

export default function UserAssignModal({ isOpen, onClose, onSave, roleId, assignedUsers = DEFAULT_ASSIGNED_USERS }: UserAssignModalProps) {
  const [selectedUsers, setSelectedUsers] = useState<number[]>(assignedUsers);
  const [searchTerm, setSearchTerm] = useState('');
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchUsers = () => {
    setLoading(true);
    fetch('/api/users/page', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        pageIndex: 1,
        pageSize: 100
      })
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
          setUsers(res.data || []);
        }
      })
      .catch(err => {
        console.error('Error fetching users:', err);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    if (isOpen) {
      fetchUsers();
      setSelectedUsers(assignedUsers);
    }
  }, [isOpen, assignedUsers]);

  const handleUserToggle = (userId: number) => {
    setSelectedUsers(prev => {
      if (prev.includes(userId)) {
        return prev.filter(id => id !== userId);
      } else {
        return [...prev, userId];
      }
    });
  };

  const handleSubmit = () => {
    onSave(selectedUsers);
    onClose();
  };

  const filteredUsers = users.filter(user => 
    user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.orgName.toLowerCase().includes(searchTerm.toLowerCase())
  );

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
              <h3 className="text-lg font-bold text-slate-900">分配用户</h3>
              <button onClick={onClose} className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-slate-600">
                <X size={20} />
              </button>
            </div>

            <div className="p-6 space-y-4">
              <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200">
                <Search size={16} className="text-slate-400" />
                <input 
                  type="text" 
                  placeholder="搜索用户..." 
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
                ) : filteredUsers.length === 0 ? (
                  <div className="flex items-center justify-center py-12">
                    <p className="text-slate-500 text-sm">暂无用户数据</p>
                  </div>
                ) : (
                  filteredUsers.map((user) => (
                    <div key={user.id} className="flex items-center gap-2 py-2 border-b border-slate-100 last:border-b-0">
                      <button
                        onClick={() => handleUserToggle(user.id)}
                        className="flex items-center gap-2 w-full text-left"
                      >
                        {selectedUsers.includes(user.id) ? (
                          <CheckSquare size={16} className="text-blue-600" />
                        ) : (
                          <Square size={16} className="text-slate-400" />
                        )}
                        <div className="flex-1">
                          <p className="text-sm font-medium text-slate-900">{user.username}</p>
                          <p className="text-xs text-slate-500">{user.email}</p>
                        </div>
                        <div className="text-sm text-slate-600">
                          <p>{user.orgName || '-'}</p>
                          <p className="text-xs text-slate-500">{user.role}</p>
                        </div>
                      </button>
                    </div>
                  ))
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