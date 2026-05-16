import React, { createContext, useContext } from 'react';

const PermissionContext = createContext<string[]>([]);

export const PermissionProvider = PermissionContext.Provider;

export function usePermission() {
  const permissions = useContext(PermissionContext);
  return {
    hasPermission: (perm: string) =>
      permissions.includes('*') || permissions.includes(perm),
  };
}
