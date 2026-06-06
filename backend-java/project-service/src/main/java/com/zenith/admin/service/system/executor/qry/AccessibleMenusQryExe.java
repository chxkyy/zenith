package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dataobject.RoleMenuDO;
import com.zenith.admin.mapper.MenuMapper;
import com.zenith.admin.mapper.RoleMenuMapper;
import com.zenith.admin.service.system.executor.converter.MenuConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 可访问菜单查询执行器
 * 根据用户角色计算可访问的菜单列表（含父级菜单自动展开）
 */
@Component
@RequiredArgsConstructor
public class AccessibleMenusQryExe {

    private final UserRolesQryExe userRolesQryExe;
    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final MenuConvertor menuConvertor;

    public List<MenuDTO> execute(Long userId) {
        List<Long> roleIds = userRolesQryExe.getRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<MenuDO> allMenus;
        if (userRolesQryExe.isSuperAdmin(userId)) {
            // 超级管理员可见所有菜单
            allMenus = menuMapper.selectList(
                    new LambdaQueryWrapper<MenuDO>().orderByAsc(MenuDO::getSort)
            );
            return menuConvertor.toDTOList(allMenus);
        }

        // 普通用户：只看启用的菜单
        allMenus = menuMapper.selectList(
                new LambdaQueryWrapper<MenuDO>()
                        .eq(MenuDO::getStatus, 1)
                        .orderByAsc(MenuDO::getSort)
        );

        // 查询角色关联的菜单ID
        List<RoleMenuDO> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenuDO>().in(RoleMenuDO::getRoleId, roleIds)
        );

        Set<Long> accessibleMenuIds = roleMenus.stream()
                .map(RoleMenuDO::getMenuId)
                .collect(Collectors.toSet());

        // 向上展开父级菜单
        for (MenuDO menu : allMenus) {
            if (accessibleMenuIds.contains(menu.getId())) {
                addParentMenuIds(allMenus, menu.getParentId(), accessibleMenuIds);
            }
        }

        List<MenuDO> accessibleMenus = allMenus.stream()
                .filter(menu -> accessibleMenuIds.contains(menu.getId()))
                .toList();

        return menuConvertor.toDTOList(accessibleMenus);
    }

    private void addParentMenuIds(List<MenuDO> allMenus, Long parentId, Set<Long> accessibleMenuIds) {
        if (parentId == null || parentId == 0L || accessibleMenuIds.contains(parentId)) {
            return;
        }
        accessibleMenuIds.add(parentId);
        for (MenuDO menu : allMenus) {
            if (menu.getId().equals(parentId)) {
                addParentMenuIds(allMenus, menu.getParentId(), accessibleMenuIds);
                break;
            }
        }
    }
}
