import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { Modal, Tree, Table, App, Spin, Empty, Checkbox } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type { DataNode, TreeDataNode } from 'antd/es/tree';

interface FunctionPermission {
  id: number;
  name: string;
  permission: string;
  menuId: number;
  sort: number;
  status: number;
}

interface MenuTreeNode {
  id: number;
  name: string;
  type: string;
  parentId: number | null;
  children?: MenuTreeNode[];
}

interface PermissionAssignModalProps {
  isOpen: boolean;
  onClose: () => void;
  roleId: number;
  roleName: string;
}

export default function PermissionAssignModal({ isOpen, onClose, roleId, roleName }: PermissionAssignModalProps) {
  const { message } = App.useApp();
  const [menus, setMenus] = useState<MenuTreeNode[]>([]);
  const [allFunctions, setAllFunctions] = useState<FunctionPermission[]>([]);
  const [originalCheckedIds, setOriginalCheckedIds] = useState<Set<number>>(new Set());
  const [checkedState, setCheckedState] = useState<Map<number, Set<number>>>(new Map());
  const [selectedMenuId, setSelectedMenuId] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const [menusRes, funcsRes, rolePermsRes] = await Promise.all([
        fetch('/api/menus'),
        fetch('/api/functions/list-all'),
        fetch(`/api/role-permissions/functions?roleId=${roleId}`)
      ]);

      const menusData = await menusRes.json();
      const funcsData = await funcsRes.json();
      const rolePermsData = await rolePermsRes.json();

      if (menusData.success) {
        const rawMenus = menusData.data || [];
        const menuMap = new Map<number, MenuTreeNode>();
        rawMenus.forEach((m: any) => {
          menuMap.set(m.id, {
            id: m.id,
            name: m.name,
            type: m.type,
            parentId: m.parentId,
            children: []
          });
        });
        const rootMenus: MenuTreeNode[] = [];
        menuMap.forEach(menu => {
          if (!menu.parentId) {
            rootMenus.push(menu);
          } else {
            const parent = menuMap.get(menu.parentId);
            if (parent) parent.children!.push(menu);
          }
        });
        setMenus(rootMenus);
      }

      if (funcsData.success) {
        setAllFunctions(funcsData.data || []);
      }

      if (rolePermsData.success) {
        const ids = new Set(rolePermsData.data || []);
        setOriginalCheckedIds(ids);
        initCheckedStateFromIds(ids, funcsData.data || [], menusData.data || []);
      }
    } catch (error) {
      console.error('Error fetching permission data:', error);
      message.error('获取权限数据失败');
    } finally {
      setLoading(false);
    }
  }, [roleId, message]);

  const initCheckedStateFromIds = (ids: Set<number>, functions: FunctionPermission[], rawMenus: any[]) => {
    const menuFuncMap = new Map<number, number[]>();
    functions.forEach((f: FunctionPermission) => {
      if (ids.has(f.id)) {
        const existing = menuFuncMap.get(f.menuId) || [];
        existing.push(f.id);
        menuFuncMap.set(f.menuId, existing);
      }
    });

    const state = new Map<number, Set<number>>();
    const collectMenuIds = (menuList: any[]) => {
      menuList.forEach((m: any) => {
        state.set(m.id, new Set(menuFuncMap.get(m.id) || []));
        // Note: nested menus would need recursive handling if the API returns flat list
      });
    };
    collectMenuIds(rawMenus);

    // Also ensure all menus from tree are in state
    const ensureAllMenus = (treeNodes: MenuTreeNode[]) => {
      treeNodes.forEach(node => {
        if (!state.has(node.id)) {
          state.set(node.id, new Set());
        }
        if (node.children && node.children.length > 0) {
          ensureAllMenus(node.children);
        }
      });
    };

    setCheckedState(state);
  };

  useEffect(() => {
    if (isOpen && roleId) {
      setSelectedMenuId(null);
      fetchData();
    }
  }, [isOpen, roleId, fetchData]);

  const getFunctionsForMenu = useCallback((menuId: number): FunctionPermission[] => {
    return allFunctions
      .filter(f => f.menuId === menuId)
      .sort((a, b) => a.sort - b.sort);
  }, [allFunctions]);

  const currentFunctions = useMemo(() => {
    if (!selectedMenuId) return [];
    return getFunctionsForMenu(selectedMenuId);
  }, [selectedMenuId, getFunctionsForMenu]);

  const currentCheckedIds = useMemo(() => {
    if (!selectedMenuId) return new Set<number>();
    return checkedState.get(selectedMenuId) || new Set();
  }, [selectedMenuId, checkedState]);

  const handleCheckChange = (funcId: number, checked: boolean) => {
    if (!selectedMenuId) return;
    setCheckedState(prev => {
      const next = new Map(prev);
      const menuSet = new Set(next.get(selectedMenuId) || []);
      if (checked) {
        menuSet.add(funcId);
      } else {
        menuSet.delete(funcId);
      }
      next.set(selectedMenuId, menuSet);
      return next;
    });
  };

  const handleSelectAll = (checked: boolean) => {
    if (!selectedMenuId) return;
    setCheckedState(prev => {
      const next = new Map(prev);
      if (checked) {
        next.set(selectedMenuId, new Set(currentFunctions.map(f => f.id)));
      } else {
        next.set(selectedMenuId, new Set());
      }
      return next;
    });
  };

  const getMenuCheckedInfo = useCallback((menuId: number): { checked: boolean; indeterminate: boolean; total: number; checkedCount: number } => {
    const menuFuncs = allFunctions.filter(f => f.menuId === menuId);
    const total = menuFuncs.length;
    if (total === 0) return { checked: false, indeterminate: false, total: 0, checkedCount: 0 };
    const menuChecked = checkedState.get(menuId) || new Set();
    const checkedCount = menuFuncs.filter(f => menuChecked.has(f.id)).length;
    return {
      checked: checkedCount === total && total > 0,
      indeterminate: checkedCount > 0 && checkedCount < total,
      total,
      checkedCount
    };
  }, [allFunctions, checkedState]);

  const convertToTreeData = (menuList: MenuTreeNode[]): TreeDataNode[] => {
    return menuList.map(menu => {
      const { checked, indeterminate } = getMenuCheckedInfo(menu.id);
      return {
        key: `menu-${menu.id}`,
        title: (
          <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Checkbox
              checked={checked}
              indeterminate={indeterminate}
              onClick={(e) => e.stopPropagation()}
              onChange={(e) => {
                const menuFuncs = allFunctions.filter(f => f.menuId === menu.id);
                setCheckedState(prev => {
                  const next = new Map(prev);
                  if (e.target.checked) {
                    next.set(menu.id, new Set(menuFuncs.map(f => f.id)));
                  } else {
                    next.set(menu.id, new Set());
                  }
                  return next;
                });
              }}
            />
            <span>{menu.name}</span>
            <span style={{ fontSize: 12, color: '#999' }}>({getMenuCheckedInfo(menu.id).checkedCount}/{getMenuCheckedInfo(menu.id).total})</span>
          </span>
        ),
        children: menu.children && menu.children.length > 0 ? convertToTreeData(menu.children) : undefined,
      } as TreeDataNode;
    });
  };

  const treeData = useMemo(() => convertToTreeData(menus), [menus, checkedState, allFunctions, getMenuCheckedInfo]);

  const columns: ColumnsType<FunctionPermission> = [
    {
      title: '权限名称',
      dataIndex: 'name',
      key: 'name',
      width: 140,
    },
    {
      title: '权限标识',
      dataIndex: 'permission',
      key: 'permission',
      width: 200,
      render: (v) => <code style={{ fontSize: 12 }}>{v || '-'}</code>,
    },
    {
      title: '排序号',
      dataIndex: 'sort',
      key: 'sort',
      width: 80,
      align: 'center',
    },
    {
      title: '授权状态',
      key: 'checked',
      width: 100,
      align: 'center',
      render: (_, record) => (
        <Checkbox
          checked={currentCheckedIds.has(record.id)}
          onChange={(e) => handleCheckChange(record.id, e.target.checked)}
        />
      ),
    },
  ];

  const getAllCheckedFunctionIds = (): number[] => {
    const allIds: number[] = [];
    checkedState.forEach(ids => {
      ids.forEach(id => allIds.push(id));
    });
    return allIds;
  };

  const hasChanges = useMemo(() => {
    const currentAllIds = new Set(getAllCheckedFunctionIds());
    if (currentAllIds.size !== originalCheckedIds.size) return true;
    for (const id of currentAllIds) {
      if (!originalCheckedIds.has(id)) return true;
    }
    return false;
  }, [checkedState, originalCheckedIds]);

  const handleSave = async () => {
    setSaving(true);
    try {
      const functionIds = getAllCheckedFunctionIds();

      const response = await fetch('/api/role-permissions/assign', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ roleId, functionIds })
      });
      const data = await response.json();
      if (data.success) {
        message.success('权限分配成功');
        onClose();
      } else {
        throw new Error(data.errMessage || '分配失败');
      }
    } catch (error: any) {
      console.error('Error assigning permissions:', error);
      message.error(error.message || '权限分配失败');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      title={`分配权限 - ${roleName}`}
      open={isOpen}
      onCancel={onClose}
      onOk={handleSave}
      okText="保存分配"
      cancelText="取消"
      confirmLoading={saving}
      width={960}
      destroyOnHidden
      styles={{ body: { padding: 0, height: 500, display: 'flex' } }}
    >
      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
          <Spin description="加载权限数据..." />
        </div>
      ) : (
        <div style={{ display: 'flex', height: '100%' }}>
          {/* Left: Menu Tree */}
          <div style={{
            width: 300,
            borderRight: '1px solid #f0f0f0',
            overflowY: 'auto',
            padding: '12px 0',
            background: '#fafafa'
          }}>
            <div style={{ padding: '0 16px 12px', fontSize: 13, fontWeight: 600, color: '#333' }}>
              菜单列表
              {hasChanges && (
                <span style={{ marginLeft: 8, fontSize: 12, color: '#faad14', fontWeight: 400 }}>
                  （有未保存的变更）
                </span>
              )}
            </div>
            <Tree
              treeData={treeData}
              defaultExpandAll
              selectedKeys={selectedMenuId ? [`menu-${selectedMenuId}`] : []}
              onSelect={(keys) => {
                const key = keys[0];
                if (key && String(key).startsWith('menu-')) {
                  setSelectedMenuId(Number(String(key).replace('menu-', '')));
                }
              }}
              showIcon={false}
            />
          </div>

          {/* Right: Function Table */}
          <div style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
            {!selectedMenuId ? (
              <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Empty description="请选择左侧菜单以查看按钮权限" />
              </div>
            ) : (
              <>
                <div style={{
                  padding: '12px 16px',
                  borderBottom: '1px solid #f0f0f0',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  background: '#fff'
                }}>
                  <span style={{ fontSize: 14, fontWeight: 600 }}>
                    按钮权限列表
                    <span style={{ marginLeft: 8, fontSize: 12, color: '#999', fontWeight: 400 }}>
                      ({currentCheckedIds.size}/{currentFunctions.length} 已授权)
                    </span>
                  </span>
                  <span style={{ fontSize: 13, color: '#666' }}>
                    <Checkbox
                      checked={currentFunctions.length > 0 && currentCheckedIds.size === currentFunctions.length}
                      indeterminate={currentCheckedIds.size > 0 && currentCheckedIds.size < currentFunctions.length}
                      onChange={(e) => handleSelectAll(e.target.checked)}
                    >
                      全选
                    </Checkbox>
                  </span>
                </div>
                <div style={{ flex: 1, overflow: 'auto' }}>
                  <Table<FunctionPermission>
                    columns={columns}
                    dataSource={currentFunctions}
                    rowKey="id"
                    size="small"
                    pagination={false}
                    locale={{ emptyText: '该菜单下暂无按钮权限' }}
                  />
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </Modal>
  );
}
