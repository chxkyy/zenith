package com.zenith.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.UserService;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.query.LoginQuery;
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
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginQuery loginQuery;
    private UserDO userDO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        loginQuery = new LoginQuery();
        loginQuery.setLoginId("zhangsan");
        loginQuery.setPassword("123456");

        userDO = new UserDO();
        userDO.setId(1L);
        userDO.setLoginId("zhangsan");
        userDO.setUsername("张三");
        userDO.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        userDO.setStatus(1);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setLoginId("zhangsan");
        userDTO.setUsername("张三");
        userDTO.setStatus(1);
    }

    @Test
    @DisplayName("登录 - 正确账号密码返回用户信息")
    void testLogin_Success() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(userDO);
        when(userService.getById(1L)).thenReturn(userDTO);

        UserDTO result = authService.login(loginQuery);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("zhangsan", result.getLoginId());
        assertEquals("张三", result.getUsername());
        verify(userMapper).selectOne(any(LambdaQueryWrapper.class));
        verify(userService).getById(1L);
    }
}
