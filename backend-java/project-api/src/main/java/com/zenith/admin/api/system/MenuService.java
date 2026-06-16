package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.system.cmd.MenuAddCmd;
import com.zenith.admin.dto.system.data.MenuDTO;
import com.zenith.admin.dto.system.qry.MenuPageQuery;
import com.zenith.admin.dto.system.cmd.MenuUpdateCmd;
import com.zenith.admin.dto.system.cmd.MenuToggleStatusCmd;
import com.zenith.admin.dto.system.cmd.MenuUpdateParentCmd;
import com.zenith.admin.dto.system.cmd.MenuReorderCmd;

import java.util.List;

public interface MenuService {
    List<MenuDTO> listAll();
    PageInfo<MenuDTO> page(MenuPageQuery query);
    void save(MenuAddCmd cmd, Long currentUserId);
    void update(MenuUpdateCmd cmd, Long currentUserId);
    void delete(Long id, Long currentUserId);
    MenuDTO getById(Long id);
    void updateParent(MenuUpdateParentCmd cmd, Long currentUserId);
    void reorder(MenuReorderCmd cmd, Long currentUserId);
    void toggleStatus(MenuToggleStatusCmd cmd, Long currentUserId);
}
