package com.zenith.admin.service;

import com.zenith.admin.FunctionConvertor;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.mapper.FunctionMapper;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FunctionServiceImplTest {

    @Mock
    private FunctionMapper functionMapper;

    @Mock
    private FunctionConvertor functionConvertor;

    @InjectMocks
    private FunctionServiceImpl functionService;

    private FunctionDO testFunctionDO;
    private FunctionDTO testFunctionDTO;

    @BeforeEach
    void setUp() {
        testFunctionDO = new FunctionDO();
        testFunctionDO.setId(1L);
        testFunctionDO.setMenuId(10L);
        testFunctionDO.setName("用户新增");
        testFunctionDO.setType("button");
        testFunctionDO.setPermission("user:add");
        testFunctionDO.setSort(1);
        testFunctionDO.setStatus(1);

        testFunctionDTO = new FunctionDTO();
        testFunctionDTO.setId(1L);
        testFunctionDTO.setMenuId(10L);
        testFunctionDTO.setName("用户新增");
        testFunctionDTO.setType("button");
        testFunctionDTO.setPermission("user:add");
        testFunctionDTO.setSort(1);
        testFunctionDTO.setStatus(1);
    }

    @Test
    @DisplayName("获取所有功能列表")
    void testListAll_Success() {
        when(functionMapper.selectList(any())).thenReturn(Arrays.asList(testFunctionDO));
        when(functionConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testFunctionDTO));

        List<FunctionDTO> result = functionService.listAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("用户新增", result.get(0).getName());
        verify(functionMapper).selectList(any());
    }

    @Test
    @DisplayName("获取所有功能列表-无数据")
    void testListAll_Empty() {
        when(functionMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(functionConvertor.toDTOList(anyList())).thenReturn(Collections.emptyList());

        List<FunctionDTO> result = functionService.listAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("根据菜单ID获取功能列表")
    void testListByMenuId_Success() {
        when(functionMapper.selectList(any())).thenReturn(Arrays.asList(testFunctionDO));
        when(functionConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testFunctionDTO));

        List<FunctionDTO> result = functionService.listByMenuId(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getMenuId());
        verify(functionMapper).selectList(any());
    }

    @Test
    @DisplayName("根据ID获取功能")
    void testGetById_Success() {
        when(functionMapper.selectById(1L)).thenReturn(testFunctionDO);
        when(functionConvertor.toDTO(any(FunctionDO.class))).thenReturn(testFunctionDTO);

        FunctionDTO result = functionService.getById(1L);

        assertNotNull(result);
        assertEquals("用户新增", result.getName());
    }

    @Test
    @DisplayName("删除功能")
    void testDelete_Success() {
        functionService.delete(1L, 100L);

        verify(functionMapper).deleteById(1L);
    }
}
