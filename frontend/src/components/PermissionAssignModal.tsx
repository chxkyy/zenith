import React, { useState, useEffect } from 'react';
import { Modal, Tree, App, Spin } from 'antd';
import type { TreeProps } from 'antd';
import { CheckCircleFilled } from '@ant-design/icons';

interface Permission {
  id: number;
  name: string;
  permission: string;
  type: string;
  menuId: number;
  sort: number;
  status: number;
}

interface Menu {
  id: number;
  name: string;
  type: string;
  path: string;
  icon: string;
  parentId: number | null;
  sort: number;
  status: number;
  children?: Menu[];
  functions?: Permission[];
}

interface PermissionAssignModalProps {
  isOpen: boolean;
  onClose: () => void;
  roleId: number;
  roleName: string;
}

export default function PermissionAssignModal({ isOpen, onClose, roleId, roleName }: PermissionAssignModalProps) {
  const { message } = App.useApp();
  const [menus, setMenus] = useState<Menu[]>([]);
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [checkedKeys, setCheckedKeys] = useState<React.Key[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (isOpen && roleId) {
      fetchData();
    }
  }, [isOpen, roleId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [menusRes, permsRes, rolePermsRes] = await Promise.all([
        fetch('/api/menus'),
        fetch('/api/functions/list-all'),
        fetch(`/api/role-permissions?roleId=${roleId}`)
      ]);

      const menusData = await menusRes.json();
      const permsData = await permsRes.json();
      const rolePermsData = await rolePermsRes.json();

      if (menusData.success) setMenus(menusData.data || []);
      if (permsData.success) setPermissions(permsData.data || []);
      if (rolePermsData.success) {
        setCheckedKeys((rolePermsData.data || []).map((id: number) => `perm-${id}`));
      }
    } catch (error) {
      console.error('Error fetching permission data:', error);
      message.error('获取权限数据失败');
    } finally {
      setLoading(false);
    }
  };

  const buildTreeData = (): TreeProps['treeData'] => {
    const menuMap = new Map<number, Menu>();
    menus.forEach(menu => {
      menuMap.set(menu.id, { ...menu, children: [], functions: [] });
    });

    const menuFunctions = new Map<number, Permission[]>();
    permissions.forEach(perm => {
      const list = menuFunctions.get(perm.menuId) || [];
      list.push(perm);
      menuFunctions.set(perm.menuId, list);
    });

    const rootMenus: Menu[] = [];
    menuMap.forEach(menu => {
      const menuPerms = menuFunctions.get(menu.id);
      if (menuPerms) (menu as any).functions = menuPerms;
      if (!menu.parentId) { rootMenus.push(menu); }
      else {
        const parent = menuMap.get(menu.parentId);
        if (parent) parent.children?.push(menu);
      }
    });

    const convertToTreeNode = (menuList: Menu[]): TreeProps['treeData'] => {
      return menuList.map(menu => ({
        key: `menu-${menu.id}`,
        title: menu.name,
        children: [
          ...(menu.functions && (menu as any).functions.length > 0
            ? (menu as any).functions.map((perm: Permission) => ({
                key: `perm-${perm.id}`,
                title: perm.name,
                isLeaf: true,
              }))
            : []),
          ...(menu.children && menu.children.length > 0
            ? convertToTreeNode(menu.children)
            : [])
        ]
      }));
    };

    return convertToTreeNode(rootMenus);
  };

  const collectFunctionIds = (keys: React.Key[]): number[] => {
    const directPermIds = keys
      .filter(key => String(key).startsWith('perm-'))
      .map(key => Number(String(key).replace('perm-', '')));

    const checkedMenuIds = keys
      .filter(key => String(key).startsWith('menu-'))
      .map(key => Number(String(key).replace('menu-', '')));

    const menuPermIds = permissions
      .filter(perm => checkedMenuIds.includes(perm.menuId))
      .map(perm => perm.id);

    return [...new Set([...directPermIds, ...menuPermIds])];
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const functionIds = collectFunctionIds(checkedKeys);

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
      confirmLoading={saving}
      width={600}
      destroyOnHidden
    >
      {loading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}><Spin description="加载权限数据..." /></div>
      ) : (
        <Tree
          checkable
          checkedKeys={checkedKeys}
          onCheck={(keys) => {
            const checked = Array.isArray(keys) ? keys : (keys as any).checked || [];
            setCheckedKeys(checked);
          }}
          treeData={buildTreeData()}
          defaultExpandAll
          style={{ marginTop: 16 }}
        />
      )}
    </Modal>
  );
}
