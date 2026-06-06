package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.RoleMenuDO;
import com.zenith.admin.mapper.RoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色菜单ID列表查询执行器
 */
@Component
@RequiredArgsConstructor
public class RoleMenuQryExe {

    private final RoleMenuMapper roleMenuMapper;

    public List<Long> execute(Long roleId) {
        List<RoleMenuDO> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenuDO>().eq(RoleMenuDO::getRoleId, roleId)
        );
        return roleMenus.stream()
                .map(RoleMenuDO::getMenuId)
                .toList();
    }
}
