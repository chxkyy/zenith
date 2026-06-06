package com.zenith.admin.service.system.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dto.cmd.RoleAddCmd;
import com.zenith.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSaveCmdExe {

    private final RoleMapper roleMapper;

    public void execute(RoleAddCmd cmd, Long currentUserId) {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("name", cmd.getName());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BizException("ROLE_SAVE_001", "角色名称已存在");
        }

        RoleDO roleDO = new RoleDO();
        roleDO.setName(cmd.getName());
        roleDO.setStatus(cmd.getStatus());
        roleDO.setDescription(cmd.getDescription());
        roleDO.setCreateUserId(currentUserId);
        roleDO.setUpdateUserId(currentUserId);
        roleMapper.insert(roleDO);
    }
}
