package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.RoleFunctionDO;
import com.zenith.admin.mapper.RoleFunctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色权限ID列表查询执行器
 */
@Component
@RequiredArgsConstructor
public class RolePermissionQryExe {

    private final RoleFunctionMapper roleFunctionMapper;

    public List<Long> execute(Long roleId) {
        List<RoleFunctionDO> roleFunctions = roleFunctionMapper.selectList(
                new LambdaQueryWrapper<RoleFunctionDO>().eq(RoleFunctionDO::getRoleId, roleId)
        );
        return roleFunctions.stream()
                .map(RoleFunctionDO::getFunctionId)
                .toList();
    }
}
