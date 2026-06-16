package com.zenith.admin.dto.system.data;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StatsOverviewDTO {

    private Long totalUsers;

    private Long totalRoles;

    private Long operLogs;

    private Long errorLogs;

    private List<Map<String, Object>> chartData;
}
