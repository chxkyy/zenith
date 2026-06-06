package com.zenith.admin.service.system.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户角色更新命令执行器
 * 处理用户角色分配（先删后插）
 * 注意：缓存失效由调用方（Service层）负责，避免Executor间循环依赖
 */
@Component
@RequiredArgsConstructor
public class UserRolesUpdateCmdExe {

    private final UserRoleMapper userRoleMapper;

    public void execute(Long userId, List<Long> roleIds) {
        // 删除旧关联
        LambdaQueryWrapper<UserRoleDO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserRoleDO::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);

        // 插入新关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRoleDO userRole = new UserRoleDO();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreatedTime(LocalDateTime.now());
                userRoleMapper.insert(userRole);
            }
        }
    }
}
