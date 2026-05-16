package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.AuthService;
import com.zenith.admin.api.LoginLogService;
import com.zenith.admin.api.PermissionService;
import com.zenith.admin.dto.data.LoginLogDTO;
import com.zenith.admin.dto.data.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LoginLogService loginLogService;
    private final PermissionService permissionService;

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

        if (response.isSuccess()) {
            loginLogDTO.setStatus("成功");
            loginLogDTO.setMsg("登录成功");
            loginLogDTO.setCreateUserId(response.getData().getId());
            loginLogDTO.setUpdateUserId(response.getData().getId());

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("userId", response.getData().getId());
            session.setAttribute("username", response.getData().getUsername());
            session.setAttribute("ip", ip);
            session.setAttribute("userAgent", userAgent);
            session.setAttribute("loginTime", System.currentTimeMillis());
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
            session.invalidate();
            if (username != null) {
                loginLogService.updateLogoutAt(username);
            }
        }
        return Response.buildSuccess();
    }

    @GetMapping("/me")
    public SingleResponse<UserDTO> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return SingleResponse.buildFailure("NOT_LOGIN", "未登录");
        }
        Long userId = (Long) session.getAttribute("userId");
        UserDTO user = authService.getCurrentUser(userId);
        return SingleResponse.of(user);
    }

    @PostMapping("/password")
    public Response changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.buildFailure("NOT_LOGIN", "未登录");
        }
        Long userId = (Long) session.getAttribute("userId");
        return authService.changePassword(userId, changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
    }

    @GetMapping("/permissions")
    public MultiResponse<String> getCurrentUserPermissions(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return MultiResponse.buildFailure("NOT_LOGIN", "未登录");
        }
        Long userId = (Long) session.getAttribute("userId");
        List<String> permissions = permissionService.getUserPermissions(userId);
        return MultiResponse.of(permissions);
    }

    @PostMapping("/profile")
    public Response updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.buildFailure("NOT_LOGIN", "未登录");
        }
        Long userId = (Long) session.getAttribute("userId");
        return authService.updateProfile(userId, updateProfileRequest.getUsername(), updateProfileRequest.getEmail(), updateProfileRequest.getPhone());
    }

    @lombok.Data
    public static class LoginRequest {
        private String loginId;
        private String password;
    }

    @lombok.Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }

    @lombok.Data
    public static class UpdateProfileRequest {
        private String username;
        private String email;
        private String phone;
    }
}
