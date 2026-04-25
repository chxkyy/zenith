package com.zenith.admin.service;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.MenuConvertor;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.MenuPageQuery;
import com.zenith.admin.mapper.MenuMapper;
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
class MenuServiceImplTest {

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private MenuConvertor menuConvertor;

    @InjectMocks
    private MenuServiceImpl menuService;

    private MenuDO testMenu;
    private MenuDTO testMenuDTO;

    @BeforeEach
    void setUp() {
        testMenu = new MenuDO();
        testMenu.setId(1L);
        testMenu.setName("用户管理");
        testMenu.setPath("/users");
        testMenu.setIcon("user");
        testMenu.setSort(1);
        testMenu.setType("menu");

        testMenuDTO = new MenuDTO();
        testMenuDTO.setId(1L);
        testMenuDTO.setName("用户管理");
        testMenuDTO.setPath("/users");
        testMenuDTO.setIcon("user");
    }

    @Test
    @DisplayName("获取所有菜单列表")
    void testListAll_Success() {
        when(menuMapper.selectList(null)).thenReturn(Arrays.asList(testMenu));
        when(menuConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testMenuDTO));

        MultiResponse<MenuDTO> result = menuService.listAll();

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
    }

    @Test
    @DisplayName("分页查询菜单列表")
    void testPage_Success() {
        MenuPageQuery query = new MenuPageQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        when(menuMapper.selectList(any())).thenReturn(Arrays.asList(testMenu));
        when(menuConvertor.toDataObject(any(MenuDTO.class))).thenReturn(testMenu);
        when(menuConvertor.toDTO(any(MenuDO.class))).thenReturn(testMenuDTO);
        when(menuConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testMenuDTO));

        PageInfo<MenuDTO> result = menuService.page(query);

        assertNotNull(result);
        verify(menuMapper).selectList(any());
    }

    @Test
    @DisplayName("保存菜单")
    void testSave_Menu() {
        when(menuConvertor.toDataObject(any(MenuDTO.class))).thenReturn(testMenu);

        menuService.save(testMenuDTO);

        verify(menuMapper).updateById(any(MenuDO.class));
    }

    @Test
    @DisplayName("更新菜单信息")
    void testUpdate_Menu() {
        when(menuConvertor.toDataObject(any(MenuDTO.class))).thenReturn(testMenu);

        menuService.update(testMenuDTO);

        verify(menuMapper).updateById(any(MenuDO.class));
    }

    @Test
    @DisplayName("根据ID获取菜单")
    void testGetById_Success() {
        when(menuMapper.selectById(1L)).thenReturn(testMenu);
        when(menuConvertor.toDTO(any(MenuDO.class))).thenReturn(testMenuDTO);

        MenuDTO result = menuService.getById(1L);

        assertNotNull(result);
        assertEquals("用户管理", result.getName());
    }

    @Test
    @DisplayName("删除菜单")
    void testDelete_Success() {
        menuService.delete(1L);

        verify(menuMapper).deleteById(1L);
    }
}
