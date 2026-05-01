package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;
import com.zenith.admin.dto.data.MenuUpdateParentCmd;
import com.zenith.admin.dto.data.MenuReorderCmd;

import java.util.List;

public interface MenuService {
    List<MenuDTO> listAll();
    PageInfo<MenuDTO> page(MenuPageQuery query);
    void save(MenuDTO menuDTO);
    void update(MenuDTO menuDTO);
    void delete(Long id);
    MenuDTO getById(Long id);
    void updateParent(MenuUpdateParentCmd cmd);
    void reorder(MenuReorderCmd cmd);
}
