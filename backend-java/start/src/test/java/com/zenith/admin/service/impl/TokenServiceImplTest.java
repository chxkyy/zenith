package com.zenith.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.TokenService;
import com.zenith.admin.dataobject.OnlineUserDO;
import com.zenith.admin.dto.data.OnlineUserDTO;
import com.zenith.admin.mapper.OnlineUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest implements TokenService {

    @Mock
    private OnlineUserMapper onlineUserMapper;

    @InjectMocks
    private TokenServiceImpl tokenService;

    private OnlineUserDO testOnlineUser;

    @BeforeEach
    void setUp() {
        testOnlineUser = new OnlineUserDO();
        testOnlineUser.setId(1L);
        testOnlineUser.setUserId(1L);
        testOnlineUser.setToken("test-token-123");
        testOnlineUser.setIp("127.0.0.1");
        testOnlineUser.setLoginTime(LocalDateTime.now());
        testOnlineUser.setLastAccessTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("生成Token - 新用户")
    void testGenerateToken_NewUser() {
        when(onlineUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        String token = tokenService.generateToken(1L, "127.0.0.1");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        verify(onlineUserMapper).insert(any(OnlineUserDO.class));
    }

    @Test
    @DisplayName("生成Token - 已存在用户更新token")
    void testGenerateToken_ExistingUser() {
        when(onlineUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOnlineUser);

        String token = tokenService.generateToken(1L, "192.168.1.1");

        assertNotNull(token);
        assertNotEquals("test-token-123", token);
        verify(onlineUserMapper).updateById(any(OnlineUserDO.class));
    }

    @Test
    @DisplayName("验证Token - 有效token")
    void testValidateToken_ValidToken() {
        when(onlineUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOnlineUser);

        OnlineUserDTO result = tokenService.validateToken("test-token-123");

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(onlineUserMapper).updateById(any(OnlineUserDO.class));
    }

    @Test
    @DisplayName("验证Token - 无效token返回null")
    void testValidateToken_InvalidToken() {
        when(onlineUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        OnlineUserDTO result = tokenService.validateToken("invalid-token");

        assertNull(result);
    }

    @Test
    @DisplayName("验证Token - 空token返回null")
    void testValidateToken_NullToken() {
        OnlineUserDTO result = tokenService.validateToken(null);

        assertNull(result);
    }

    @Test
    @DisplayName("验证Token - 空字符串返回null")
    void testValidateToken_EmptyToken() {
        OnlineUserDTO result = tokenService.validateToken("");

        assertNull(result);
    }

    @Test
    @DisplayName("刷新Token - 成功刷新")
    void testRefreshToken_Success() {
        when(onlineUserMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOnlineUser);

        tokenService.refreshToken("test-token-123");

        verify(onlineUserMapper).updateById(any(OnlineUserDO.class));
    }

    @Test
    @DisplayName("刷新Token - 空token不执行操作")
    void testRefreshToken_NullToken() {
        tokenService.refreshToken(null);

        verify(onlineUserMapper, never()).selectOne(any(LambdaQueryWrapper.class));
        verify(onlineUserMapper, never()).updateById(any(OnlineUserDO.class));
    }

    @Test
    @DisplayName("删除Token - 成功删除")
    void testDeleteToken_Success() {
        tokenService.deleteToken("test-token-123");

        verify(onlineUserMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("删除Token - 空token不执行操作")
    void testDeleteToken_EmptyToken() {
        tokenService.deleteToken("");

        verify(onlineUserMapper, never()).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("根据用户ID删除Token")
    void testDeleteTokenByUserId() {
        tokenService.deleteTokenByUserId(1L);

        verify(onlineUserMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("根据用户ID删除Token - null ID不执行操作")
    void testDeleteTokenByUserId_NullId() {
        tokenService.deleteTokenByUserId(null);

        verify(onlineUserMapper, never()).delete(any(LambdaQueryWrapper.class));
    }
}
