package com.zenith.admin.service.system.impl;

import com.zenith.admin.api.system.DataPermissionService;
import com.zenith.admin.service.system.executor.qry.DataScopeQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据权限服务实现
 * 纯编排层，委托给 DataScopeQryExe 执行数据范围计算
 */
@Service
@RequiredArgsConstructor
public class DataPermissionServiceImpl implements DataPermissionService {

    private final DataScopeQryExe dataScopeQryExe;

    @Override
    public DataScopeInfo getDataScope(Long userId) {
        return dataScopeQryExe.execute(userId);
    }

    @Override
    public List<Long> getAccessibleOrgIds(Long userId) {
        return dataScopeQryExe.getAccessibleOrgIds(userId);
    }

    @Override
    public boolean hasFullAccess(Long userId) {
        return dataScopeQryExe.hasFullAccess(userId);
    }
}
