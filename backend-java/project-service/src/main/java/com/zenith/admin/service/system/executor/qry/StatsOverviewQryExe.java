package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.dto.data.StatsOverviewDTO;
import com.zenith.admin.mapper.ErrorLogMapper;
import com.zenith.admin.mapper.OperLogMapper;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 统计概览查询执行器
 * 聚合各实体的计数数据，供 StatsService 调用
 */
@Component
@RequiredArgsConstructor
public class StatsOverviewQryExe {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final OperLogMapper operLogMapper;
    private final ErrorLogMapper errorLogMapper;

    public StatsOverviewDTO execute() {
        StatsOverviewDTO dto = new StatsOverviewDTO();
        dto.setTotalUsers(userMapper.selectCount(null));
        dto.setTotalRoles(roleMapper.selectCount(null));
        dto.setOperLogs(operLogMapper.selectCount(null));
        dto.setErrorLogs(errorLogMapper.selectCount(null));
        dto.setChartData(Collections.emptyList());
        return dto;
    }
}
