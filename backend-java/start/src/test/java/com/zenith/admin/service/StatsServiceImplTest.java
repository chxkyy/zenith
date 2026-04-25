package com.zenith.admin.service;

import com.zenith.admin.api.StatsService;
import com.zenith.admin.dto.data.StatsOverviewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest implements StatsService {

    @InjectMocks
    private StatsServiceImpl statsService;

    @Test
    @DisplayName("获取系统概览统计")
    void testGetOverview_Success() {
        StatsOverviewDTO result = statsService.getOverview();

        assertNotNull(result);
    }
}
