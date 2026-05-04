package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.MenuAddCmd;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;
import com.zenith.admin.dto.data.MenuUpdateCmd;
import com.zenith.admin.dto.data.MenuUpdateParentCmd;
import com.zenith.admin.dto.data.MenuReorderCmd;

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
}
