package com.zenith.admin.web;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.AuthService;
import com.zenith.admin.api.LoginLogService;
import com.zenith.admin.dto.data.LoginLogDTO;
import com.zenith.admin.dto.data.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private LoginLogService loginLogService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("登录成功 - 记录成功日志")
    void testLogin_Success_SaveLoginLog() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr("192.168.1.100");
        httpRequest.addHeader("User-Agent", "Mozilla/5.0");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("admin");
        SingleResponse<UserDTO> successResponse = SingleResponse.of(userDTO);
        when(authService.login(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(successResponse);

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setLoginId("admin");
        loginRequest.setPassword("000000");

        SingleResponse<UserDTO> response = authController.login(loginRequest, httpRequest);

        assertTrue(response.isSuccess());

        ArgumentCaptor<LoginLogDTO> logCaptor = ArgumentCaptor.forClass(LoginLogDTO.class);
        verify(loginLogService).save(logCaptor.capture());

        LoginLogDTO savedLog = logCaptor.getValue();
        assertEquals("admin", savedLog.getUsername());
        assertEquals("192.168.1.100", savedLog.getIp());
        assertEquals("成功", savedLog.getStatus());
        assertEquals("登录成功", savedLog.getMsg());
        assertEquals(1L, savedLog.getCreateUserId());
        assertEquals(1L, savedLog.getUpdateUserId());
        assertNotNull(savedLog.getLoginAt());
        assertNotNull(savedLog.getCreatedTime());
        assertNotNull(savedLog.getUpdateTime());
    }

    @Test
    @DisplayName("登录失败 - 用户不存在 - 记录失败日志")
    void testLogin_UserNotFound_SaveFailLog() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr("10.0.0.1");
        httpRequest.addHeader("User-Agent", "Mozilla/5.0");

        SingleResponse<UserDTO> failResponse = SingleResponse.buildFailure("USER_NOT_FOUND", "登录账号不存在");
        when(authService.login(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(failResponse);

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setLoginId("nonexistent");
        loginRequest.setPassword("wrong");

        SingleResponse<UserDTO> response = authController.login(loginRequest, httpRequest);

        assertFalse(response.isSuccess());

        ArgumentCaptor<LoginLogDTO> logCaptor = ArgumentCaptor.forClass(LoginLogDTO.class);
        verify(loginLogService).save(logCaptor.capture());

        LoginLogDTO savedLog = logCaptor.getValue();
        assertEquals("nonexistent", savedLog.getUsername());
        assertEquals("10.0.0.1", savedLog.getIp());
        assertEquals("失败", savedLog.getStatus());
        assertEquals("登录账号不存在", savedLog.getMsg());
        assertNull(savedLog.getCreateUserId());
        assertNull(savedLog.getUpdateUserId());
        assertNotNull(savedLog.getLoginAt());
    }

    @Test
    @DisplayName("登录失败 - 密码错误 - 记录失败日志")
    void testLogin_WrongPassword_SaveFailLog() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr("172.16.0.1");

        SingleResponse<UserDTO> failResponse = SingleResponse.buildFailure("PASSWORD_ERROR", "密码错误");
        when(authService.login(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(failResponse);

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setLoginId("admin");
        loginRequest.setPassword("wrongpassword");

        SingleResponse<UserDTO> response = authController.login(loginRequest, httpRequest);

        assertFalse(response.isSuccess());

        ArgumentCaptor<LoginLogDTO> logCaptor = ArgumentCaptor.forClass(LoginLogDTO.class);
        verify(loginLogService).save(logCaptor.capture());

        LoginLogDTO savedLog = logCaptor.getValue();
        assertEquals("admin", savedLog.getUsername());
        assertEquals("失败", savedLog.getStatus());
        assertEquals("密码错误", savedLog.getMsg());
    }

    @Test
    @DisplayName("登录成功 - 创建Session并存入用户信息")
    void testLogin_Success_CreateSession() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr("192.168.1.100");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("admin");
        SingleResponse<UserDTO> successResponse = SingleResponse.of(userDTO);
        when(authService.login(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(successResponse);

        HttpSession mockSession = httpRequest.getSession(true);
        when(httpRequest.getSession(true)).thenReturn(mockSession);

        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setLoginId("admin");
        loginRequest.setPassword("000000");

        authController.login(loginRequest, httpRequest);

        assertEquals(1L, mockSession.getAttribute("userId"));
        assertEquals("admin", mockSession.getAttribute("username"));
        assertEquals("192.168.1.100", mockSession.getAttribute("ip"));
        assertNotNull(mockSession.getAttribute("loginTime"));
    }

    @Test
    @DisplayName("登出 - 更新登出时间")
    void testLogout_UpdateLogoutAt() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("username", "admin");

        authController.logout(httpRequest);

        verify(loginLogService).updateLogoutAt("admin");
    }

    @Test
    @DisplayName("登出 - Session为空时不更新登出时间")
    void testLogout_NoSession() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        authController.logout(httpRequest);

        verify(loginLogService, never()).updateLogoutAt(anyString());
    }

    @Test
    @DisplayName("登出 - Session中无username时不更新登出时间")
    void testLogout_NoUsernameInSession() {
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        HttpSession session = httpRequest.getSession(true);
        session.removeAttribute("username");

        authController.logout(httpRequest);

        verify(loginLogService, never()).updateLogoutAt(anyString());
    }
}
