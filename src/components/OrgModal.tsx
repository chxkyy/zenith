import React, { useState } from 'react';
import { X } from 'lucide-react';

interface Org {
  id: number;
  name: string;
  parentId: number | null;
  sort: number;
  status: number;
  children?: Org[];
}

interface OrgModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (org: Partial<Org>) => void;
  org?: Org;
  mode: 'add' | 'add-sub' | 'edit';
  allOrgs: Org[];
  hideParentSelect?: boolean;
}

const OrgModal: React.FC<OrgModalProps> = ({ isOpen, onClose, onSave, org, mode, allOrgs, hideParentSelect }) => {
  const [formData, setFormData] = useState<Partial<Org>>({
    id: org?.id,
    name: org?.name || '',
    parentId: mode === 'add-sub' && org?.id ? org.id : (org?.parentId || null),
    sort: org?.sort || 1,
    status: org?.status ?? 1
  });

  React.useEffect(() => {
    if (isOpen) {
      setFormData({
        id: org?.id,
        name: org?.name || '',
        parentId: mode === 'add-sub' && org?.id ? org.id : (org?.parentId || null),
        sort: org?.sort || 1,
        status: org?.status ?? 1
      });
    }
  }, [isOpen, org, mode]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  if (!isOpen) return null;

  const getTitle = () => {
    switch (mode) {
      case 'add':
        return '新增组织';
      case 'add-sub':
        return '新增子组织';
      case 'edit':
        return '编辑组织';
      default:
        return '组织管理';
    }
  };

  const getButtonText = () => {
    return mode === 'edit' ? '更新组织' : '保存组织';
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/15 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden">
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <h3 className="text-lg font-bold text-slate-900">
            {getTitle()}
          </h3>
          <button onClick={onClose} className="p-2 hover:bg-white rounded-lg transition-colors text-slate-400 hover:text-slate-600">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">组织名称</label>
            <input
              required
              type="text"
              value={formData.name || ''}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
              placeholder="请输入组织名称"
              maxLength={50}
            />
          </div>

          {!hideParentSelect && (
            <div className="space-y-1.5">
              <label className="text-sm font-semibold text-slate-700">上级组织</label>
              <select
                value={formData.parentId || ''}
                onChange={(e) => setFormData({ ...formData, parentId: e.target.value ? parseInt(e.target.value) : null })}
                className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 transition-all bg-white"
              >
                <option value="">顶级组织</option>
                {allOrgs
                  .filter(o => !formData.id || o.id !== formData.id)
                  .map(parentOrg => (
                    <option key={parentOrg.id} value={parentOrg.id}>
                      {parentOrg.name}
                    </option>
                  ))}
              </select>
            </div>
          )}

          {hideParentSelect && org && (
            <div className="space-y-1.5">
              <label className="text-sm font-semibold text-slate-700">上级组织</label>
              <div className="w-full px-4 py-2 rounded-xl border border-slate-200 bg-slate-50 text-slate-600">
                {org.name}
              </div>
            </div>
          )}

          <div className="space-y-1.5">
            <label className="text-sm font-semibold text-slate-700">排序</label>
            <input
              required
              type="number"
              min="0"
              value={formData.sort || 1}
              onChange={(e) => setFormData({ ...formData, sort: parseInt(e.target.value) || 0 })}
              className="w-full px-4 py-2 rounded-xl border border-slate-200 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-50/50 transition-all"
              placeholder="请输入排序数字，数字越小越靠前"
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
              {getButtonText()}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default OrgModal;
