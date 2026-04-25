package com.zenith.admin.api;

import com.zenith.admin.dto.data.OnlineUserDTO;

public interface TokenService {
    String generateToken(Long userId, String ip);
    OnlineUserDTO validateToken(String token);
    void refreshToken(String token);
    void deleteToken(String token);
    void deleteTokenByUserId(Long userId);
}
