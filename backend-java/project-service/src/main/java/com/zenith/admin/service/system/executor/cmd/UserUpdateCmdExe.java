package com.zenith.admin.service.system.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.system.cmd.UserUpdateCmd;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserUpdateCmdExe {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    public void execute(UserUpdateCmd cmd) {
        UserDO userDO = userMapper.selectById(cmd.getId());
        if (userDO != null) {
            if (cmd.getLoginId() != null && !cmd.getLoginId().isEmpty()) {
                userDO.setLoginId(cmd.getLoginId());
            }
            userDO.setUsername(cmd.getUsername());
            userDO.setEmail(cmd.getEmail());
            userDO.setOrgId(cmd.getOrgId());
            userDO.setStatus(cmd.getStatus());
            userMapper.updateById(userDO);

            if (cmd.getRoles() != null) {
                updateUserRoles(userDO.getId(), cmd.getRoles());
            }
        }
    }

    private void updateUserRoles(Long userId, List<String> roleIds) {
        LambdaQueryWrapper<UserRoleDO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserRoleDO::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            saveUserRoles(userId, roleIds);
        }
    }

    private void saveUserRoles(Long userId, List<String> roleIds) {
        for (String roleIdStr : roleIds) {
            try {
                Long roleId = Long.parseLong(roleIdStr);
                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setUserId(userId);
                userRoleDO.setRoleId(roleId);
                userRoleMapper.insert(userRoleDO);
            } catch (NumberFormatException e) {
            }
        }
    }
}
