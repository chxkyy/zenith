package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.system.cmd.UserAddCmd;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSaveCmdExe {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;

    public void execute(UserAddCmd cmd) {
        UserDO userDO = new UserDO();
        userDO.setLoginId(cmd.getLoginId());
        userDO.setUsername(cmd.getUsername());
        userDO.setEmail(cmd.getEmail());
        userDO.setOrgId(cmd.getOrgId());
        userDO.setStatus(cmd.getStatus());
        userMapper.insert(userDO);

        if (cmd.getRoles() != null && !cmd.getRoles().isEmpty()) {
            saveUserRoles(userDO.getId(), cmd.getRoles());
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
