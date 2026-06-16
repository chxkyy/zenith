package com.zenith.admin.service.impl;

import com.zenith.admin.dto.system.data.MenuDTO;
import com.zenith.admin.service.system.executor.qry.AccessibleMenusQryExe;
import com.zenith.admin.service.system.executor.qry.PermissionResolveQryExe;
import com.zenith.admin.service.system.executor.qry.RoleMenuQryExe;
import com.zenith.admin.service.system.executor.qry.RolePermissionQryExe;
import com.zenith.admin.service.system.executor.qry.UserRolesQryExe;
import com.zenith.admin.service.system.impl.PermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PermissionServiceImplTest {

    @Mock
    private UserRolesQryExe userRolesQryExe;

    @Mock
    private PermissionResolveQryExe permissionResolveQryExe;

    @Mock
    private AccessibleMenusQryExe accessibleMenusQryExe;

    @Mock
    private RolePermissionQryExe rolePermissionQryExe;

    @Mock
    private RoleMenuQryExe roleMenuQryExe;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @BeforeEach
    void setUp() {
        // 默认返回空列表
        when(userRolesQryExe.getRoleIds(anyLong())).thenReturn(Collections.emptyList());
        when(permissionResolveQryExe.execute(anyLong())).thenReturn(Collections.emptyList());
        when(accessibleMenusQryExe.execute(anyLong())).thenReturn(Collections.emptyList());
        when(rolePermissionQryExe.execute(anyLong())).thenReturn(Collections.emptyList());
        when(roleMenuQryExe.execute(anyLong())).thenReturn(Collections.emptyList());
        when(permissionResolveQryExe.hasPermission(anyLong(), anyString())).thenReturn(false);
    }

    @Test
    @DisplayName("获取用户角色列表")
    void testGetUserRoles_Success() {
        when(userRolesQryExe.getRoleIds(1L)).thenReturn(Arrays.asList(2L));

        List<String> roles = permissionService.getUserRoles(1L);

        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("2", roles.get(0));
    }

    @Test
    @DisplayName("获取用户角色列表 - 无角色")
    void testGetUserRoles_NoRoles() {
        when(userRolesQryExe.getRoleIds(999L)).thenReturn(Collections.emptyList());

        List<String> roles = permissionService.getUserRoles(999L);

        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    @DisplayName("获取用户权限列表 - 返回空列表")
    void testGetUserPermissions_ReturnsEmpty() {
        List<String> permissions = permissionService.getUserPermissions(1L);

        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    @DisplayName("获取可访问菜单 - 返回空列表")
    void testGetAccessibleMenus_ReturnsEmpty() {
        List<MenuDTO> menus = permissionService.getAccessibleMenus(1L);

        assertNotNull(menus);
        assertTrue(menus.isEmpty());
    }

    @Test
    @DisplayName("检查权限 - 返回false")
    void testHasPermission_ReturnsFalse() {
        boolean result = permissionService.hasPermission(1L, "user:read");

        assertFalse(result);
    }

    @Test
    @DisplayName("根据角色ID获取菜单ID列表")
    void testGetRoleMenus_Success() {
        when(roleMenuQryExe.execute(2L)).thenReturn(Arrays.asList(3L));

        List<Long> menuIds = permissionService.getRoleMenus(2L);

        assertNotNull(menuIds);
        assertEquals(1, menuIds.size());
        assertEquals(3L, menuIds.get(0));
    }

    @Test
    @DisplayName("根据角色ID获取权限ID列表")
    void testGetRolePermissions_Success() {
        when(rolePermissionQryExe.execute(1L)).thenReturn(Arrays.asList(10L, 20L));

        List<Long> permIds = permissionService.getRolePermissions(1L);

        assertNotNull(permIds);
        assertEquals(2, permIds.size());
        assertEquals(10L, permIds.get(0));
        assertEquals(20L, permIds.get(1));
    }
}
