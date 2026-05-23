package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.AuthService;
import com.zenith.admin.api.LoginLogService;
import com.zenith.admin.api.PermissionService;
import com.zenith.admin.config.RedisSessionRepository;
import com.zenith.admin.dto.data.LoginLogDTO;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final LoginLogService loginLogService;
    private final PermissionService permissionService;
    private final RedisSessionRepository sessionRepository;

    @PostMapping("/password")
    public Response changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
                                   HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.buildFailure("NOT_LOGIN", "未登录");
        }
        Long userId = getUserIdFromSession(session);
        return authService.changePassword(userId, changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword());
    }

    @GetMapping("/me")
    public SingleResponse<UserDTO> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return SingleResponse.buildFailure("NOT_LOGIN", "未登录");
        }
        Long userId = getUserIdFromSession(session);
        UserDTO user = authService.getCurrentUser(userId);
        return SingleResponse.of(user);
    }

    @GetMapping("/menus")
    public MultiResponse<MenuDTO> getCurrentUserMenus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return MultiResponse.buildFailure("NOT_LOGIN", "未登录");
        }
        Long userId = getUserIdFromSession(session);
        List<MenuDTO> menus = permissionService.getAccessibleMenus(userId);
        return MultiResponse.of(menus);
    }

    @GetMapping("/permissions")
    public MultiResponse<String> getCurrentUserPermissions(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return MultiResponse.buildFailure("NOT_LOGIN", "未登录");
        }
        Long userId = getUserIdFromSession(session);
        List<String> permissions = permissionService.getUserPermissions(userId);
        return MultiResponse.of(permissions);
    }

    private Long getUserIdFromSession(HttpSession session) {
        Object userIdAttr = session.getAttribute("userId");
        if (userIdAttr instanceof Number) {
            return ((Number) userIdAttr).longValue();
        }
        return null;
    }

    @PostMapping("/login")
    public SingleResponse<UserDTO> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        SingleResponse<UserDTO> response = authService.login(
                request.getLoginId(), request.getPassword(), ip, userAgent);

        LoginLogDTO loginLogDTO = new LoginLogDTO();
        loginLogDTO.setUsername(request.getLoginId());
        loginLogDTO.setIp(ip);
        loginLogDTO.setLoginAt(LocalDateTime.now());
        loginLogDTO.setCreatedTime(LocalDateTime.now());
        loginLogDTO.setUpdateTime(LocalDateTime.now());
        loginLogDTO.setUserAgent(userAgent);

        if (response.isSuccess()) {
            loginLogDTO.setStatus("成功");
            loginLogDTO.setMsg("登录成功");
            loginLogDTO.setCreateUserId(response.getData().getId());
            loginLogDTO.setUpdateUserId(response.getData().getId());

            HttpSession session = httpRequest.getSession(true);
            Long userId = response.getData().getId();
            String username = response.getData().getUsername();
            String loginId = request.getLoginId();
            
            session.setAttribute("userId", userId);
            session.setAttribute("username", username);
            session.setAttribute("loginId", loginId);
            session.setAttribute("ip", ip);
            session.setAttribute("userAgent", userAgent);
            session.setAttribute("loginTime", System.currentTimeMillis());

            String sessionId = session.getId();
            log.info("Login success for user {}, sessionId: {}", username, sessionId);
            
            RedisSessionRepository.RedisSession redisSession = sessionRepository.findById(sessionId);
            if (redisSession != null) {
                redisSession.setUserId(userId);
                redisSession.setUsername(username);
                redisSession.setIp(ip);
                redisSession.setUserAgent(userAgent);
                redisSession.setLoginTime(System.currentTimeMillis());
                sessionRepository.save(redisSession);
                
                sessionRepository.enforceMaxConcurrentSessions(userId, sessionId);
            } else {
                log.warn("RedisSession not found for sessionId: {}", sessionId);
            }
        } else {
            loginLogDTO.setStatus("失败");
            loginLogDTO.setMsg(response.getErrMessage());
        }

        loginLogService.save(loginLogDTO);
        return response;
    }

    @PostMapping("/logout")
    public Response logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            String sessionId = session.getId();
            
            sessionRepository.deleteById(sessionId);
            
            session.invalidate();
            SecurityContextHolder.clearContext();
            
            if (username != null) {
                loginLogService.updateLogoutAt(username);
            }
        }
        return Response.buildSuccess();
    }

    @PostMapping("/profile")
    public Response updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.buildFailure("NOT_LOGIN", "未登录");
        }
        Long userId = (Long) session.getAttribute("userId");
        return authService.updateProfile(userId, updateProfileRequest.getUsername(), updateProfileRequest.getEmail(),
                updateProfileRequest.getPhone());
    }

    @lombok.Data
    public static class LoginRequest {
        private String loginId;
        private String password;
    }

    @lombok.Data
    public static class ChangePasswordRequest {
        private String newPassword;
        private String oldPassword;
    }

    @lombok.Data
    public static class UpdateProfileRequest {
        private String email;
        private String phone;
        private String username;
    }
}
