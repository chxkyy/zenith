package com.zenith.admin.api;

import com.zenith.admin.dto.data.MenuDTO;
import java.util.List;

public interface PermissionService {
    List<String> getUserRoles(Long userId);
    List<String> getUserPermissions(Long userId);
    List<MenuDTO> getAccessibleMenus(Long userId);
    boolean hasPermission(Long userId, String permission);
}
