package com.zenith.admin.service;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.OperLogConvertor;
import com.zenith.admin.dataobject.OperLogDO;
import com.zenith.admin.dto.data.OperLogDTO;
import com.zenith.admin.mapper.OperLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OperLogServiceImplTest {

    @Mock
    private OperLogMapper operLogMapper;

    @Mock
    private OperLogConvertor operLogConvertor;

    @InjectMocks
    private OperLogServiceImpl operLogService;

    private OperLogDO testOperLog;
    private OperLogDTO testOperLogDTO;

    @BeforeEach
    void setUp() {
        testOperLog = new OperLogDO();
        testOperLog.setId(1L);
        testOperLog.setOperator("admin");
        testOperLog.setModule("用户管理");
        testOperLog.setContent("新增用户");
        testOperLog.setResult("success");

        testOperLogDTO = new OperLogDTO();
        testOperLogDTO.setId(1L);
        testOperLogDTO.setOperator("admin");
        testOperLogDTO.setModule("用户管理");
        testOperLogDTO.setContent("新增用户");
        testOperLogDTO.setResult("success");
    }

    @Test
    @DisplayName("分页查询操作日志")
    void testListByPage_Success() {
        when(operLogMapper.selectList(any())).thenReturn(Arrays.asList(testOperLog));
        when(operLogConvertor.toDataObject(any(OperLogDTO.class))).thenReturn(testOperLog);
        when(operLogConvertor.toDTO(any(OperLogDO.class))).thenReturn(testOperLogDTO);
        when(operLogConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testOperLogDTO));

        PageInfo<OperLogDTO> result = operLogService.listByPage(1, 10, "admin", "用户管理", "success");

        assertNotNull(result);
        verify(operLogMapper).selectList(any());
    }

    @Test
    @DisplayName("保存操作日志")
    void testSave_Success() {
        when(operLogConvertor.toDataObject(any(OperLogDTO.class))).thenReturn(testOperLog);

        operLogService.save(testOperLogDTO);

        verify(operLogMapper).insert(any(OperLogDO.class));
    }

    @Test
    @DisplayName("删除操作日志")
    void testDelete_Success() {
        operLogService.delete(1L);

        verify(operLogMapper).deleteById(1L);
    }
}
