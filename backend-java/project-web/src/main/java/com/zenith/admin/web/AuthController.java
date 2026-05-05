package com.zenith.admin.web;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.AuthService;
import com.zenith.admin.dto.data.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public SingleResponse<UserDTO> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        SingleResponse<UserDTO> response = authService.login(
                request.getLoginId(), request.getPassword(), ip, userAgent);

        if (response.isSuccess()) {
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("userId", response.getData().getId());
            session.setAttribute("username", response.getData().getUsername());
            session.setAttribute("ip", ip);
            session.setAttribute("userAgent", userAgent);
            session.setAttribute("loginTime", System.currentTimeMillis());
        }

        return response;
    }

    @PostMapping("/logout")
    public Response logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
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
}
