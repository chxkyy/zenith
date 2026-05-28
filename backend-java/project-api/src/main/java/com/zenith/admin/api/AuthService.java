package com.zenith.admin.api;

import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.query.LoginQuery;

public interface AuthService {
    UserDTO login(LoginQuery query);
    UserDTO getCurrentUser(Long userId);
    void changePassword(Long userId, String oldPassword, String newPassword);
    void updateProfile(Long userId, String username, String email, String phone);
}
