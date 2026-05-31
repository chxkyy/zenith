package com.zenith.admin.executor;

import com.zenith.admin.service.system.executor.converter.RoleConvertor;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dto.data.RoleDTO;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.service.system.executor.qry.RoleGetByIdQryExe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleGetByIdQryExeTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private RoleConvertor roleConvertor;

    @InjectMocks
    private RoleGetByIdQryExe roleGetByIdQryExe;

    private RoleDO testRoleDO;
    private RoleDTO testRoleDTO;

    @BeforeEach
    void setUp() {
        testRoleDO = new RoleDO();
        testRoleDO.setId(1L);
        testRoleDO.setName("管理员");
        testRoleDO.setDescription("系统管理员");
        testRoleDO.setStatus(1);

        testRoleDTO = new RoleDTO();
        testRoleDTO.setId(1L);
        testRoleDTO.setName("管理员");
        testRoleDTO.setDescription("系统管理员");
        testRoleDTO.setStatus(1);
    }

    @Test
    @DisplayName("根据ID获取角色 - 成功返回角色信息")
    void testExecute_Success() {
        when(roleMapper.selectById(1L)).thenReturn(testRoleDO);
        when(roleConvertor.toDTO(testRoleDO)).thenReturn(testRoleDTO);

        RoleDTO result = roleGetByIdQryExe.execute(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("管理员", result.getName());
        assertEquals("系统管理员", result.getDescription());
        assertEquals(1, result.getStatus());
    }

    @Test
    @DisplayName("根据ID获取角色 - 角色不存在返回null")
    void testExecute_RoleNotFound_ReturnsNull() {
        when(roleMapper.selectById(999L)).thenReturn(null);

        RoleDTO result = roleGetByIdQryExe.execute(999L);

        assertNull(result);
    }
}
