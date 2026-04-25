package com.zenith.admin.service;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.UserService;
import com.zenith.admin.UserConvertor;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.data.UserPageQuery;
import com.zenith.admin.mapper.OrgMapper;
import com.zenith.admin.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private OrgMapper orgMapper;

    @Mock
    private UserConvertor userConvertor;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDO testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new UserDO();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setNickname("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setStatus(1);
        testUser.setRole("ROLE_USER");

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setUsername("testuser");
        testUserDTO.setNickname("测试用户");
    }

    @Test
    @DisplayName("分页查询用户列表")
    void testListByPage_Success() {
        UserPageQuery query = new UserPageQuery();
        query.setPageIndex(1);
        query.setPageSize(10);

        when(userMapper.selectList(any())).thenReturn(Arrays.asList(testUser));
        when(userConvertor.toDataObject(any(UserDTO.class))).thenReturn(testUser);
        when(userConvertor.toDTO(any(UserDO.class))).thenReturn(testUserDTO);
        when(userConvertor.toDTOList(anyList())).thenReturn(Arrays.asList(testUserDTO));

        PageInfo<UserDTO> result = userService.listByPage(query);

        assertNotNull(result);
        verify(userMapper).selectList(any());
    }

    @Test
    @DisplayName("保存新用户")
    void testSave_NewUser() {
        when(userConvertor.toDataObject(any(UserDTO.class))).thenReturn(testUser);

        userService.save(testUserDTO);

        verify(userMapper).insert(any(UserDO.class));
    }

    @Test
    @DisplayName("更新已有用户")
    void testUpdate_ExistingUser() {
        testUser.setId(1L);
        when(userConvertor.toDataObject(any(UserDTO.class))).thenReturn(testUser);

        userService.update(testUserDTO);

        verify(userMapper).updateById(any(UserDO.class));
    }

    @Test
    @DisplayName("根据ID获取用户")
    void testGetById_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userConvertor.toDTO(any(UserDO.class))).thenReturn(testUserDTO);

        UserDTO result = userService.getById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("根据ID获取用户 - 用户不存在返回null")
    void testGetById_NotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);
        when(userConvertor.toDTO(null)).thenReturn(null);

        UserDTO result = userService.getById(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("删除普通用户成功")
    void testDelete_NormalUser() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.delete(1L);

        verify(userMapper).deleteById(1L);
    }

    @Test
    @DisplayName("删除admin用户抛出异常")
    void testDelete_AdminUser() {
        testUser.setUsername("admin");
        when(userMapper.selectById(1L)).thenReturn(testUser);

        assertThrows(RuntimeException.class, () -> userService.delete(1L));
        verify(userMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("删除ADMIN角色用户抛出异常")
    void testDelete_AdminRoleUser() {
        testUser.setRole("ADMIN");
        when(userMapper.selectById(1L)).thenReturn(testUser);

        assertThrows(RuntimeException.class, () -> userService.delete(1L));
        verify(userMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("重置密码")
    void testResetPassword_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.resetPassword(1L);

        verify(userMapper).updateById(any(UserDO.class));
    }

    @Test
    @DisplayName("修改用户状态 - 启用")
    void testChangeStatus_Enable() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.changeStatus(1L, 1);

        verify(userMapper).updateById(any(UserDO.class));
    }

    @Test
    @DisplayName("修改用户状态 - 禁用普通用户")
    void testChangeStatus_DisableNormalUser() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        userService.changeStatus(1L, 0);

        verify(userMapper).updateById(any(UserDO.class));
    }

    @Test
    @DisplayName("禁用admin用户抛出异常")
    void testChangeStatus_DisableAdminUser() {
        testUser.setUsername("admin");
        when(userMapper.selectById(1L)).thenReturn(testUser);

        assertThrows(RuntimeException.class, () -> userService.changeStatus(1L, 0));
        verify(userMapper, never()).updateById(any(UserDO.class));
    }
}
