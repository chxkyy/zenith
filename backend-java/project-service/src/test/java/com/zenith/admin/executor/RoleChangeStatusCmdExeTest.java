package com.zenith.admin.executor;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.mapper.RoleMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleChangeStatusCmdExeTest {

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleChangeStatusCmdExe roleChangeStatusCmdExe;

    @Test
    @DisplayName("禁用超级管理员角色 - 应抛出业务异常")
    void testExecute_DisableSuperAdminRole_ShouldThrowBizException() {
        Long superAdminRoleId = 1L;
        Integer disabledStatus = 0;
        Long currentUserId = 100L;

        BizException exception = assertThrows(BizException.class, () ->
                roleChangeStatusCmdExe.execute(superAdminRoleId, disabledStatus, currentUserId)
        );

        assertEquals("ROLE_STATUS_001", exception.getErrCode());
        assertEquals("超级管理员角色不可禁用", exception.getMessage());
    }

    @Test
    @DisplayName("正常修改普通角色状态 - 应正确更新状态和修改人")
    void testExecute_UpdateNormalRoleStatus_Success() {
        Long roleId = 2L;
        Integer newStatus = 0;
        Long currentUserId = 100L;

        RoleDO existingRole = new RoleDO();
        existingRole.setId(2L);
        existingRole.setName("普通角色");
        existingRole.setStatus(1);
        when(roleMapper.selectById(roleId)).thenReturn(existingRole);

        roleChangeStatusCmdExe.execute(roleId, newStatus, currentUserId);

        ArgumentCaptor<RoleDO> captor = ArgumentCaptor.forClass(RoleDO.class);
        verify(roleMapper).updateById(captor.capture());

        RoleDO updatedRole = captor.getValue();
        assertEquals(0, updatedRole.getStatus());
        assertEquals(100L, updatedRole.getUpdateUserId());
    }

    @Test
    @DisplayName("角色ID不存在 - 不调用updateById且不抛异常")
    void testExecute_RoleNotFound_ShouldNotUpdate() {
        Long nonExistentRoleId = 999L;
        Integer newStatus = 0;
        Long currentUserId = 100L;

        when(roleMapper.selectById(nonExistentRoleId)).thenReturn(null);

        roleChangeStatusCmdExe.execute(nonExistentRoleId, newStatus, currentUserId);

        verify(roleMapper, never()).updateById(any(RoleDO.class));
    }

    @Test
    @DisplayName("启用超级管理员角色 - 应允许操作并正常更新")
    void testExecute_EnableSuperAdminRole_ShouldSucceed() {
        Long superAdminRoleId = 1L;
        Integer enabledStatus = 1;
        Long currentUserId = 100L;

        RoleDO superAdminRole = new RoleDO();
        superAdminRole.setId(1L);
        superAdminRole.setName("超级管理员");
        superAdminRole.setStatus(0);
        when(roleMapper.selectById(superAdminRoleId)).thenReturn(superAdminRole);

        roleChangeStatusCmdExe.execute(superAdminRoleId, enabledStatus, currentUserId);

        ArgumentCaptor<RoleDO> captor = ArgumentCaptor.forClass(RoleDO.class);
        verify(roleMapper).updateById(captor.capture());

        RoleDO updatedRole = captor.getValue();
        assertEquals(1, updatedRole.getStatus());
        assertEquals(100L, updatedRole.getUpdateUserId());
    }
}
