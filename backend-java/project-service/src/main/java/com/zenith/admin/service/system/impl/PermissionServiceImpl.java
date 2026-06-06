package com.zenith.admin.service.system.impl;

import com.zenith.admin.api.PermissionService;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.service.system.executor.cmd.PermissionAssignCmdExe;
import com.zenith.admin.service.system.executor.cmd.UserRolesUpdateCmdExe;
import com.zenith.admin.service.system.executor.qry.AccessibleMenusQryExe;
import com.zenith.admin.service.system.executor.qry.PermissionResolveQryExe;
import com.zenith.admin.service.system.executor.qry.RoleMenuQryExe;
import com.zenith.admin.service.system.executor.qry.RolePermissionQryExe;
import com.zenith.admin.service.system.executor.qry.UserRolesQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务实现
 * 纯编排层，所有业务逻辑委托给对应的 Executor 执行：
 * <ul>
 *   <li>PermissionAssignCmdExe - 角色权限分配（含事务、快照、日志）</li>
 *   <li>PermissionResolveQryExe - 用户权限解析（含缓存）</li>
 *   <li>AccessibleMenusQryExe - 可访问菜单查询（含树展开）</li>
 *   <li>UserRolesQryExe / UserRolesUpdateCmdExe - 用户角色管理</li>
 *   <li>RolePermissionQryExe / RoleMenuQryExe - 角色权限/菜单查询</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final UserRolesQryExe userRolesQryExe;
    private final PermissionResolveQryExe permissionResolveQryExe;
    private final AccessibleMenusQryExe accessibleMenusQryExe;
    private final PermissionAssignCmdExe permissionAssignCmdExe;
    private final UserRolesUpdateCmdExe userRolesUpdateCmdExe;
    private final RolePermissionQryExe rolePermissionQryExe;
    private final RoleMenuQryExe roleMenuQryExe;

    @Override
    public List<String> getUserRoles(Long userId) {
        List<Long> roleIds = userRolesQryExe.getRoleIds(userId);
        return roleIds.stream().map(String::valueOf).toList();
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return permissionResolveQryExe.execute(userId);
    }

    @Override
    public List<MenuDTO> getAccessibleMenus(Long userId) {
        return accessibleMenusQryExe.execute(userId);
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        return permissionResolveQryExe.hasPermission(userId, permission);
    }

    @Override
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        userRolesUpdateCmdExe.execute(userId, roleIds);
        permissionResolveQryExe.invalidateCache(userId);
    }

    @Override
    public void assignRolePermissions(Long roleId, List<Long> functionIds, List<Long> menuIds) {
        permissionAssignCmdExe.execute(roleId, functionIds, menuIds);
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        return rolePermissionQryExe.execute(roleId);
    }

    @Override
    public List<Long> getRoleMenus(Long roleId) {
        return roleMenuQryExe.execute(roleId);
    }
}
