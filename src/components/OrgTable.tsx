import React, { useState } from 'react';
import { MoreHorizontal, Search, Building2, Plus, ChevronDown, ChevronRight, Users2 } from 'lucide-react';
import { cn } from '../lib/utils';

const mockOrgs = [
  { 
    id: 1, 
    name: 'Zenith 集团总部', 
    code: 'ZENITH_HQ', 
    type: '集团', 
    leader: '张建国',
    memberCount: 45,
    children: [
      { 
        id: 2, 
        name: '研发中心', 
        code: 'RD_CENTER', 
        type: '部门', 
        leader: '李明',
        memberCount: 120,
        children: [
          { id: 4, name: '前端开发组', code: 'FE_TEAM', type: '小组', leader: '王小二', memberCount: 24 },
          { id: 5, name: '后端开发组', code: 'BE_TEAM', type: '小组', leader: '赵六', memberCount: 36 },
        ]
      },
      { 
        id: 3, 
        name: '市场部', 
        code: 'MARKET_DEPT', 
        type: '部门', 
        leader: '孙美美',
        memberCount: 30 
      },
    ]
  },
];

interface OrgItemProps {
  org: any;
  level: number;
  key?: React.Key;
}

function OrgRow({ org, level }: OrgItemProps) {
  const [isExpanded, setIsExpanded] = useState(true);
  const hasChildren = org.children && org.children.length > 0;

  return (
    <>
      <tr className="hover:bg-slate-50 transition-colors group">
        <td className="px-6 py-4">
          <div className="flex items-center gap-2" style={{ paddingLeft: `${level * 24}px` }}>
            <button 
              onClick={() => setIsExpanded(!isExpanded)}
              className={cn(
                "p-1 hover:bg-slate-200 rounded transition-colors",
                !hasChildren && "invisible"
              )}
            >
              {isExpanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
            </button>
            <div className={cn(
              "w-8 h-8 rounded-lg flex items-center justify-center",
              level === 0 ? "bg-blue-50 text-blue-600" : "bg-slate-50 text-slate-600"
            )}>
              <Building2 size={16} />
            </div>
            <span className="text-sm font-semibold text-slate-900">{org.name}</span>
          </div>
        </td>
        <td className="px-6 py-4">
          <code className="text-xs bg-slate-100 text-slate-600 px-2 py-1 rounded font-mono">
            {org.code}
          </code>
        </td>
        <td className="px-6 py-4">
          <span className="text-xs font-medium px-2 py-1 bg-slate-100 text-slate-600 rounded-md">
            {org.type}
          </span>
        </td>
        <td className="px-6 py-4">
          <span className="text-sm text-slate-600">{org.leader}</span>
        </td>
        <td className="px-6 py-4">
          <div className="flex items-center gap-1.5 text-slate-500">
            <Users2 size={14} />
            <span className="text-sm font-medium">{org.memberCount}</span>
          </div>
        </td>
        <td className="px-6 py-4 text-right">
          <button className="p-2 text-slate-400 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-all">
            <MoreHorizontal size={18} />
          </button>
        </td>
      </tr>
      {isExpanded && hasChildren && org.children.map((child: any) => (
        <OrgRow key={child.id} org={child} level={level + 1} />
      ))}
    </>
  );
}

export default function OrgTable() {
  return (
    <div className="p-8 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">组织管理</h2>
          <p className="text-slate-500 mt-1">管理企业的组织架构、部门及层级关系。</p>
        </div>
        <button className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200">
          <Plus size={18} />
          新增组织
        </button>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
          <div className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
            <Search size={16} className="text-slate-400" />
            <input type="text" placeholder="搜索组织名称或编码..." className="text-sm outline-none w-full" />
          </div>
        </div>

        <table className="w-full text-left">
          <thead>
            <tr className="bg-slate-50/50 text-slate-500 text-xs uppercase font-bold tracking-wider">
              <th className="px-6 py-4">组织名称</th>
              <th className="px-6 py-4">组织编码</th>
              <th className="px-6 py-4">类型</th>
              <th className="px-6 py-4">负责人</th>
              <th className="px-6 py-4">成员数</th>
              <th className="px-6 py-4 text-right">操作</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {mockOrgs.map((org) => (
              <OrgRow key={org.id} org={org} level={0} />
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
