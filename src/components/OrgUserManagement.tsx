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
    Shield,
    Eye,
    EyeOff,
    MoveUp,
    GripVertical
} from 'lucide-react';
import {cn, formatDateTime} from '../lib/utils';
import UserModal from './UserModal';
import RoleAssignModal from './RoleAssignModal';
import OrgModal from './OrgModal';
import Notification from './Notification';
import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
  DragEndEvent,
  DragStartEvent,
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy,
  useSortable,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

interface Org {
    id: number;
    name: string;
    code: string;
    type: string;
    leader: string;
    memberCount: number;
    parentId?: number;
    sort?: number;
    status?: number;
    children?: Org[];
}

interface SortableOrgItemProps {
    org: Org;
    level: number;
    isSelected: boolean;
    isExpanded: boolean;
    hasChildren: boolean;
    onSelect: (org: Org) => void;
    onToggleExpand: (id: number) => void;
    onRightClick: (e: React.MouseEvent, org: Org) => void;
}

const SortableOrgItem: React.FC<SortableOrgItemProps> = ({
    org,
    level,
    isSelected,
    isExpanded,
    hasChildren,
    onSelect,
    onToggleExpand,
    onRightClick,
}) => {
    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
        isDragging,
    } = useSortable({ id: org.id });

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
        opacity: isDragging ? 0.5 : 1,
    };

    return (
        <div
            ref={setNodeRef}
            style={style}
            className={cn(
                "flex items-center gap-2 p-2 rounded-lg transition-colors cursor-pointer",
                isSelected ? 'bg-blue-50 text-blue-600' : 'hover:bg-slate-100',
                isDragging && 'shadow-md z-50'
            )}
            onClick={() => onSelect(org)}
            onContextMenu={(e) => onRightClick(e, org)}
        >
            <button
                {...attributes}
                {...listeners}
                className="p-1 hover:bg-slate-200 rounded transition-colors cursor-grab active:cursor-grabbing touch-none"
                onClick={(e) => e.stopPropagation()}
            >
                <GripVertical size={14} className="text-slate-400" />
            </button>
            {hasChildren && (
                <button
                    onClick={(e) => {
                        e.stopPropagation();
                        onToggleExpand(org.id);
                    }}
                    className="p-1 hover:bg-slate-200 rounded transition-colors"
                >
                    {isExpanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
                </button>
            )}
            {!hasChildren && (
                <div className="w-4"></div>
            )}
            <Building2 size={16} className={level === 0 ? "text-blue-600" : "text-slate-600"} />
            <span className={level === 0 ? "font-semibold text-slate-900" : "text-sm text-slate-700"}>
                {org.name}
            </span>
            <span className="text-xs text-slate-400 ml-1">({org.memberCount}人)</span>
            {org.status === 0 && (
                <span className="px-1.5 py-0.5 text-[10px] bg-red-100 text-red-600 rounded font-medium">
                    已禁用
                </span>
            )}
        </div>
    );
};

export default function OrgUserManagement() {
    // 部门相关状态
    const [orgs, setOrgs] = useState<Org[]>([]);
    const [selectedOrg, setSelectedOrg] = useState<Org | null>(null);
    const [orgLoading, setOrgLoading] = useState(true);
    const [expanded, setExpanded] = useState<number[]>([]);

    // 新增状态：搜索、过滤、右键菜单、组织弹窗、拖拽
    const [searchKeyword, setSearchKeyword] = useState('');
    const [filteredOrgs, setFilteredOrgs] = useState<Org[] | null>(null);
    const [rightClickMenu, setRightClickMenu] = useState<{ x: number; y: number; org: Org } | null>(null);
    const [isOrgModalOpen, setIsOrgModalOpen] = useState(false);
    const [orgModalMode, setOrgModalMode] = useState<'add' | 'add-sub' | 'edit'>('add');
    const [selectedOrgForEdit, setSelectedOrgForEdit] = useState<Org | null>(null);
    const [draggingId, setDraggingId] = useState<number | null>(null);

    // 用户相关状态
    const [users, setUsers] = useState<any[]>([]);
    const [userLoading, setUserLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMode, setModalMode] = useState<'add' | 'edit'>('add');
    const [selectedUser, setSelectedUser] = useState<any>(null);
    const [isRoleModalOpen, setIsRoleModalOpen] = useState(false);
    const [selectedUserForRoles, setSelectedUserForRoles] = useState<any>(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    // Notification状态
    const [notification, setNotification] = useState<{
        message: string;
        type: 'success' | 'error' | 'info';
        key: number;
    } | null>(null);

    // DnD sensors 配置
    const sensors = useSensors(
        useSensor(PointerSensor, {
            activationConstraint: {
                distance: 5,
            },
        }),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    // 切换展开/折叠
    const toggleExpand = (id: number) => {
        setExpanded(prev => prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]);
    };

    // 从后端获取组织数据
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
                const buildOrgTree = (orgList: any[]): { orgs: Org[]; expandedIds: number[] } => {
                    const orgMap = new Map<number, Org>();
                    const expandedIds: number[] = [];

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
                            sort: org.sort || 1,
                            status: org.status ?? 1,
                            children: []
                        });
                        expandedIds.push(org.id); // 收集所有ID用于默认展开
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

                    return { orgs: rootOrgs, expandedIds };
                };

                const result = buildOrgTree(data.data);
                setOrgs(result.orgs);
                setExpanded(result.expandedIds); // 设置所有组织为默认展开状态
                // 默认选择第一个组织
                if (result.orgs.length > 0 && !selectedOrg) {
                    setSelectedOrg(result.orgs[0]);
                }
            }
        } catch (error) {
            console.error('Error fetching orgs:', error);
            setNotification({
                message: '获取组织列表失败',
                type: 'error',
                key: Date.now()
            });
        } finally {
            setOrgLoading(false);
        }
    };

    useEffect(() => {
        fetchOrgs();
    }, []);

    // 当选中的组织变化时，获取该组织下的用户
    useEffect(() => {
        if (selectedOrg) {
            fetchUsersByOrg(selectedOrg.id);
        }
    }, [selectedOrg]);

    // 搜索组织功能
    const handleSearchOrgs = () => {
        if (!searchKeyword.trim()) {
            setFilteredOrgs(null);
            return;
        }

        const keyword = searchKeyword.trim().toLowerCase();

        const filterOrgTree = (orgList: Org[]): Org[] => {
            return orgList.reduce((result: Org[], org) => {
                const filteredChildren = org.children?.length
                    ? filterOrgTree(org.children)
                    : [];

                const isNameMatched = org.name.toLowerCase().includes(keyword);

                if (isNameMatched || filteredChildren.length > 0) {
                    result.push({
                        ...org,
                        children: isNameMatched ? org.children : filteredChildren
                    });
                }

                return result;
            }, []);
        };

        const result = filterOrgTree(orgs);
        setFilteredOrgs(result);

        // 展开所有匹配的节点
        if (result.length > 0) {
            const collectAllIds = (orgList: Org[]): number[] => {
                return orgList.reduce((ids: number[], org) => {
                    ids.push(org.id);
                    if (org.children?.length) {
                        ids.push(...collectAllIds(org.children));
                    }
                    return ids;
                }, []);
            };
            setExpanded(collectAllIds(result));
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (e.key === 'Enter') {
            handleSearchOrgs();
        }
    };

    // 刷新组织树
    const handleRefreshOrgs = async () => {
        setSearchKeyword('');
        setFilteredOrgs(null);
        await fetchOrgs();
    };

    // 右键菜单处理
    const handleRightClick = (e: React.MouseEvent, org: Org) => {
        e.preventDefault();
        setRightClickMenu({
            x: e.clientX,
            y: e.clientY,
            org
        });
    };

    // 点击外部关闭右键菜单
    const handleClickOutside = (e: React.MouseEvent) => {
        setRightClickMenu(null);
    };

    // 监听点击事件关闭右键菜单
    useEffect(() => {
        document.addEventListener('click', handleClickOutside as unknown as EventListener);
        return () => {
            document.removeEventListener('click', handleClickOutside as unknown as EventListener);
        };
    }, []);

    // ========== 拖拽相关方法 ==========

    // 拖拽开始
    const handleDragStart = (event: DragStartEvent) => {
        setDraggingId(event.active.id as number);
    };

    // 拖拽结束
    const handleDragEnd = async (event: DragEndEvent) => {
        setDraggingId(null);
        const { active, over } = event;

        if (!over || active.id === over.id) {
            return;
        }

        const draggedId = active.id as number;
        const targetId = over.id as number;

        // 在树中查找节点
        const findOrgById = (orgList: Org[], id: number): Org | null => {
            for (const org of orgList) {
                if (org.id === id) return org;
                if (org.children) {
                    const found = findOrgById(org.children, id);
                    if (found) return found;
                }
            }
            return null;
        };

        // 查找父节点ID
        const findParentId = (orgList: Org[], targetId: number, excludeId: number): number | null => {
            for (const org of orgList) {
                if (org.id === excludeId) continue;
                if (org.children && org.children.some(child => child.id === targetId)) {
                    return org.id;
                }
                if (org.children) {
                    const found = findParentId(org.children, targetId, excludeId);
                    if (found !== null) return found;
                }
            }
            return null;
        };

        // 获取同级节点的索引位置
        const getSiblingIndex = (orgList: Org[], parentId: number | null, targetId: number): number => {
            let siblings: Org[] = [];
            const collectSiblings = (list: Org[]) => {
                for (const org of list) {
                    if ((parentId === null && !org.parentId) || (parentId !== null && org.parentId === parentId)) {
                        siblings.push(org);
                    }
                    if (org.children) collectSiblings(org.children);
                }
            };
            collectSiblings(orgList);
            return siblings.findIndex(m => m.id === targetId);
        };

        const draggedOrg = findOrgById(orgs, draggedId);
        const targetOrg = findOrgById(orgs, targetId);

        if (!draggedOrg || !targetOrg) return;

        const draggedOriginalParentId = draggedOrg.parentId;
        const targetParentId = findParentId(orgs, targetId, draggedId);

        setOrgLoading(true);
        try {
            // 判断是移动到不同父节点还是调整排序
            if (draggedOriginalParentId !== targetParentId) {
                // 移动到新的父节点下
                const response = await fetch('/api/orgs/update', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        id: draggedId,
                        parentId: targetParentId
                    })
                });
                const data = await response.json();
                if (!data.success) {
                    throw new Error(data.errMessage || '移动组织失败');
                }
            } else {
                // 调整同级排序
                const siblingIndex = getSiblingIndex(orgs, targetParentId, targetId);
                const response = await fetch('/api/orgs/update', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        id: draggedId,
                        sort: siblingIndex + 1
                    })
                });
                const data = await response.json();
                if (!data.success) {
                    throw new Error(data.errMessage || '调整排序失败');
                }
            }

            setNotification({
                message: '组织排序已更新',
                type: 'success',
                key: Date.now()
            });

            await fetchOrgs(); // 刷新组织树
        } catch (error: any) {
            console.error('Error during drag:', error);
            setNotification({
                message: error.message || '操作失败',
                type: 'error',
                key: Date.now()
            });
        } finally {
            setOrgLoading(false);
        }
    };

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
                    setNotification({
                        message: isEdit ? '用户编辑成功' : '用户添加成功',
                        type: 'success',
                        key: Date.now()
                    });
                } else {
                    setNotification({
                        message: `${isEdit ? '编辑用户失败' : '添加用户失败'}: ${res.errMessage}`,
                        type: 'error',
                        key: Date.now()
                    });
                }
            })
            .catch(err => {
                console.error(`${isEdit ? 'Error updating user' : 'Error adding user'}:`, err);
                setNotification({
                    message: `${isEdit ? '编辑用户失败' : '添加用户失败'}，请检查网络`,
                    type: 'error',
                    key: Date.now()
                });
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
                        setNotification({
                            message: '用户删除成功',
                            type: 'success',
                            key: Date.now()
                        });
                    } else {
                        setNotification({
                            message: '删除用户失败: ' + res.errMessage,
                            type: 'error',
                            key: Date.now()
                        });
                    }
                })
                .catch(err => {
                    console.error('Error deleting user:', err);
                    setNotification({
                        message: '删除用户失败，请检查网络',
                        type: 'error',
                        key: Date.now()
                    });
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
                        setNotification({
                            message: '密码重置成功，默认密码为 123456，请通知用户及时修改',
                            type: 'success',
                            key: Date.now()
                        });
                    } else {
                        setNotification({
                            message: '重置密码失败: ' + res.errMessage,
                            type: 'error',
                            key: Date.now()
                        });
                    }
                })
                .catch(err => {
                    console.error('Error resetting password:', err);
                    setNotification({
                        message: '重置密码失败，请检查网络',
                        type: 'error',
                        key: Date.now()
                    });
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
            fetch(`/api/users/status`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ id, status: newStatus })
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
                        setNotification({
                            message: `用户状态已切换为${newStatus === 1 ? '活跃' : '禁用'}`,
                            type: 'success',
                            key: Date.now()
                        });
                    } else {
                        setNotification({
                            message: '状态切换失败: ' + res.errMessage,
                            type: 'error',
                            key: Date.now()
                        });
                    }
                })
                .catch(err => {
                    console.error('Error changing status:', err);
                    setNotification({
                        message: '状态切换失败，请检查网络',
                        type: 'error',
                        key: Date.now()
                    });
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
        setUserLoading(true);
        setTimeout(() => {
            setNotification({
                message: '角色分配成功',
                type: 'success',
                key: Date.now()
            });
            if (selectedOrg) {
                fetchUsersByOrg(selectedOrg.id); // 重新加载当前组织的用户列表
            }
            setUserLoading(false);
        }, 500);
    };

    // ========== 组织管理相关方法 ==========

    // 打开新增组织弹窗（从顶部按钮）
    const handleAddOrgFromTopButton = () => {
        setOrgModalMode('add');
        setSelectedOrgForEdit(null);
        setIsOrgModalOpen(true);
    };

    // 打开新增子组织弹窗（从右键菜单）
    const handleAddSubOrg = (parentOrg: Org) => {
        setSelectedOrg(parentOrg);
        setSelectedOrgForEdit(parentOrg);
        setOrgModalMode('add-sub');
        setIsOrgModalOpen(true);
        setRightClickMenu(null);
    };

    // 打开编辑组织弹窗
    const handleEditOrg = (org: Org) => {
        setSelectedOrg(org);
        setSelectedOrgForEdit(org);
        setOrgModalMode('edit');
        setIsOrgModalOpen(true);
        setRightClickMenu(null);
    };

    // 保存组织（新增或编辑）
    const handleSaveOrg = async (orgData: Partial<Org>) => {
        setOrgLoading(true);
        try {
            const isEdit = orgModalMode === 'edit';
            const url = isEdit ? '/api/orgs/update' : '/api/orgs';

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(orgData)
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            if (data.success) {
                setIsOrgModalOpen(false);
                setRightClickMenu(null);
                setNotification({
                    message: isEdit ? '组织编辑成功' : '组织新增成功',
                    type: 'success',
                    key: Date.now()
                });
                await fetchOrgs(); // 刷新组织树
            } else {
                throw new Error(data.errMessage || '保存失败');
            }
        } catch (error: any) {
            console.error('Error saving org:', error);
            setNotification({
                message: error.message || '保存组织失败',
                type: 'error',
                key: Date.now()
            });
        } finally {
            setOrgLoading(false);
        }
    };

    // 删除组织
    const handleDeleteOrg = async (id: number) => {
        if (window.confirm('删除该组织后，其下所有子组织和关联用户将一并删除且不可恢复，是否确认删除？')) {
            setOrgLoading(true);
            try {
                const response = await fetch('/api/orgs/delete', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ id })
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();

                if (data.success) {
                    setRightClickMenu(null);
                    if (selectedOrg?.id === id) {
                        setSelectedOrg(null);
                    }
                    setNotification({
                        message: '组织删除成功',
                        type: 'success',
                        key: Date.now()
                    });
                    await fetchOrgs(); // 刷新组织树
                } else {
                    throw new Error(data.errMessage || '删除失败');
                }
            } catch (error: any) {
                console.error('Error deleting org:', error);
                setNotification({
                    message: error.message || '删除组织失败',
                    type: 'error',
                    key: Date.now()
                });
            } finally {
                setOrgLoading(false);
            }
        }
    };

    // 切换组织状态（启用/禁用）
    const handleChangeOrgStatus = async (id: number, currentStatus: number) => {
        const newStatus = currentStatus === 1 ? 0 : 1;
        setOrgLoading(true);
        try {
            const response = await fetch('/api/orgs/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    id,
                    status: newStatus
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();

            if (data.success) {
                setRightClickMenu(null);
                setNotification({
                    message: `组织状态已切换为${newStatus === 1 ? '启用' : '禁用'}`,
                    type: 'success',
                    key: Date.now()
                });
                await fetchOrgs(); // 刷新组织树
            } else {
                throw new Error(data.errMessage || '状态更新失败');
            }
        } catch (error: any) {
            console.error('Error changing org status:', error);
            setNotification({
                message: error.message || '切换组织状态失败',
                type: 'error',
                key: Date.now()
            });
        } finally {
            setOrgLoading(false);
        }
    };

    // 获取显示的组织数据（原始或过滤后）
    const displayOrgs = filteredOrgs !== null ? filteredOrgs : orgs;

    // 渲染组织树（递归）
    const renderOrgTree = (orgList: Org[], level = 0) => {
        return (
            <SortableContext items={orgList.map(o => o.id)} strategy={verticalListSortingStrategy}>
                {orgList.map(org => (
                    <React.Fragment key={org.id}>
                        <SortableOrgItem
                            org={org}
                            level={level}
                            isSelected={selectedOrg?.id === org.id}
                            isExpanded={expanded.includes(org.id)}
                            hasChildren={!!(org.children && org.children.length > 0)}
                            onSelect={setSelectedOrg}
                            onToggleExpand={toggleExpand}
                            onRightClick={handleRightClick}
                        />
                        {org.children && org.children.length > 0 && expanded.includes(org.id) && (
                            <div className={`ml-4 border-l-2 border-slate-200 pl-2 mt-1`}>
                                {renderOrgTree(org.children, level + 1)}
                            </div>
                        )}
                    </React.Fragment>
                ))}
            </SortableContext>
        );
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

            <OrgModal
                isOpen={isOrgModalOpen}
                onClose={() => setIsOrgModalOpen(false)}
                onSave={handleSaveOrg}
                org={selectedOrgForEdit || undefined}
                mode={orgModalMode}
                allOrgs={displayOrgs}
                hideParentSelect={orgModalMode === 'add-sub'}
            />

            <div className="grid grid-cols-12 gap-6">
                {/* 左侧组织树 */}
                <div className="col-span-12 lg:col-span-3">
                    <div
                        className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden h-[calc(100vh-300px)]">
                        {/* 标题 */}
                        <div className="p-4 border-b border-slate-100 bg-slate-50/50">
                            <h3 className="font-medium text-slate-900">组织树</h3>
                        </div>

                        {/* 搜索框 */}
                        <div className="p-4 border-b border-slate-100">
                            <div className="flex items-center gap-2 bg-slate-100 px-3 py-2 rounded-lg">
                                <Search size={16} className="text-slate-400"/>
                                <input
                                    type="text"
                                    placeholder="搜索组织..."
                                    className="text-sm outline-none w-full bg-transparent"
                                    value={searchKeyword}
                                    onChange={(e) => setSearchKeyword(e.target.value)}
                                    onKeyDown={handleKeyDown}
                                />
                            </div>
                        </div>

                        {/* 工具栏：新增按钮 + 刷新按钮 */}
                        <div className="p-4 border-b border-slate-100 flex justify-between items-center">
                            <button
                                onClick={handleAddOrgFromTopButton}
                                className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition-all shadow-md"
                            >
                                <Plus size={16}/>
                                新增组织
                            </button>
                            <button
                                onClick={handleRefreshOrgs}
                                className="p-2 text-slate-500 hover:text-slate-900 hover:bg-slate-100 rounded-lg transition-colors"
                                title="刷新"
                            >
                                <MoveUp size={16}/>
                            </button>
                        </div>

                        {/* 组织树内容 */}
                        <div className="p-4 overflow-y-auto h-[calc(100%-220px)]">
                            {orgLoading ? (
                                <div className="flex items-center justify-center py-12">
                                    <div
                                        className="w-6 h-6 border-2 border-blue-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
                                </div>
                            ) : displayOrgs.length === 0 ? (
                                <div className="text-center py-12 text-slate-500">
                                    <Building2 size={48} className="mx-auto text-slate-300 mb-4"/>
                                    <p>暂无组织数据</p>
                                </div>
                            ) : (
                                <DndContext
                                    sensors={sensors}
                                    collisionDetection={closestCenter}
                                    onDragStart={handleDragStart}
                                    onDragEnd={handleDragEnd}
                                >
                                    {renderOrgTree(displayOrgs)}
                                </DndContext>
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
                                    onKeyDown={(e) => {
                                        if (e.key === 'Enter') {
                                            if (selectedOrg) {
                                                fetchUsersByOrg(selectedOrg.id, 1);
                                            }
                                        }
                                    }}
                                />
                            </div>
                            <button
                                onClick={() => {
                                    if (selectedOrg) {
                                        fetchUsersByOrg(selectedOrg.id, 1);
                                    }
                                }}
                                disabled={!selectedOrg || userLoading}
                                className="flex items-center gap-2 bg-white text-slate-600 border border-slate-200 px-4 py-2 rounded-xl font-medium hover:bg-slate-50 transition-all shadow-sm disabled:opacity-50"
                            >
                                查询
                            </button>
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
                                                {formatDateTime(user.createdTime)}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="text-sm text-slate-600">{user.updateUserId || '-'}</span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="text-sm text-slate-600">
                                                {formatDateTime(user.updateTime)}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 text-right">
                                            <div className="flex items-center justify-end gap-3">
                                                <button
                                                    onClick={() => handleEditUser(user)}
                                                    className="text-sm text-blue-600 hover:text-blue-800 font-medium"
                                                >
                                                    编辑
                                                </button>
                                                <button
                                                    onClick={() => handleResetPassword(user.id)}
                                                    className="text-sm text-amber-600 hover:text-amber-800 font-medium"
                                                >
                                                    重置密码
                                                </button>
                                                <button
                                                    onClick={() => handleChangeStatus(user.id, user.status)}
                                                    className="text-sm text-purple-600 hover:text-purple-800 font-medium"
                                                >
                                                    {user.status === 1 ? '禁用' : '启用'}
                                                </button>
                                                <button
                                                    onClick={() => handleAssignRoles(user)}
                                                    className="text-sm text-emerald-600 hover:text-emerald-800 font-medium"
                                                >
                                                    分配角色
                                                </button>
                                                <button
                                                    onClick={() => handleDeleteUser(user.id)}
                                                    className="text-sm text-red-600 hover:text-red-800 font-medium"
                                                >
                                                    删除
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

            {/* 右键菜单 */}
            {rightClickMenu && (
                <div
                    className="fixed z-50 bg-white rounded-lg shadow-lg border border-slate-200 py-2 min-w-48"
                    style={{ left: rightClickMenu.x, top: rightClickMenu.y }}
                >
                    <button
                        onClick={() => handleAddSubOrg(rightClickMenu.org)}
                        className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                    >
                        <Plus size={14}/>
                        新增子组织
                    </button>
                    <button
                        onClick={() => handleEditOrg(rightClickMenu.org)}
                        className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                    >
                        <Edit size={14}/>
                        编辑
                    </button>
                    <button
                        onClick={() => {
                            handleChangeOrgStatus(rightClickMenu.org.id, rightClickMenu.org.status ?? 1);
                            setRightClickMenu(null);
                        }}
                        className="flex items-center gap-2 w-full px-4 py-2 text-sm text-slate-700 hover:bg-slate-50 transition-colors"
                    >
                        {rightClickMenu.org.status === 1 ? <EyeOff size={14}/> : <Eye size={14}/>}
                        {rightClickMenu.org.status === 1 ? '禁用' : '启用'}
                    </button>
                    <div className="border-t border-slate-100 my-1"></div>
                    <button
                        onClick={() => {
                            handleDeleteOrg(rightClickMenu.org.id);
                            setRightClickMenu(null);
                        }}
                        className="flex items-center gap-2 w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                    >
                        <Trash2 size={14}/>
                        删除
                    </button>
                </div>
            )}

            {/* Notification 组件 */}
            {notification && (
                <Notification
                    key={notification.key}
                    message={notification.message}
                    type={notification.type}
                />
            )}
        </div>
    );
}
