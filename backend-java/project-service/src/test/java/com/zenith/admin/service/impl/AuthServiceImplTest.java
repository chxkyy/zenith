package com.zenith.admin.service.impl;

import com.zenith.admin.api.system.UserService;
import com.zenith.admin.dto.system.data.UserDTO;
import com.zenith.admin.dto.system.qry.LoginQuery;
import com.zenith.admin.service.system.executor.qry.LoginAuthQryExe;
import com.zenith.admin.service.system.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private LoginAuthQryExe loginAuthQryExe;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginQuery loginQuery;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        loginQuery = new LoginQuery();
        loginQuery.setLoginId("zhangsan");
        loginQuery.setPassword("123456");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setLoginId("zhangsan");
        userDTO.setUsername("张三");
        userDTO.setStatus(1);
    }

    @Test
    @DisplayName("登录 - 正确账号密码返回用户信息")
    void testLogin_Success() {
        when(loginAuthQryExe.execute(anyString(), anyString())).thenReturn(userDTO);

        UserDTO result = authService.login(loginQuery);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("zhangsan", result.getLoginId());
        assertEquals("张三", result.getUsername());
        verify(loginAuthQryExe).execute("zhangsan", "123456");
    }
}
