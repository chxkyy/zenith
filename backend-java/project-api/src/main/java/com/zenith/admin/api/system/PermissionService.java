package com.zenith.admin.api.system;

import com.zenith.admin.dto.data.MenuDTO;
import java.util.List;

public interface PermissionService {
    List<String> getUserRoles(Long userId);
    List<String> getUserPermissions(Long userId);
    List<MenuDTO> getAccessibleMenus(Long userId);
    boolean hasPermission(Long userId, String permission);
    void updateUserRoles(Long userId, List<Long> roleIds);

    void assignRolePermissions(Long roleId, List<Long> functionIds, List<Long> menuIds);

    List<Long> getRolePermissions(Long roleId);

    List<Long> getRoleMenus(Long roleId);
}
