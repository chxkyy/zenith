package com.zenith.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.PermissionService;
import com.zenith.admin.dataobject.RoleFunctionDO;
import com.zenith.admin.dataobject.RoleMenuDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.mapper.RoleFunctionMapper;
import com.zenith.admin.mapper.RoleMenuMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RoleFunctionMapper roleFunctionMapper;

    @Override
    public List<String> getUserRoles(Long userId) {
        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        return userRoles.stream()
                .map(ur -> ur.getRoleId().toString())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return Collections.emptyList();
    }

    @Override
    public List<MenuDTO> getAccessibleMenus(Long userId) {
        return Collections.emptyList();
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        return false;
    }

    public List<Long> getRolesByUserId(Long userId) {
        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        return userRoles.stream()
                .map(UserRoleDO::getRoleId)
                .collect(Collectors.toList());
    }

    public List<Long> getMenusByRoleId(Long roleId) {
        List<RoleMenuDO> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenuDO>().eq(RoleMenuDO::getRoleId, roleId)
        );
        return roleMenus.stream()
                .map(RoleMenuDO::getMenuId)
                .collect(Collectors.toList());
    }

    public List<Long> getFunctionsByRoleId(Long roleId) {
        List<RoleFunctionDO> roleFunctions = roleFunctionMapper.selectList(
                new LambdaQueryWrapper<RoleFunctionDO>().eq(RoleFunctionDO::getRoleId, roleId)
        );
        return roleFunctions.stream()
                .map(RoleFunctionDO::getFunctionId)
                .collect(Collectors.toList());
    }

    public boolean hasPermission(Long userId, Long menuId) {
        List<Long> roleIds = getRolesByUserId(userId);
        if (roleIds.isEmpty()) {
            return false;
        }

        List<RoleMenuDO> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenuDO>()
                        .in(RoleMenuDO::getRoleId, roleIds)
                        .eq(RoleMenuDO::getMenuId, menuId)
        );

        return !roleMenus.isEmpty();
    }
}