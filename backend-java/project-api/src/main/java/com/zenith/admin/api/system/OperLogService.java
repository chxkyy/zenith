package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.system.data.OperLogDTO;

import java.util.List;

public interface OperLogService {
    PageInfo<OperLogDTO> listByPage(int pageIndex, int pageSize, String operator, String module, String result);
    List<OperLogDTO> listAll(String operator, String module, String result);
    void delete(Long id);
    void save(OperLogDTO operLogDTO);
}
