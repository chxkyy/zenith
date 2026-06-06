package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.MenuService;
import com.zenith.admin.dto.data.MenuAddCmd;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;
import com.zenith.admin.dto.data.MenuUpdateCmd;
import com.zenith.admin.dto.data.MenuToggleStatusCmd;
import com.zenith.admin.dto.data.MenuUpdateParentCmd;
import com.zenith.admin.dto.data.MenuReorderCmd;
import com.zenith.admin.service.system.executor.cmd.MenuDeleteCmdExe;
import com.zenith.admin.service.system.executor.cmd.MenuReorderCmdExe;
import com.zenith.admin.service.system.executor.cmd.MenuSaveCmdExe;
import com.zenith.admin.service.system.executor.cmd.MenuToggleStatusCmdExe;
import com.zenith.admin.service.system.executor.cmd.MenuUpdateCmdExe;
import com.zenith.admin.service.system.executor.cmd.MenuUpdateParentCmdExe;
import com.zenith.admin.service.system.executor.qry.MenuGetByIdQryExe;
import com.zenith.admin.service.system.executor.qry.MenuListAllQryExe;
import com.zenith.admin.service.system.executor.qry.MenuPageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuListAllQryExe menuListAllQryExe;
    private final MenuPageQryExe menuPageQryExe;
    private final MenuGetByIdQryExe menuGetByIdQryExe;
    private final MenuSaveCmdExe menuSaveCmdExe;
    private final MenuUpdateCmdExe menuUpdateCmdExe;
    private final MenuDeleteCmdExe menuDeleteCmdExe;
    private final MenuUpdateParentCmdExe menuUpdateParentCmdExe;
    private final MenuReorderCmdExe menuReorderCmdExe;
    private final MenuToggleStatusCmdExe menuToggleStatusCmdExe;

    @Override
    public List<MenuDTO> listAll() {
        return menuListAllQryExe.execute();
    }

    @Override
    public PageInfo<MenuDTO> page(MenuPageQuery query) {
        return menuPageQryExe.execute(query);
    }

    @Override
    public void save(MenuAddCmd cmd, Long currentUserId) {
        menuSaveCmdExe.execute(cmd);
    }

    @Override
    public void update(MenuUpdateCmd cmd, Long currentUserId) {
        menuUpdateCmdExe.execute(cmd);
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        menuDeleteCmdExe.execute(id);
    }

    @Override
    public MenuDTO getById(Long id) {
        return menuGetByIdQryExe.execute(id);
    }

    @Override
    public void updateParent(MenuUpdateParentCmd cmd, Long currentUserId) {
        menuUpdateParentCmdExe.execute(cmd);
    }

    @Override
    public void reorder(MenuReorderCmd cmd, Long currentUserId) {
        menuReorderCmdExe.execute(cmd);
    }

    @Override
    public void toggleStatus(MenuToggleStatusCmd cmd, Long currentUserId) {
        menuToggleStatusCmdExe.execute(cmd);
    }
}
