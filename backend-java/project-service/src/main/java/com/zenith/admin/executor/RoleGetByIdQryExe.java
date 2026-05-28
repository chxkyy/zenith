package com.zenith.admin.executor;

import com.zenith.admin.RoleConvertor;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleGetByIdQryExe {

    private final RoleMapper roleMapper;
    private final RoleConvertor roleConvertor;

    public RoleDTO execute(Long id) {
        RoleDO roleDO = roleMapper.selectById(id);
        return roleConvertor.toDTO(roleDO);
    }
}
