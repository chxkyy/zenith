package com.zenith.admin.api.system;

import com.zenith.admin.dto.system.data.StatsOverviewDTO;

public interface StatsService {
    StatsOverviewDTO getOverview();
}
