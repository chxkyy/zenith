package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.LoginLogDTO;

public interface LoginLogService {
    void delete(Long id);

    PageInfo<LoginLogDTO> listByPage(int pageIndex, int pageSize, String username, String status, String ip);

    void save(LoginLogDTO loginLogDTO);

    void updateLogoutAt(String username);
}
