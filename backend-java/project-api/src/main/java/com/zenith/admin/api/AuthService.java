package com.zenith.admin.api;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(String username, String password);
    Map<String, Object> logout(String token);
    Map<String, Object> getCurrentUser(String token);
    Map<String, Object> refreshToken(String token);
}
