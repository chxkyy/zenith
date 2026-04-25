package com.zenith.admin.web;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.AuthService;
import com.zenith.admin.dto.data.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public SingleResponse<UserDTO> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        String ip = request.getRemoteAddr();
        SingleResponse<UserDTO> loginResponse = authService.login(loginRequest.getUsername(), loginRequest.getPassword(), ip);

        if (loginResponse.isSuccess()) {
            String token = authService.getTokenAndClean();

            Cookie cookie = new Cookie("ZENITH_TOKEN", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(7));
            response.addCookie(cookie);
        }

        return loginResponse;
    }

    @PostMapping("/logout")
    public Response logout(HttpServletRequest request, HttpServletResponse response) {
        String token = getTokenFromCookie(request);

        authService.logout(token);

        Cookie cookie = new Cookie("ZENITH_TOKEN", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return Response.buildSuccess();
    }

    @GetMapping("/me")
    public SingleResponse<UserDTO> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserDTO user = authService.getCurrentUser(userId);
        return SingleResponse.of(user);
    }

    @PostMapping("/password")
    public Response changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return authService.changePassword(userId, changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("ZENITH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @lombok.Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @lombok.Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
