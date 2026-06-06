package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dto.cmd.RoleAddCmd;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.service.system.executor.cmd.RoleSaveCmdExe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleSaveCmdExeTest {

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleSaveCmdExe roleSaveCmdExe;

    private RoleAddCmd testCmd;

    @BeforeEach
    void setUp() {
        testCmd = new RoleAddCmd();
        testCmd.setName("新角色");
        testCmd.setStatus(1);
        testCmd.setDescription("测试角色描述");
    }

    @Test
    @DisplayName("保存角色 - 成功创建新角色")
    void testExecute_Success() {
        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);

        roleSaveCmdExe.execute(testCmd, 100L);

        ArgumentCaptor<RoleDO> captor = ArgumentCaptor.forClass(RoleDO.class);
        verify(roleMapper).insert(captor.capture());

        RoleDO savedRole = captor.getValue();
        assertEquals("新角色", savedRole.getName());
        assertEquals(1, savedRole.getStatus());
        assertEquals("测试角色描述", savedRole.getDescription());
        assertEquals(100L, savedRole.getCreateUserId());
        assertEquals(100L, savedRole.getUpdateUserId());
    }
}
