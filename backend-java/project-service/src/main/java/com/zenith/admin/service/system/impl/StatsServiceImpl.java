package com.zenith.admin.service.system.impl;

import com.zenith.admin.api.StatsService;
import com.zenith.admin.dto.data.StatsOverviewDTO;
import com.zenith.admin.mapper.ErrorLogMapper;
import com.zenith.admin.mapper.OperLogMapper;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final OperLogMapper operLogMapper;
    private final ErrorLogMapper errorLogMapper;

    @Override
    public StatsOverviewDTO getOverview() {
        StatsOverviewDTO dto = new StatsOverviewDTO();
        
        dto.setTotalUsers(userMapper.selectCount(null));
        dto.setTotalRoles(roleMapper.selectCount(null));
        dto.setOperLogs(operLogMapper.selectCount(null));
        dto.setErrorLogs(errorLogMapper.selectCount(null));
        dto.setChartData(Collections.emptyList());
        
        return dto;
    }
}
