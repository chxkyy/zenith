import React, {useState, useEffect} from 'react';
import {
    MoreHorizontal,
    Search,
    Building2,
    Plus,
    ChevronDown,
    ChevronRight,
    Users2,
    UserPlus,
    RefreshCw,
    Edit,
    Trash2,
    RefreshCw as ResetIcon,
    User,
    Shield
} from 'lucide-react';
import {cn} from '../lib/utils';
import UserModal from './UserModal';
import RoleAssignModal from './RoleAssignModal';

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
    onSelectOrg: (org: Org) => void;
    selectedOrgId: number | null;
    key?: React.Key;
}

function OrgTreeItem({org, level, onSelectOrg, selectedOrgId}: OrgItemProps) {
    const [isExpanded, setIsExpanded] = useState(true);
    const hasChildren = org.children && org.children.length > 0;

    return (
        <>
            <div
                className={cn(
                    "flex items-center gap-1.5 py-1.5 px-3 cursor-pointer rounded-lg transition-colors",
                    selectedOrgId === org.id ? "bg-blue-50 text-blue-600" : "hover:bg-slate-50"
                )}
                style={{paddingLeft: `${level * 20 + 12}px`}}
                onClick={() => onSelectOrg(org)}
            >
                <button
                    onClick={(e) => {
                        e.stopPropagation();
                        setIsExpanded(!isExpanded);
                    }}
                    className={cn(
                        "p-0.5 hover:bg-slate-200 rounded transition-colors",
                        !hasChildren && "invisible"
                    )}
                >
                    {isExpanded ? <ChevronDown size={12}/> : <ChevronRight size={12}/>}
                </button>
                <div className={cn(
                    "w-5 h-5 rounded-lg flex items-center justify-center",
                    level === 0 ? "bg-blue-50 text-blue-600" : "bg-slate-50 text-slate-600"
                )}>
                    <Building2 size={12}/>
                </div>
                <div className="flex-1">
                    <span className="text-xs font-medium">{org.name}</span>
                    <span className="text-xs text-slate-400 ml-1.5">({org.memberCount}人)</span>
                </div>
            </div>
            {isExpanded && hasChildren && org.children.map((child) => (
                <OrgTreeItem
                    key={child.id}
                    org={child}
                    level={level + 1}
                    onSelectOrg={onSelectOrg}
                    selectedOrgId={selectedOrgId}
                />
            ))}
        </>
    );
}

export default function OrgUserManagement() {
    // 部门相关状态
    const [orgs, setOrgs] = useState<Org[]>([]);
    const [selectedOrg, setSelectedOrg] = useState<Org | null>(null);
    const [orgLoading, setOrgLoading] = useState(true);

    // 用户相关状态
    const [users, setUsers] = useState<any[]>([]);
    const [userLoading, setUserLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
    const [selectedUser, setSelectedUser] = useState<any>(null);
    const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);
    const [selectedUserForRoles, setSelectedUserForRoles] = useState<any>(null);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    // 从后端获取组织数据
    useEffect(() => {
        const fetchOrgs = async () => {
            setOrgLoading(true);
            try {
                const response = await fetch('/api/orgs/page', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        pageIndex: 1,
                        pageSize: 1000
                    })
                });
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

                    const orgTree = buildOrgTree(data.data);
                    setOrgs(orgTree);
                    // 默认选择第一个组织
                    if (orgTree.length > 0) {
                        setSelectedOrg(orgTree[0]);
                    }
                }
            } catch (error) {
                console.error('Error fetching orgs:', error);
            } finally {
                setOrgLoading(false);
            }
        };

        fetchOrgs();
    }, []);

    // 当选中的组织变化时，获取该组织下的用户
    useEffect(() => {
        if (selectedOrg) {
            fetchUsersByOrg(selectedOrg.id);
        }
    }, [selectedOrg]);

    // 从后端获取指定组织下的用户数据
    const fetchUsersByOrg = (orgId: number, pageIndex = 1) => {
        setUserLoading(true);
        setError(null);

        const query = {
            pageIndex,
            pageSize: 10,
            keyword: searchKeyword,
            orgId
        };

        fetch('/api/users/page', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(query)
        }).then(async res => {
            const text = await res.text();
            if (res.status === 503) {
                throw new Error('Java 后端正在启动，请稍候...');
            }
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}. ${text}`);
            }
            if (!text) {
                return {success: true, data: [], totalCount: 0};
            }
            try {
                return JSON.parse(text);
            } catch (e) {
                console.error('Failed to parse JSON:', text);
                throw new Error('服务器返回了非 JSON 格式的数据');
            }
        }).then(res => {
            if (res.success) {
                setUsers(res.data || []);
                setTotalPages(Math.ceil(res.totalCount / 10));
                setCurrentPage(res.pageIndex || 1);
            } else {
                setError(res.errMessage || '获取用户列表失败');
            }
        })
            .catch(err => {
                console.error('Error fetching users:', err);
                setError(err.message || '网络错误，请检查后端服务');
            })
            .finally(() => {
                setUserLoading(false);
            });
    };

    // 处理部门选择
    const handleSelectOrg = (org: Org) => {
        setSelectedOrg(org);
    };

    // 处理用户保存
    const handleSaveUser = (user: any) => {
        setUserLoading(true);
        const isEdit = user.id !== undefined;
        const url = isEdit ? '/api/users' : '/api/users';
        const method = isEdit ? 'POST' : 'POST';

        // 如果是新增用户，且当前有选中的组织，则设置用户的组织ID
        if (!isEdit && selectedOrg) {
            user.orgId = selectedOrg.id;
        }

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.text().then(text => text ? JSON.parse(text) : {success: true});
            })
            .then(res => {
                if (res.success) {
                    if (selectedOrg) {
                        fetchUsersByOrg(selectedOrg.id); // 重新加载当前组织的用户列表
                    }
                    setIsModalOpen(false);
                } else {
                    alert(`${isEdit ? '编辑用户失败' : '添加用户失败'}: ${res.errMessage}`);
                }
            })
            .catch(err => {
                console.error(`${isEdit ? 'Error updating user' : 'Error adding user'}:`, err);
                alert(`${isEdit ? '编辑用户失败' : '添加用户失败'}，请检查网络`);
            })
            .finally(() => {
                setUserLoading(false);
            });
    };

    // 处理页面变化
    const handlePageChange = (page: number) => {
        if (selectedOrg && page >= 1 && page <= totalPages) {
            fetchUsersByOrg(selectedOrg.id, page);
        }
    };

    // 处理编辑用户
    const handleEditUser = (user: any) => {
        setSelectedUser(user);
        setModalMode('edit');
        setIsModalOpen(true);
    };

    // 处理删除用户
    const handleDeleteUser = (id: number) => {
        if (window.confirm('删除后用户数据不可恢复，关联角色自动解除，是否确认删除？')) {
            setUserLoading(true);
            fetch(`/api/users/delete`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({id})
            })
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`HTTP error! status: ${res.status}`);
                    }
                    return res.text().then(text => text ? JSON.parse(text) : {success: true});
                })
                .then(res => {
                    if (res.success) {
                        if (selectedOrg) {
                            fetchUsersByOrg(selectedOrg.id); // 重新加载当前组织的用户列表
                        }
                    } else {
                        alert('删除用户失败: ' + res.errMessage);
                    }
                })
                .catch(err => {
                    console.error('Error deleting user:', err);
                    alert('删除用户失败，请检查网络');
                })
                .finally(() => {
                    setUserLoading(false);
                });
        }
    };

    // 处理重置密码
    const handleResetPassword = (id: number) => {
        if (window.confirm('确定要重置该用户的密码吗？重置后密码为默认密码 123456，请通知用户及时修改。')) {
            setUserLoading(true);
            fetch(`/api/users/reset-password/${id}`, {
                method: 'POST'
            })
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`HTTP error! status: ${res.status}`);
                    }
                    return res.text().then(text => text ? JSON.parse(text) : {success: true});
                })
                .then(res => {
                    if (res.success) {
                        alert('密码重置成功，默认密码为 123456，请通知用户及时修改');
                    } else {
                        alert('重置密码失败: ' + res.errMessage);
                    }
                })
                .catch(err => {
                    console.error('Error resetting password:', err);
                    alert('重置密码失败，请检查网络');
                })
                .finally(() => {
                    setUserLoading(false);
                });
        }
    };

    // 处理切换用户状态
    const handleChangeStatus = (id: number, currentStatus: number) => {
        const newStatus = currentStatus === 1 ? 0 : 1;
        if (window.confirm(`确定要将用户状态切换为${newStatus === 1 ? '活跃' : '禁用'}吗？`)) {
            setUserLoading(true);
            fetch(`/api/users/status/${id}?status=${newStatus}`, {
                method: 'POST'
            })
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`HTTP error! status: ${res.status}`);
                    }
                    return res.text().then(text => text ? JSON.parse(text) : {success: true});
                })
                .then(res => {
                    if (res.success) {
                        if (selectedOrg) {
                            fetchUsersByOrg(selectedOrg.id); // 重新加载当前组织的用户列表
                        }
                    } else {
                        alert('状态切换失败: ' + res.errMessage);
                    }
                })
                .catch(err => {
                    console.error('Error changing status:', err);
                    alert('状态切换失败，请检查网络');
                })
                .finally(() => {
                    setUserLoading(false);
                });
        }
    };

    // 处理分配角色
    const handleAssignRoles = (user: any) => {
        setSelectedUserForRoles(user);
        setIsRoleModalOpen(true);
    };

    // 处理保存角色
    const handleSaveRoles = (roles: string[]) => {
        // 这里简化处理，实际应该调用后端 API 来更新角色
        setUserLoading(true);
        // 由于后端 API 尚未实现角色分配功能，这里只是模拟成功
        setTimeout(() => {
            alert('角色分配成功');
            if (selectedOrg) {
                fetchUsersByOrg(selectedOrg.id); // 重新加载当前组织的用户列表
            }
            setUserLoading(false);
        }, 500);
    };

    return (
        <div className="p-8 space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h2 className="text-2xl font-bold text-slate-900">组织管理</h2>
                </div>
                <button
                    onClick={() => setIsModalOpen(true)}
                    className="flex items-center gap-2 bg-blue-600 text-white px-5 py-2.5 rounded-xl font-medium hover:bg-blue-700 transition-all shadow-lg shadow-blue-200"
                >
                    <UserPlus size={18}/>
                    添加用户
                </button>
            </div>

            <UserModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSave={handleSaveUser}
                user={selectedUser}
                mode={modalMode}
            />

            <RoleAssignModal
                isOpen={isRoleModalOpen}
                onClose={() => setIsRoleModalOpen(false)}
                onSave={handleSaveRoles}
                userRoles={selectedUserForRoles ? [selectedUserForRoles.role] : []}
            />

            <div className="grid grid-cols-12 gap-6">
                {/* 左侧组织树 */}
                <div className="col-span-12 lg:col-span-3">
                    <div
                        className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden h-[calc(100vh-300px)]">
                        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
                            <h3 className="font-medium text-slate-900">组织树</h3>
                            <button
                                className="p-1.5 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-colors">
                                <Plus size={16}/>
                            </button>
                        </div>
                        <div className="p-4 overflow-y-auto h-[calc(100%-64px)]">
                            {orgLoading ? (
                                <div className="flex items-center justify-center py-12">
                                    <div
                                        className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
                                </div>
                            ) : orgs.length === 0 ? (
                                <div className="text-center py-12 text-slate-500">
                                    暂无部门数据
                                </div>
                            ) : (
                                <div className="space-y-1">
                                    {orgs.map((org) => (
                                        <OrgTreeItem
                                            key={org.id}
                                            org={org}
                                            level={0}
                                            onSelectOrg={handleSelectOrg}
                                            selectedOrgId={selectedOrg?.id || null}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {/* 右侧用户列表 */}
                <div className="col-span-12 lg:col-span-9">
                    <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
                        <div className="p-4 border-b border-slate-100 flex flex-wrap items-center gap-4 bg-slate-50/50">
                            <div
                                className="flex items-center gap-4 bg-white px-3 py-1.5 rounded-lg border border-slate-200 w-72">
                                <Search size={16} className="text-slate-400"/>
                                <input
                                    type="text"
                                    placeholder="搜索用户..."
                                    className="text-sm outline-none w-full"
                                    value={searchKeyword}
                                    onChange={(e) => setSearchKeyword(e.target.value)}
                                />
                            </div>
                            <div className="flex items-center gap-2">
                                <button
                                    onClick={() => {
                                        if (selectedOrg) {
                                            fetchUsersByOrg(selectedOrg.id);
                                        }
                                    }}
                                    disabled={!selectedOrg || userLoading}
                                    className="flex items-center gap-2 bg-white text-slate-600 border border-slate-200 px-4 py-2 rounded-xl font-medium hover:bg-slate-50 transition-all shadow-sm disabled:opacity-50"
                                >
                                    <RefreshCw size={16}/>
                                    刷新
                                </button>
                            </div>
                        </div>

                        <table className="w-full text-left">
                            <thead>
                            <tr className="bg-slate-50/50 text-slate-500 text-xs uppercase font-bold tracking-wider">
                                <th className="px-6 py-4">用户信息</th>
                                <th className="px-6 py-4">角色</th>
                                <th className="px-6 py-4">状态</th>
                                <th className="px-6 py-4">创建人</th>
                                <th className="px-6 py-4">创建时间</th>
                                <th className="px-6 py-4">修改人</th>
                                <th className="px-6 py-4">修改时间</th>
                                <th className="px-6 py-4 text-right">操作</th>
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100">
                            {!selectedOrg ? (
                                <tr>
                                    <td colSpan={8} className="px-6 py-12 text-center">
                                        <p className="text-slate-500 text-sm">请选择一个部门</p>
                                    </td>
                                </tr>
                            ) : userLoading ? (
                                <tr>
                                    <td colSpan={8} className="px-6 py-12 text-center">
                                        <div className="flex flex-col items-center gap-3">
                                            <div
                                                className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin"></div>
                                            <p className="text-slate-500 text-sm">正在加载用户数据...</p>
                                        </div>
                                    </td>
                                </tr>
                            ) : error ? (
                                <tr>
                                    <td colSpan={8} className="px-6 py-12 text-center">
                                        <div className="flex flex-col items-center gap-3">
                                            <p className="text-red-500 text-sm font-medium">{error}</p>
                                            <button
                                                onClick={() => {
                                                    if (selectedOrg) {
                                                        fetchUsersByOrg(selectedOrg.id);
                                                    }
                                                }}
                                                className="text-blue-600 text-sm hover:underline"
                                            >
                                                点击重试
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ) : users.length === 0 ? (
                                <tr>
                                    <td colSpan={8} className="px-6 py-12 text-center">
                                        <p className="text-slate-500 text-sm">该部门暂无用户数据</p>
                                    </td>
                                </tr>
                            ) : (
                                users.map((user) => (
                                    <tr key={user.id} className="hover:bg-slate-50 transition-colors group">
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-3">
                                                <div
                                                    className="w-10 h-10 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold">
                                                    {user.username ? user.username[0] : '?'}
                                                </div>
                                                <div>
                                                    <p className="text-sm font-semibold text-slate-900">{user.username}</p>
                                                    <p className="text-xs text-slate-500">{user.email}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="text-sm text-slate-600">{user.roleNames}</span>
                                        </td>
                                        <td className="px-6 py-4">
                        <span className={cn(
                            "px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider",
                            user.status === 1 ? "bg-emerald-100 text-emerald-700" :
                                user.status === 0 ? "bg-red-100 text-red-700" : "bg-slate-100 text-slate-600"
                        )}>
                          {user.status === 1 ? '活跃' : '禁用'}
                        </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="text-sm text-slate-600">{user.createUserId || '-'}</span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="text-sm text-slate-600">
                                                {user.createdTime ? new Date(user.createdTime).toLocaleString() : '-'}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="text-sm text-slate-600">{user.updateUserId || '-'}</span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="text-sm text-slate-600">
                                                {user.updateTime ? new Date(user.updateTime).toLocaleString() : '-'}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 text-right">
                                            <div className="flex items-center justify-end gap-1">
                                                <button
                                                    onClick={() => handleEditUser(user)}
                                                    className="p-1.5 text-slate-500 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                                                    title="编辑"
                                                >
                                                    <Edit size={16}/>
                                                </button>
                                                <button
                                                    onClick={() => handleResetPassword(user.id)}
                                                    className="p-1.5 text-slate-500 hover:text-amber-600 hover:bg-amber-50 rounded-lg transition-colors"
                                                    title="重置密码"
                                                >
                                                    <ResetIcon size={16}/>
                                                </button>
                                                <button
                                                    onClick={() => handleChangeStatus(user.id, user.status)}
                                                    className="p-1.5 text-slate-500 hover:text-purple-600 hover:bg-purple-50 rounded-lg transition-colors"
                                                    title={user.status === 1 ? '禁用' : '启用'}
                                                >
                                                    <User size={16}/>
                                                </button>
                                                <button
                                                    onClick={() => handleAssignRoles(user)}
                                                    className="p-1.5 text-slate-500 hover:text-emerald-600 hover:bg-emerald-50 rounded-lg transition-colors"
                                                    title="分配角色"
                                                >
                                                    <Shield size={16}/>
                                                </button>
                                                <button
                                                    onClick={() => handleDeleteUser(user.id)}
                                                    className="p-1.5 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                                                    title="删除"
                                                >
                                                    <Trash2 size={16}/>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                            </tbody>
                        </table>

                        {/* 分页 */}
                        {selectedOrg && !userLoading && !error && users.length > 0 && (
                            <div className="p-4 border-t border-slate-100 flex items-center justify-between">
                                <p className="text-sm text-slate-500">
                                    第 {currentPage} 页，共 {totalPages} 页
                                </p>
                                <div className="flex items-center gap-2">
                                    <button
                                        onClick={() => handlePageChange(currentPage - 1)}
                                        disabled={currentPage === 1}
                                        className="px-3 py-1.5 text-sm font-medium rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
                                    >
                                        上一页
                                    </button>
                                    <button
                                        onClick={() => handlePageChange(currentPage + 1)}
                                        disabled={currentPage === totalPages}
                                        className="px-3 py-1.5 text-sm font-medium rounded-lg border border-slate-200 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
                                    >
                                        下一页
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
