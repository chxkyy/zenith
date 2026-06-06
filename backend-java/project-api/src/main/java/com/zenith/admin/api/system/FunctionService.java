package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.cmd.FunctionAddCmd;
import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.dto.query.FunctionPageQuery;
import com.zenith.admin.dto.cmd.FunctionUpdateCmd;

import java.util.List;

public interface FunctionService {
    List<FunctionDTO> listByMenuId(Long menuId);
    List<FunctionDTO> listAll();
    PageInfo<FunctionDTO> page(FunctionPageQuery query);
    void save(FunctionAddCmd cmd, Long currentUserId);
    void update(FunctionUpdateCmd cmd, Long currentUserId);
    void delete(Long id, Long currentUserId);
    FunctionDTO getById(Long id);
}
