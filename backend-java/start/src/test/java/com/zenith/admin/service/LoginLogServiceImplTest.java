package com.zenith.admin.service;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.LoginLogService;
import com.zenith.admin.LoginLogConvertor;
import com.zenith.admin.dataobject.LoginLogDO;
import com.zenith.admin.dto.data.LoginLogDTO;
import com.zenith.admin.mapper.LoginLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginLogServiceImplTest implements LoginLogService {

    @Mock
    private LoginLogMapper loginLogMapper;

    @Mock
    private LoginLogConvertor loginLogConvertor;

    @InjectMocks
    private LoginLogServiceImpl loginLogService;

    private LoginLogDO testLoginLog;
    private LoginLogDTO testLoginLogDTO;

    @BeforeEach
    void setUp() {
        testLoginLog = new LoginLogDO();
        testLoginLog.setId(1L);
        testLoginLog.setUsername("admin");
        testLoginLog.setIp("127.0.0.1");
        testLoginLog.setStatus("success");

        testLoginLogDTO = new LoginLogDTO();
        testLoginLogDTO.setId(1L);
        testLoginLogDTO.setUsername("admin");
        testLoginLogDTO.setIp("127.0.0.1");
        testLoginLogDTO.setStatus("success");
    }

    @Test
    @DisplayName("分页查询登录日志")
    void testListByPage_Success() {
        when(loginLogMapper.selectList(any())).thenReturn(Arrays.asList(testLoginLog));
        when(loginLogConvertor.toDataObject(any(LoginLogDTO.class))).thenReturn(testLoginLog);
        when(loginLogConvertor.toDTO(any(LoginLogDO.class))).thenReturn(testLoginLogDTO);
        when(loginLogConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testLoginLogDTO));

        PageInfo<LoginLogDTO> result = listByPage(1, 10, "admin", "success", "127.0.0.1");

        assertNotNull(result);
        verify(loginLogMapper).selectList(any());
    }

    @Test
    @DisplayName("保存登录日志")
    void testSave_Success() {
        when(loginLogConvertor.toDataObject(any(LoginLogDTO.class))).thenReturn(testLoginLog);

        save(testLoginLogDTO);

        verify(loginLogMapper).insert(any(LoginLogDO.class));
    }

    @Test
    @DisplayName("删除登录日志")
    void testDelete_Success() {
        delete(1L);

        verify(loginLogMapper).deleteById(1L);
    }
}
