package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenith.admin.service.system.executor.converter.RoleConvertor;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleListActiveRolesQryExe {

    private final RoleMapper roleMapper;
    private final RoleConvertor roleConvertor;

    public List<RoleDTO> execute() {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("id");

        List<RoleDO> roleDOS = roleMapper.selectList(wrapper);
        return roleConvertor.toDTOList(roleDOS);
    }
}
