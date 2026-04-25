package com.zenith.admin.service;

import com.alibaba.cola.dto.MultiResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.RoleService;
import com.zenith.admin.RoleConvertor;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.dto.data.RolePageQuery;
import com.zenith.admin.mapper.RoleMapper;
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
class RoleServiceImplTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RoleConvertor roleConvertor;

    @InjectMocks
    private RoleServiceImpl roleService;

    private RoleDO testRole;
    private RoleDTO testRoleDTO;

    @BeforeEach
    void setUp() {
        testRole = new RoleDO();
        testRole.setId(1L);
        testRole.setName("普通用户");
        testRole.setCode("ROLE_USER");
        testRole.setStatus(1);

        testRoleDTO = new RoleDTO();
        testRoleDTO.setId(1L);
        testRoleDTO.setName("普通用户");
        testRoleDTO.setCode("ROLE_USER");
    }

    @Test
    @DisplayName("获取所有角色列表")
    void testListAll_Success() {
        when(roleMapper.selectList(null)).thenReturn(Arrays.asList(testRole));
        when(roleConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testRoleDTO));

        MultiResponse<RoleDTO> result = roleService.listAll();

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
    }

    @Test
    @DisplayName("分页查询角色列表")
    void testListByPage_Success() {
        RolePageQuery query = new RolePageQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        when(roleMapper.selectList(any())).thenReturn(Arrays.asList(testRole));
        when(roleConvertor.toDataObject(any(RoleDTO.class))).thenReturn(testRole);
        when(roleConvertor.toDTO(any(RoleDO.class))).thenReturn(testRoleDTO);
        when(roleConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testRoleDTO));

        PageInfo<RoleDTO> result = roleService.listByPage(query);

        assertNotNull(result);
        verify(roleMapper).selectList(any());
    }

    @Test
    @DisplayName("保存角色")
    void testSave_Role() {
        when(roleConvertor.toDataObject(any(RoleDTO.class))).thenReturn(testRole);

        roleService.save(testRoleDTO);

        verify(roleMapper).updateById(any(RoleDO.class));
    }

    @Test
    @DisplayName("更新角色信息")
    void testUpdate_Role() {
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(roleConvertor.toDataObject(any(RoleDTO.class))).thenReturn(testRole);

        roleService.update(testRoleDTO);

        verify(roleMapper).updateById(any(RoleDO.class));
    }

    @Test
    @DisplayName("更新ADMIN角色时code不可修改")
    void testUpdate_AdminRoleCodeNotChanged() {
        testRole.setCode("ADMIN");
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(roleConvertor.toDataObject(any(RoleDTO.class))).thenReturn(testRole);

        testRoleDTO.setCode("NEW_CODE");
        roleService.update(testRoleDTO);

        verify(roleMapper).updateById(any(RoleDO.class));
    }

    @Test
    @DisplayName("根据ID获取角色")
    void testGetById_Success() {
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(roleConvertor.toDTO(any(RoleDO.class))).thenReturn(testRoleDTO);

        RoleDTO result = roleService.getById(1L);

        assertNotNull(result);
        assertEquals("ROLE_USER", result.getCode());
    }

    @Test
    @DisplayName("删除普通角色成功")
    void testDelete_NormalRole() {
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        roleService.delete(1L);

        verify(roleMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除ADMIN角色抛出异常")
    void testDelete_AdminRole() {
        testRole.setCode("ADMIN");
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        assertThrows(RuntimeException.class, () -> roleService.delete(1L));
        verify(roleMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("修改角色状态 - 启用")
    void testChangeStatus_Enable() {
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        roleService.changeStatus(1L, 1);

        verify(roleMapper).updateById(any(RoleDO.class));
    }

    @Test
    @DisplayName("禁用ADMIN角色抛出异常")
    void testChangeStatus_DisableAdminRole() {
        testRole.setCode("ADMIN");
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        assertThrows(RuntimeException.class, () -> roleService.changeStatus(1L, 0));
        verify(roleMapper, never()).updateById(any(RoleDO.class));
    }
}
