package com.zenith.admin.executor;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleChangeStatusCmdExe {

    private static final Long SUPER_ADMIN_ROLE_ID = 1L;

    private final RoleMapper roleMapper;

    public void execute(Long id, Integer status, Long currentUserId) {
        if (SUPER_ADMIN_ROLE_ID.equals(id) && status == 0) {
            throw new BizException("ROLE_STATUS_001", "超级管理员角色不可禁用");
        }
        RoleDO role = roleMapper.selectById(id);
        if (role != null) {
            role.setStatus(status);
            role.setUpdateUserId(currentUserId);
            roleMapper.updateById(role);
        }
    }
}
