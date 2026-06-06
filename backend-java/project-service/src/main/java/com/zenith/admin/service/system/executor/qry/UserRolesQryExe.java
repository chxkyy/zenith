package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 共享的用户角色查询执行器
 * 统一封装 userId -> userRoles -> roles 的查询链路，
 * 被 PermissionService、DataPermissionService 等多个 Service 复用
 */
@Component
@RequiredArgsConstructor
public class UserRolesQryExe {

    private static final Long SUPER_ADMIN_ROLE_ID = 1L;

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    /**
     * 查询用户的角色ID列表
     */
    public List<Long> getRoleIds(Long userId) {
        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        return userRoles.stream()
                .map(UserRoleDO::getRoleId)
                .toList();
    }

    /**
     * 查询用户关联的角色DO列表
     */
    public List<RoleDO> getRoles(Long userId) {
        List<Long> roleIds = getRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return roleMapper.selectList(
                new LambdaQueryWrapper<RoleDO>().in(RoleDO::getId, roleIds)
        );
    }

    /**
     * 判断用户是否为超级管理员
     */
    public boolean isSuperAdmin(List<RoleDO> roles) {
        return roles.stream()
                .anyMatch(role -> SUPER_ADMIN_ROLE_ID.equals(role.getId()));
    }

    /**
     * 判断用户是否为超级管理员（便捷方法，内部查询角色）
     */
    public boolean isSuperAdmin(Long userId) {
        return isSuperAdmin(getRoles(userId));
    }
}
