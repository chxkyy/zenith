package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenith.admin.RoleConvertor;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleListAllQryExe {

    private final RoleMapper roleMapper;
    private final RoleConvertor roleConvertor;

    public List<RoleDTO> execute() {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        List<RoleDO> roleDOS = roleMapper.selectList(wrapper);
        return roleConvertor.toDTOList(roleDOS);
    }
}
