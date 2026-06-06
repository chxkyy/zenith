package com.zenith.admin.service.system.impl;

import com.zenith.admin.api.StatsService;
import com.zenith.admin.dto.data.StatsOverviewDTO;
import com.zenith.admin.service.system.executor.qry.StatsOverviewQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 统计服务实现
 * 纯编排层，委托给 StatsOverviewQryExe 执行具体逻辑
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsOverviewQryExe statsOverviewQryExe;

    @Override
    public StatsOverviewDTO getOverview() {
        return statsOverviewQryExe.execute();
    }
}
