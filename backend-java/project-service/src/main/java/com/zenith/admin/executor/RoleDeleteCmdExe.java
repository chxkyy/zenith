package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleDeleteCmdExe {

    private static final Long SUPER_ADMIN_ROLE_ID = 1L;

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public void execute(Long id) {
        if (SUPER_ADMIN_ROLE_ID.equals(id)) {
            throw new BizException("ROLE_DELETE_001", "超级管理员角色不可删除");
        }

        QueryWrapper<UserRoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", id);
        Long userCount = userRoleMapper.selectCount(wrapper);
        if (userCount > 0) {
            throw new BizException("ROLE_DELETE_002", "该角色已分配给 " + userCount + " 个用户，请先移除用户关联");
        }

        roleMapper.deleteById(id);
    }
}
