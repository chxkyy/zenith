package com.zenith.admin.service;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.DictConvertor;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.data.DictItemPageQuery;
import com.zenith.admin.dto.data.DictPageQuery;
import com.zenith.admin.mapper.DictItemMapper;
import com.zenith.admin.mapper.DictMapper;
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
class DictServiceImplTest {

    @Mock
    private DictConvertor dictConvertor;
    @Mock
    private DictItemMapper dictItemMapper;
    @Mock
    private DictMapper dictMapper;
    @InjectMocks
    private DictServiceImpl dictService;

    private DictDO testDict;
    private DictDTO testDictDTO;
    private DictItemDO testDictItem;
    private DictItemDTO testDictItemDTO;

    @BeforeEach
    void setUp() {
        testDict = new DictDO();
        testDict.setId(1L);
        testDict.setName("用户状态");
        testDict.setType("user_status");
        testDict.setStatus(1);
        testDict.setRemark("用户状态字典");

        testDictDTO = new DictDTO();
        testDictDTO.setId(1L);
        testDictDTO.setName("用户状态");
        testDictDTO.setType("user_status");
        testDictDTO.setStatus(1);
        testDictDTO.setRemark("用户状态字典");

        testDictItem = new DictItemDO();
        testDictItem.setId(1L);
        testDictItem.setType("user_status");
        testDictItem.setLabel("正常");
        testDictItem.setDictValue("1");
        testDictItem.setSort(1);

        testDictItemDTO = new DictItemDTO();
        testDictItemDTO.setId(1L);
        testDictItemDTO.setType("user_status");
        testDictItemDTO.setLabel("正常");
        testDictItemDTO.setDictValue("1");
        testDictItemDTO.setSort(1);
    }

    @Test
    @DisplayName("获取所有字典类型")
    void testListAll_Success() {
        when(dictMapper.selectList(null)).thenReturn(Arrays.asList(testDict));
        when(dictConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testDictDTO));

        MultiResponse<DictDTO> result = dictService.listAll();

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
    }

    @Test
    @DisplayName("根据类型查询字典")
    void testListByType_Success() {
        when(dictMapper.selectList(any())).thenReturn(Arrays.asList(testDict));
        when(dictConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testDictDTO));

        MultiResponse<DictDTO> result = dictService.listByType("user_status");

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("分页查询字典类型")
    void testPage_Success() {
        DictPageQuery query = new DictPageQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        when(dictMapper.selectList(any())).thenReturn(Arrays.asList(testDict));
        when(dictConvertor.toDataObject(any(DictDTO.class))).thenReturn(testDict);
        when(dictConvertor.toDTO(any(DictDO.class))).thenReturn(testDictDTO);
        when(dictConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testDictDTO));

        PageInfo<DictDTO> result = dictService.page(query);

        assertNotNull(result);
        verify(dictMapper).selectList(any());
    }

    @Test
    @DisplayName("保存字典类型")
    void testSave_Dict() {
        when(dictConvertor.toDataObject(any(DictDTO.class))).thenReturn(testDict);

        dictService.save(testDictDTO);

        verify(dictMapper).updateById(any(DictDO.class));
    }

    @Test
    @DisplayName("更新字典类型")
    void testUpdate_Dict() {
        when(dictConvertor.toDataObject(any(DictDTO.class))).thenReturn(testDict);

        dictService.update(testDictDTO);

        verify(dictMapper).updateById(any(DictDO.class));
    }

    @Test
    @DisplayName("根据ID获取字典类型")
    void testGetById_Success() {
        when(dictMapper.selectById(1L)).thenReturn(testDict);
        when(dictConvertor.toDTO(any(DictDO.class))).thenReturn(testDictDTO);

        DictDTO result = dictService.getById(1L);

        assertNotNull(result);
        assertEquals("user_status", result.getType());
    }

    @Test
    @DisplayName("删除字典类型")
    void testDelete_Success() {
        when(dictMapper.selectById(1L)).thenReturn(testDict);
        when(dictItemMapper.selectCount(any())).thenReturn(0L);

        dictService.delete(1L);

        verify(dictMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除有字典项的字典类型抛出异常")
    void testDelete_HasItems() {
        when(dictMapper.selectById(1L)).thenReturn(testDict);
        when(dictItemMapper.selectCount(any())).thenReturn(5L);

        assertThrows(com.alibaba.cola.exception.BizException.class, () -> dictService.delete(1L));
        verify(dictMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("根据类型查询字典项")
    void testListItemsByType_Success() {
        when(dictItemMapper.selectList(any())).thenReturn(Arrays.asList(testDictItem));
        when(dictConvertor.toItemDTOList(anyList())).thenReturn(Arrays.asList(testDictItemDTO));

        MultiResponse<DictItemDTO> result = dictService.listItemsByType("user_status");

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("分页查询字典项")
    void testPageItems_Success() {
        DictItemPageQuery query = new DictItemPageQuery();
        query.setPageIndex(1);
        query.setPageSize(10);
        query.setType("user_status");

        when(dictItemMapper.selectList(any())).thenReturn(Arrays.asList(testDictItem));
        when(dictConvertor.toItemDataObject(any(DictItemDTO.class))).thenReturn(testDictItem);
        when(dictConvertor.toItemDTO(any(DictItemDO.class))).thenReturn(testDictItemDTO);
        when(dictConvertor.toItemDTOList(anyList())).thenReturn(Arrays.asList(testDictItemDTO));

        PageInfo<DictItemDTO> result = dictService.pageItems(query);

        assertNotNull(result);
        verify(dictItemMapper).selectList(any());
    }

    @Test
    @DisplayName("保存字典项")
    void testSaveItem_DictItem() {
        when(dictConvertor.toItemDataObject(any(DictItemDTO.class))).thenReturn(testDictItem);
        when(dictItemMapper.selectCount(any())).thenReturn(0L);

        dictService.saveItem(testDictItemDTO);

        verify(dictItemMapper).updateById(any(DictItemDO.class));
    }

    @Test
    @DisplayName("更新字典项")
    void testUpdateItem_ExistingItem() {
        when(dictConvertor.toItemDataObject(any(DictItemDTO.class))).thenReturn(testDictItem);

        dictService.updateItem(testDictItemDTO);

        verify(dictItemMapper).updateById(any(DictItemDO.class));
    }

    @Test
    @DisplayName("删除字典项")
    void testDeleteItem_Success() {
        dictService.deleteItem(1L);

        verify(dictItemMapper).deleteById(1L);
    }

    @Test
    @DisplayName("根据ID获取字典项")
    void testGetItemById_Success() {
        when(dictItemMapper.selectById(1L)).thenReturn(testDictItem);
        when(dictConvertor.toItemDTO(any(DictItemDO.class))).thenReturn(testDictItemDTO);

        DictItemDTO result = dictService.getItemById(1L);

        assertNotNull(result);
        assertEquals("1", result.getDictValue());
    }
}
