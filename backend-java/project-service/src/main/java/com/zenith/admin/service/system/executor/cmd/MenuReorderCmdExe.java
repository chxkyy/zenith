package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dto.cmd.MenuReorderCmd;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuReorderCmdExe {

    private static final int SORT_STEP = 10;

    private final MenuMapper menuMapper;

    public void execute(MenuReorderCmd cmd) {
        Long id = cmd.getId();
        Integer targetIndex = cmd.getTargetIndex();

        MenuDO currentMenu = menuMapper.selectById(id);
        if (currentMenu == null) {
            throw new BizException("MENU_NOT_EXIST", "菜单不存在");
        }

        Long parentId = currentMenu.getParentId();

        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MenuDO::getParentId, parentId);
        queryWrapper.orderByAsc(MenuDO::getSort).orderByAsc(MenuDO::getId);
        List<MenuDO> siblings = menuMapper.selectList(queryWrapper);

        MenuDO movedMenu = null;
        for (MenuDO m : siblings) {
            if (m.getId().equals(id)) {
                movedMenu = m;
                break;
            }
        }

        if (movedMenu != null) {
            siblings.remove(movedMenu);
        } else {
            return;
        }

        int safeIndex = Math.max(0, Math.min(targetIndex, siblings.size()));
        siblings.add(safeIndex, movedMenu);

        int sortValue = SORT_STEP;
        LocalDateTime now = LocalDateTime.now();
        for (MenuDO menu : siblings) {
            menu.setSort(sortValue);
            menuMapper.updateById(menu);
            sortValue += SORT_STEP;
        }
    }
}
