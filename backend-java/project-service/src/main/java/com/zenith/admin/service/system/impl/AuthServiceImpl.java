package com.zenith.admin.service.system.impl;

import com.zenith.admin.api.system.AuthService;
import com.zenith.admin.api.system.UserService;
import com.zenith.admin.dto.system.data.UserDTO;
import com.zenith.admin.dto.system.qry.LoginQuery;
import com.zenith.admin.service.system.executor.cmd.UserChangePasswordCmdExe;
import com.zenith.admin.service.system.executor.cmd.UserUpdateProfileCmdExe;
import com.zenith.admin.service.system.executor.qry.LoginAuthQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 * 纯编排层，登录认证委托给 LoginAuthQryExe 执行，
 * 其他方法委托给对应的 Executor 或 Service
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LoginAuthQryExe loginAuthQryExe;
    private final UserService userService;
    private final UserChangePasswordCmdExe userChangePasswordCmdExe;
    private final UserUpdateProfileCmdExe userUpdateProfileCmdExe;

    @Override
    public UserDTO login(LoginQuery query) {
        return loginAuthQryExe.execute(query.getLoginId(), query.getPassword());
    }

    @Override
    public UserDTO getCurrentUser(Long userId) {
        return userService.getById(userId);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        userChangePasswordCmdExe.execute(userId, oldPassword, newPassword);
    }

    @Override
    public void updateProfile(Long userId, String username, String email, String phone) {
        userUpdateProfileCmdExe.execute(userId, username, email, phone);
    }
}
