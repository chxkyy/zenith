import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

interface UserModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (user: any) => void;
  user?: any; // 编辑模式下的用户数据
  mode?: 'add' | 'edit'; // 模式：新增或编辑
}

export default function UserModal({ isOpen, onClose, onSave, user, mode = 'add' }: UserModalProps) {
  const [formData, setFormData] = useState({
    id: undefined,
    username: '',
    email: '',
    role: 'USER',
    orgName: '',
    status: 1,
    createUserId: undefined,
    updateUserId: undefined,
    createdTime: undefined,
    updateTime: undefined
  });

  // 当用户数据变化时，更新表单数据
  useEffect(() => {
    if (user && mode === 'edit') {
      setFormData({
        id: user.id,
        username: user.username || '',
        email: user.email || '',
        role: user.role || 'USER',
        orgName: user.orgName || '',
        status: user.status || 1,
        createUserId: user.createUserId,
        updateUserId: user.updateUserId,
        createdTime: user.createdTime,
        updateTime: user.updateTime
      });
    } else if (mode === 'add') {
      setFormData({
        id: undefined,
        username: '',
        email: '',
        role: 'USER',
        orgName: '',
        status: 1,
        createUserId: undefined,
        updateUserId: undefined,
        createdTime: undefined,
        updateTime: undefined
      });
    }
  }, [user, mode]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
    onClose();
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm">
          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 20 }}
            className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden"
          >
            <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
              <h3 className="text-lg font-bold text-slate-900">
                {mode === 'add' ? '添加新用户' : '编辑用户'}
              </h3>
              <button onClick={onClose} className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-slate-600">
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-slate-700">用户名</label>
                <input
                  required
                  type="text"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
                  placeholder="请输入用户名"
                  value={formData.username}
                  onChange={e => setFormData({ ...formData, username: e.target.value })}
                  disabled={mode === 'edit'} // 编辑模式下用户名不可修改
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-slate-700">电子邮箱</label>
                <input
                  required
                  type="email"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
                  placeholder="example@domain.com"
                  value={formData.email}
                  onChange={e => setFormData({ ...formData, email: e.target.value })}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="text-sm font-semibold text-slate-700">角色</label>
                  <select
                    className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 transition-all bg-white"
                    value={formData.role}
                    onChange={e => setFormData({ ...formData, role: e.target.value })}
                  >
                    <option value="ADMIN">管理员</option>
                    <option value="EDITOR">编辑者</option>
                    <option value="USER">普通用户</option>
                  </select>
                </div>
                <div className="space-y-1.5">
                  <label className="text-sm font-semibold text-slate-700">状态</label>
                  <select
                    className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 transition-all bg-white"
                    value={formData.status}
                    onChange={e => setFormData({ ...formData, status: Number(e.target.value) })}
                  >
                    <option value={1}>活跃</option>
                    <option value={0}>禁用</option>
                  </select>
                </div>
              </div>

              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-slate-700">所属组织</label>
                <input
                  type="text"
                  className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
                  placeholder="例如：研发中心"
                  value={formData.orgName}
                  onChange={e => setFormData({ ...formData, orgName: e.target.value })}
                />
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
                  {mode === 'add' ? '保存用户' : '更新用户'}
                </button>
              </div>
            </form>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
}
