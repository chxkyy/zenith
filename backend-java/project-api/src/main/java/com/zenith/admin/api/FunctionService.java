package com.zenith.admin.api;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.dto.data.FunctionPageQuery;

public interface FunctionService {
    MultiResponse<FunctionDTO> listByMenuId(Long menuId);
    PageInfo<FunctionDTO> page(FunctionPageQuery query);
    void save(FunctionDTO functionDTO);
    void update(FunctionDTO functionDTO);
    void delete(Long id);
    FunctionDTO getById(Long id);
}
