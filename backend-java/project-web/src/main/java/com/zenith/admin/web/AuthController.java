package com.zenith.admin.web;

import com.zenith.admin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        return authService.login(credentials.get("username"), credentials.get("password"));
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        return authService.logout(token);
    }

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        return authService.getCurrentUser(token);
    }

    @PostMapping("/refresh")
    public Map<String, Object> refreshToken(@RequestHeader(value = "Authorization", required = false) String token) {
        return authService.refreshToken(token);
    }
}
