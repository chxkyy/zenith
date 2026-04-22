package com.zenith.admin.api;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.LoginLogDTO;

public interface LoginLogService {
    PageInfo<LoginLogDTO> listByPage(int pageIndex, int pageSize, String username, String status, String ip);
    void delete(Long id);
    void save(LoginLogDTO loginLogDTO);
}
