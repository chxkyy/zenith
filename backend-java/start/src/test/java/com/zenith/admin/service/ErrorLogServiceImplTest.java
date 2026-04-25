package com.zenith.admin.service;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.ErrorLogService;
import com.zenith.admin.ErrorLogConvertor;
import com.zenith.admin.dataobject.ErrorLogDO;
import com.zenith.admin.dto.data.ErrorLogDTO;
import com.zenith.admin.mapper.ErrorLogMapper;
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
class ErrorLogServiceImplTest implements ErrorLogService {

    @Mock
    private ErrorLogMapper errorLogMapper;

    @Mock
    private ErrorLogConvertor errorLogConvertor;

    @InjectMocks
    private ErrorLogServiceImpl errorLogService;

    private ErrorLogDO testErrorLog;
    private ErrorLogDTO testErrorLogDTO;

    @BeforeEach
    void setUp() {
        testErrorLog = new ErrorLogDO();
        testErrorLog.setId(1L);
        testErrorLog.setModule("用户管理");
        testErrorLog.setMessage("NullPointerException");
        testErrorLog.setIp("127.0.0.1");

        testErrorLogDTO = new ErrorLogDTO();
        testErrorLogDTO.setId(1L);
        testErrorLogDTO.setModule("用户管理");
        testErrorLogDTO.setMessage("NullPointerException");
        testErrorLogDTO.setIp("127.0.0.1");
    }

    @Test
    @DisplayName("分页查询错误日志")
    void testListByPage_Success() {
        when(errorLogMapper.selectList(any())).thenReturn(Arrays.asList(testErrorLog));
        when(errorLogConvertor.toDataObject(any(ErrorLogDTO.class))).thenReturn(testErrorLog);
        when(errorLogConvertor.toDTO(any(ErrorLogDO.class))).thenReturn(testErrorLogDTO);
        when(errorLogConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testErrorLogDTO));

        PageInfo<ErrorLogDTO> result = listByPage(1, 10, "用户管理", "127.0.0.1");

        assertNotNull(result);
        verify(errorLogMapper).selectList(any());
    }

    @Test
    @DisplayName("保存错误日志")
    void testSave_Success() {
        when(errorLogConvertor.toDataObject(any(ErrorLogDTO.class))).thenReturn(testErrorLog);

        save(testErrorLogDTO);

        verify(errorLogMapper).insert(any(ErrorLogDO.class));
    }

    @Test
    @DisplayName("删除错误日志")
    void testDelete_Success() {
        delete(1L);

        verify(errorLogMapper).deleteById(1L);
    }

    @Test
    @DisplayName("清理指定月份前的错误日志")
    void testClear_Success() {
        clear(3);

        verify(errorLogMapper).delete(any());
    }
}
