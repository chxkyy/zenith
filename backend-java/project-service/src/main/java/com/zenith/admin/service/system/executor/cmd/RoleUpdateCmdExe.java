package com.zenith.admin.service.system.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dto.data.RoleUpdateCmd;
import com.zenith.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleUpdateCmdExe {

    private final RoleMapper roleMapper;

    public void execute(RoleUpdateCmd cmd, Long currentUserId) {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("name", cmd.getName());
        wrapper.ne("id", cmd.getId());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BizException("ROLE_UPDATE_001", "角色名称已存在");
        }

        RoleDO roleDO = new RoleDO();
        roleDO.setId(cmd.getId());
        roleDO.setName(cmd.getName());
        roleDO.setStatus(cmd.getStatus());
        roleDO.setDescription(cmd.getDescription());
        roleDO.setUpdateUserId(currentUserId);
        roleMapper.updateById(roleDO);
    }
}
