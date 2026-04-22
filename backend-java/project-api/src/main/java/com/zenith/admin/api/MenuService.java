package com.zenith.admin.api;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;

public interface MenuService {
    MultiResponse<MenuDTO> listAll();
    PageInfo<MenuDTO> page(MenuPageQuery query);
    void save(MenuDTO menuDTO);
    void update(MenuDTO menuDTO);
    void delete(Long id);
    MenuDTO getById(Long id);
}
