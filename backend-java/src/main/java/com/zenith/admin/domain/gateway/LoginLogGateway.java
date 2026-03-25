package com.zenith.admin.domain.gateway;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.model.LoginLogEntity;
import java.util.List;

public interface LoginLogGateway {
    PageInfo<LoginLogEntity> listByPage(int pageIndex, int pageSize, String username, String status, String ip);
    void save(LoginLogEntity log);
    void deleteById(Long id);
}
