package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.dto.data.FunctionPageQuery;

import java.util.List;

public interface FunctionService {
    List<FunctionDTO> listByMenuId(Long menuId);
    PageInfo<FunctionDTO> page(FunctionPageQuery query);
    void save(FunctionDTO functionDTO);
    void update(FunctionDTO functionDTO);
    void delete(Long id);
    FunctionDTO getById(Long id);
}
