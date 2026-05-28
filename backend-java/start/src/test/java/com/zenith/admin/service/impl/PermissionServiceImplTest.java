package com.zenith.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.RoleMenuDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.mapper.RoleMenuMapper;
import com.zenith.admin.mapper.UserRoleMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PermissionServiceImplTest {

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private RoleMenuMapper roleMenuMapper;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private UserRoleDO testUserRole;
    private RoleMenuDO testRoleMenu;

    @BeforeEach
    void setUp() {
        testUserRole = new UserRoleDO();
        testUserRole.setId(1L);
        testUserRole.setUserId(1L);
        testUserRole.setRoleId(2L);

        testRoleMenu = new RoleMenuDO();
        testRoleMenu.setId(1L);
        testRoleMenu.setRoleId(2L);
        testRoleMenu.setMenuId(3L);
    }

    @Test
    @DisplayName("获取用户角色列表")
    void testGetUserRoles_Success() {
        when(userRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(testUserRole));

        List<String> roles = permissionService.getUserRoles(1L);

        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("2", roles.get(0));
    }

    @Test
    @DisplayName("获取用户角色列表 - 无角色")
    void testGetUserRoles_NoRoles() {
        when(userRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

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
    @DisplayName("根据用户ID获取角色ID列表")
    void testGetRolesByUserId_Success() {
        when(userRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(testUserRole));

        List<Long> roleIds = permissionService.getRolesByUserId(1L);

        assertNotNull(roleIds);
        assertEquals(1, roleIds.size());
        assertEquals(2L, roleIds.get(0));
    }

    @Test
    @DisplayName("根据角色ID获取菜单ID列表")
    void testGetRoleMenus_Success() {
        when(roleMenuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(testRoleMenu));

        List<Long> menuIds = permissionService.getRoleMenus(2L);

        assertNotNull(menuIds);
        assertEquals(1, menuIds.size());
        assertEquals(3L, menuIds.get(0));
    }

    @Test
    @DisplayName("检查用户是否有指定菜单权限 - 有权限")
    void testHasPermissionByMenuId_HasPermission() {
        when(userRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(testUserRole));
        when(roleMenuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(testRoleMenu));

        boolean result = permissionService.hasPermission(1L, 3L);

        assertTrue(result);
    }

    @Test
    @DisplayName("检查用户是否有指定菜单权限 - 无权限")
    void testHasPermissionByMenuId_NoPermission() {
        when(userRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(testUserRole));
        when(roleMenuMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        boolean result = permissionService.hasPermission(1L, 99L);

        assertFalse(result);
    }

    @Test
    @DisplayName("检查用户是否有指定菜单权限 - 无角色")
    void testHasPermissionByMenuId_NoRoles() {
        when(userRoleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        boolean result = permissionService.hasPermission(999L, 3L);

        assertFalse(result);
    }
}
