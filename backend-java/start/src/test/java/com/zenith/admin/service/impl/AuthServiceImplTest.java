package com.zenith.admin.service.impl;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.AuthService;
import com.zenith.admin.api.TokenService;
import com.zenith.admin.api.UserService;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserDO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDO();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW");
        testUser.setStatus(1);
    }

    @Test
    @DisplayName("登录成功 - 用户名密码正确")
    void testLogin_Success() {
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(tokenService.generateToken(anyLong(), anyString())).thenReturn("test-token-123");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("admin");
        when(userService.getById(anyLong())).thenReturn(userDTO);

        SingleResponse<UserDTO> response = authService.login("admin", "000000", "127.0.0.1");

        assertTrue(response.getSuccess());
        assertNotNull(response.getData());
        assertEquals("admin", response.getData().getUsername());
        verify(tokenService).generateToken(1L, "127.0.0.1");
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void testLogin_UserNotFound() {
        when(userMapper.selectOne(any())).thenReturn(null);

        SingleResponse<UserDTO> response = authService.login("nonexistent", "password", "127.0.0.1");

        assertFalse(response.getSuccess());
        assertEquals("USER_NOT_FOUND", response.getErrCode());
        verify(tokenService, never()).generateToken(anyLong(), anyString());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void testLogin_WrongPassword() {
        when(userMapper.selectOne(any())).thenReturn(testUser);

        SingleResponse<UserDTO> response = authService.login("admin", "wrongpassword", "127.0.0.1");

        assertFalse(response.getSuccess());
        assertEquals("PASSWORD_ERROR", response.getErrCode());
        verify(tokenService, never()).generateToken(anyLong(), anyString());
    }

    @Test
    @DisplayName("登录失败 - 用户已禁用")
    void testLogin_UserDisabled() {
        testUser.setStatus(0);
        when(userMapper.selectOne(any())).thenReturn(testUser);

        SingleResponse<UserDTO> response = authService.login("admin", "000000", "127.0.0.1");

        assertFalse(response.getSuccess());
        assertEquals("USER_DISABLED", response.getErrCode());
    }

    @Test
    @DisplayName("退出登录成功")
    void testLogout_Success() {
        Response response = authService.logout("test-token-123");

        assertTrue(response.getSuccess());
        verify(tokenService).deleteToken("test-token-123");
    }

    @Test
    @DisplayName("退出登录 - token为空")
    void testLogout_EmptyToken() {
        Response response = authService.logout("");

        assertTrue(response.getSuccess());
        verify(tokenService, never()).deleteToken(anyString());
    }

    @Test
    @DisplayName("获取当前用户成功")
    void testGetCurrentUser_Success() {
        UserDTO expectedUser = new UserDTO();
        expectedUser.setId(1L);
        expectedUser.setUsername("admin");
        when(userService.getById(1L)).thenReturn(expectedUser);

        UserDTO result = authService.getCurrentUser(1L);

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(userService).getById(1L);
    }

    @Test
    @DisplayName("修改密码成功")
    void testChangePassword_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        Response response = authService.changePassword(1L, "000000", "newpassword123");

        assertTrue(response.getSuccess());
        verify(userMapper).updateById(any(UserDO.class));
    }

    @Test
    @DisplayName("修改密码失败 - 旧密码错误")
    void testChangePassword_WrongOldPassword() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        Response response = authService.changePassword(1L, "wrongoldpassword", "newpassword123");

        assertFalse(response.getSuccess());
        assertEquals("OLD_PASSWORD_ERROR", response.getErrCode());
        verify(userMapper, never()).updateById(any(UserDO.class));
    }

    @Test
    @DisplayName("修改密码失败 - 用户不存在")
    void testChangePassword_UserNotFound() {
        when(userMapper.selectById(1L)).thenReturn(null);

        Response response = authService.changePassword(1L, "000000", "newpassword123");

        assertFalse(response.getSuccess());
        assertEquals("USER_NOT_FOUND", response.getErrCode());
    }
}
