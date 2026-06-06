package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dto.cmd.MenuAddCmd;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuSaveCmdExe {

    private final MenuMapper menuMapper;

    public void execute(MenuAddCmd cmd) {
        if (cmd.getParentId() != null && cmd.getParentId() > 0) {
            int parentDepth = getDepth(cmd.getParentId());
            if (parentDepth >= 3) {
                throw new BizException("MAX_DEPTH_EXCEEDED",
                    String.format("父菜单已处于第%d层，无法在其下创建子菜单（最大允许3层）", parentDepth));
            }
        }

        MenuDO menuDO = new MenuDO();
        menuDO.setName(cmd.getName());
        menuDO.setPath(cmd.getPath());
        menuDO.setComponent(cmd.getComponent());
        menuDO.setType(cmd.getType());
        menuDO.setParentId(cmd.getParentId());
        menuDO.setSort(calculateNextSort(cmd.getParentId()));
        menuDO.setIcon(cmd.getIcon());
        menuDO.setStatus(cmd.getStatus());
        menuDO.setPermission(cmd.getPermission());
        menuMapper.insert(menuDO);
    }

    /**
     * 计算同级菜单的下一个排序号（当前最大值 + 1）
     */
    private int calculateNextSort(Long parentId) {
        LambdaQueryWrapper<MenuDO> wrapper = new LambdaQueryWrapper<MenuDO>()
                .eq(parentId != null && parentId > 0, MenuDO::getParentId, parentId)
                .isNull(parentId == null || parentId == 0, MenuDO::getParentId)
                .orderByDesc(MenuDO::getSort)
                .last("LIMIT 1");
        MenuDO lastMenu = menuMapper.selectOne(wrapper);
        return lastMenu == null ? 0 : lastMenu.getSort() + 1;
    }

    private int getDepth(Long menuId) {
        int depth = 1;
        Long currentId = menuId;
        while (currentId != null) {
            MenuDO menu = menuMapper.selectById(currentId);
            if (menu == null || menu.getParentId() == null || menu.getParentId() == 0L) {
                break;
            }
            currentId = menu.getParentId();
            depth++;
        }
        return depth;
    }
}
