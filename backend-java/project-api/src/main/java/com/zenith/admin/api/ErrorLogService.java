package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.ErrorLogDTO;

public interface ErrorLogService {
    PageInfo<ErrorLogDTO> listByPage(int pageIndex, int pageSize, String module, String ip);
    void delete(Long id);
    void clear(int months);
    void save(ErrorLogDTO errorLogDTO);
}
