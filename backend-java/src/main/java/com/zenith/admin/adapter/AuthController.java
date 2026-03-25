package com.zenith.admin.adapter;

import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.domain.gateway.UserGateway;
import com.zenith.admin.domain.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserGateway userGateway;

    @PostMapping("/login")
    public SingleResponse<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        // In a real app, we'd check password here
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", "mock-jwt-token-" + username);
        result.put("username", username);
        return SingleResponse.of(result);
    }

    @GetMapping("/me")
    public SingleResponse<Map<String, Object>> me() {
        // Mock current user for now, but we could fetch from DB if we had session/token logic
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1L);
        user.put("username", "admin");
        user.put("nickname", "超级管理员");
        user.put("avatar", "https://picsum.photos/seed/admin/100/100");
        user.put("roles", new String[]{"ROLE_ADMIN"});
        user.put("permissions", new String[]{"*:*:*"});
        return SingleResponse.of(user);
    }
}
