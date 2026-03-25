package com.zenith.admin.domain.gateway;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.model.OperLogEntity;
import java.util.List;

public interface OperLogGateway {
    PageInfo<OperLogEntity> listByPage(int pageIndex, int pageSize, String operator, String module, String result);
    void save(OperLogEntity log);
    void deleteById(Long id);
}
