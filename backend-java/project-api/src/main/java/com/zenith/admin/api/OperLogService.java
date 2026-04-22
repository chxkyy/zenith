package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.OperLogDTO;

public interface OperLogService {
    PageInfo<OperLogDTO> listByPage(int pageIndex, int pageSize, String operator, String module, String result);
    void delete(Long id);
    void save(OperLogDTO operLogDTO);
}
