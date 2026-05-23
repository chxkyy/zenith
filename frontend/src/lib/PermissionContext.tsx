import React, { createContext, useContext } from 'react';

interface Menu {
  id: number;
  parentId: number;
  name: string;
  path: string;
  component: string;
  icon: string;
  sort: number;
  status: number;
  type: string;
  permission: string;
}

interface PermissionContextValue {
  permissions: string[];
  menus: Menu[];
}

const PermissionContext = createContext<PermissionContextValue>({ permissions: [], menus: [] });

export const PermissionProvider = PermissionContext.Provider;

export function usePermission() {
  const { permissions } = useContext(PermissionContext);
  return {
    hasPermission: (perm: string) =>
      permissions.includes('*') || permissions.includes(perm),
  };
}

export function useMenus() {
  const { menus } = useContext(PermissionContext);
  return menus;
}
