package com.zenith.admin.api;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.dto.data.UserDTO;

public interface AuthService {
    SingleResponse<UserDTO> login(String username, String password, String ip);
    Response logout(String token);
    UserDTO getCurrentUser(Long userId);
    Response changePassword(Long userId, String oldPassword, String newPassword);
    String getTokenAndClean();
}
