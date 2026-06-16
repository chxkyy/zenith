package com.zenith.admin.api.system;

import com.zenith.admin.dto.system.data.UserDTO;
import com.zenith.admin.dto.system.qry.LoginQuery;

public interface AuthService {
    UserDTO login(LoginQuery query);
    UserDTO getCurrentUser(Long userId);
    void changePassword(Long userId, String oldPassword, String newPassword);
    void updateProfile(Long userId, String username, String email, String phone);
}
