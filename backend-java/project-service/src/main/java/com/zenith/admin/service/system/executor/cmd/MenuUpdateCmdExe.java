package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dto.cmd.MenuUpdateCmd;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuUpdateCmdExe {

    private final MenuMapper menuMapper;

    public void execute(MenuUpdateCmd cmd) {
        if (cmd.getParentId() != null && cmd.getParentId() > 0) {
            int parentDepth = getDepth(cmd.getParentId());
            if (parentDepth >= 3) {
                throw new BizException("MAX_DEPTH_EXCEEDED",
                    String.format("父菜单已处于第%d层，无法在其下创建子菜单（最大允许3层）", parentDepth));
            }
        }

        MenuDO menuDO = new MenuDO();
        menuDO.setId(cmd.getId());
        menuDO.setName(cmd.getName());
        menuDO.setPath(cmd.getPath());
        menuDO.setComponent(cmd.getComponent());
        menuDO.setType(cmd.getType());
        menuDO.setParentId(cmd.getParentId());
        menuDO.setSort(cmd.getSort());
        menuDO.setIcon(cmd.getIcon());
        menuDO.setStatus(cmd.getStatus());
        menuDO.setPermission(cmd.getPermission());
        menuMapper.updateById(menuDO);
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
