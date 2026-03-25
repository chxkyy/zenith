package com.zenith.admin.domain.gateway;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.model.ErrorLogEntity;
import java.util.List;

public interface ErrorLogGateway {
    PageInfo<ErrorLogEntity> listByPage(int pageIndex, int pageSize, String module, String ip);
    void save(ErrorLogEntity log);
    void deleteById(Long id);
    void clearLogs(int months);
}
