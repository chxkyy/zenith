import React, { useState, useEffect } from 'react';
import { MoreHorizontal, Search, Building2, Plus, ChevronDown, ChevronRight, Users2 } from 'lucide-react';
import { cn } from '../lib/utils';

interface Org {
  id: number;
  name: string;
  code: string;
  type: string;
  leader: string;
  memberCount: number;
  parentId?: number;
  children?: Org[];
}

interface OrgItemProps {
  org: Org;
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
      {isExpanded && hasChildren && org.children.map((child) => (
        <OrgRow key={child.id} org={child} level={level + 1} />
      ))}
    </>
  );
}

export default function OrgTable() {
  const [orgs, setOrgs] = useState<Org[]>([]);
  const [loading, setLoading] = useState(true);

  // 从后端获取组织数据
  useEffect(() => {
    const fetchOrgs = async () => {
      setLoading(true);
      try {
        const response = await fetch('/api/orgs');
        if (!response.ok) {
          throw new Error('Failed to fetch orgs');
        }
        const data = await response.json();
        if (data.success && data.data) {
          // 将扁平的组织列表转换为树形结构
          const buildOrgTree = (orgList: any[]): Org[] => {
            const orgMap = new Map<number, Org>();
            
            // 首先创建所有组织的映射
            orgList.forEach(org => {
              orgMap.set(org.id, {
                id: org.id,
                name: org.name,
                code: org.code || '',
                type: org.type || '部门',
                leader: org.leader || '',
                memberCount: org.memberCount || 0,
                parentId: org.parentId,
                children: []
              });
            });
            
            // 构建树形结构
            const rootOrgs: Org[] = [];
            orgMap.forEach(org => {
              if (!org.parentId) {
                rootOrgs.push(org);
              } else {
                const parentOrg = orgMap.get(org.parentId);
                if (parentOrg) {
                  parentOrg.children?.push(org);
                }
              }
            });
            
            return rootOrgs;
          };
          
          setOrgs(buildOrgTree(data.data));
        }
      } catch (error) {
        console.error('Error fetching orgs:', error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchOrgs();
  }, []);

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
            {loading ? (
              <tr>
                <td colSpan={6} className="px-6 py-12 text-center">
                  <div className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
                </td>
              </tr>
            ) : orgs.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-6 py-12 text-center text-slate-500">
                  暂无组织数据
                </td>
              </tr>
            ) : (
              orgs.map((org) => (
                <OrgRow key={org.id} org={org} level={0} />
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
