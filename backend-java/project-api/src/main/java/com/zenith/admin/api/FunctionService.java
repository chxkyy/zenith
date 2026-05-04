package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.FunctionAddCmd;
import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.dto.data.FunctionPageQuery;
import com.zenith.admin.dto.data.FunctionUpdateCmd;

import java.util.List;

public interface FunctionService {
    List<FunctionDTO> listByMenuId(Long menuId);
    PageInfo<FunctionDTO> page(FunctionPageQuery query);
    void save(FunctionAddCmd cmd, Long currentUserId);
    void update(FunctionUpdateCmd cmd, Long currentUserId);
    void delete(Long id, Long currentUserId);
    FunctionDTO getById(Long id);
}
