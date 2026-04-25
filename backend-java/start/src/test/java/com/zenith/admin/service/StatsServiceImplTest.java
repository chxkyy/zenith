package com.zenith.admin.service;

import com.zenith.admin.dto.data.StatsOverviewDTO;
import com.zenith.admin.mapper.ErrorLogMapper;
import com.zenith.admin.mapper.LoginLogMapper;
import com.zenith.admin.mapper.OperLogMapper;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StatsServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private LoginLogMapper loginLogMapper;

    @Mock
    private OperLogMapper operLogMapper;

    @Mock
    private ErrorLogMapper errorLogMapper;

    @InjectMocks
    private StatsServiceImpl statsService;

    @Test
    @DisplayName("获取系统概览统计")
    void testGetOverview_Success() {
        when(userMapper.selectCount(any())).thenReturn(100L);
        when(roleMapper.selectCount(any())).thenReturn(5L);
        when(loginLogMapper.selectCount(any())).thenReturn(50L);
        when(operLogMapper.selectCount(any())).thenReturn(10L);
        when(errorLogMapper.selectCount(any())).thenReturn(3L);

        StatsOverviewDTO result = statsService.getOverview();

        assertNotNull(result);
    }
}
