package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dto.cmd.MenuToggleStatusCmd;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuToggleStatusCmdExe {

    private final MenuMapper menuMapper;

    public void execute(MenuToggleStatusCmd cmd) {
        Long id = cmd.getId();

        MenuDO menuDO = menuMapper.selectById(id);
        if (menuDO == null) {
            throw new BizException("MENU_NOT_EXIST", "菜单不存在");
        }

        Integer currentStatus = menuDO.getStatus();
        Integer newStatus = (currentStatus != null && currentStatus == 1) ? 0 : 1;

        menuDO.setStatus(newStatus);
        menuMapper.updateById(menuDO);
    }
}
