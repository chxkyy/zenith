package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dto.cmd.MenuUpdateParentCmd;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuUpdateParentCmdExe {

    private static final int SORT_STEP = 10;

    private final MenuMapper menuMapper;

    public void execute(MenuUpdateParentCmd cmd) {
        Long id = cmd.getId();
        Long newParentId = cmd.getNewParentId();

        MenuDO currentMenu = menuMapper.selectById(id);
        if (currentMenu == null) {
            throw new BizException("MENU_NOT_EXIST", "菜单不存在");
        }

        if (newParentId != null) {
            MenuDO parentMenu = menuMapper.selectById(newParentId);
            if (parentMenu == null) {
                throw new BizException("PARENT_MENU_NOT_EXIST", "目标父菜单不存在");
            }
            if (isDescendant(id, newParentId)) {
                throw new BizException("MENU_CYCLE_REFERENCE", "不能将菜单移动到自己的子菜单下");
            }

            int targetDepth = getDepth(newParentId);
            int maxChildDepth = getMaxChildDepth(id);
            if (targetDepth + 1 + maxChildDepth > 3) {
                throw new BizException("MAX_DEPTH_EXCEEDED",
                    String.format("移动后菜单层级将超过3层（目标层级：%d，子树最大深度：%d），无法完成此操作",
                        targetDepth, maxChildDepth));
            }
        }

        currentMenu.setParentId(newParentId);
        menuMapper.updateById(currentMenu);

        reorderSiblings(newParentId);
    }

    private boolean isDescendant(Long ancestorId, Long descendantId) {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuDO::getParentId, descendantId);
        List<MenuDO> children = menuMapper.selectList(queryWrapper);

        for (MenuDO child : children) {
            if (child.getId().equals(ancestorId)) {
                return true;
            }
            if (isDescendant(ancestorId, child.getId())) {
                return true;
            }
        }
        return false;
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

    private int getMaxChildDepth(Long menuId) {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuDO::getParentId, menuId);
        List<MenuDO> children = menuMapper.selectList(queryWrapper);

        if (children.isEmpty()) {
            return 0;
        }

        int maxChildDepth = 0;
        for (MenuDO child : children) {
            int childDepth = 1 + getMaxChildDepth(child.getId());
            maxChildDepth = Math.max(maxChildDepth, childDepth);
        }
        return maxChildDepth;
    }

    private void reorderSiblings(Long parentId) {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        if (parentId != null) {
            queryWrapper.eq(MenuDO::getParentId, parentId);
        } else {
            queryWrapper.isNull(MenuDO::getParentId);
        }
        queryWrapper.orderByAsc(MenuDO::getSort).orderByAsc(MenuDO::getId);
        List<MenuDO> siblings = menuMapper.selectList(queryWrapper);

        int sortValue = SORT_STEP;
        for (MenuDO menu : siblings) {
            menu.setSort(sortValue);
            menuMapper.updateById(menu);
            sortValue += SORT_STEP;
        }
    }
}
