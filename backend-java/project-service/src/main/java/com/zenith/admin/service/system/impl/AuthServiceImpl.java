package com.zenith.admin.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.AuthService;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.query.LoginQuery;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.api.UserService;
import com.alibaba.cola.exception.BizException;
import com.zenith.admin.service.system.executor.cmd.UserChangePasswordCmdExe;
import com.zenith.admin.service.system.executor.cmd.UserUpdateProfileCmdExe;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserService userService;
    private final UserChangePasswordCmdExe userChangePasswordCmdExe;
    private final UserUpdateProfileCmdExe userUpdateProfileCmdExe;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDTO login(LoginQuery query) {
        UserDO user = userMapper.selectOne(
                new LambdaQueryWrapper<UserDO>().eq(UserDO::getLoginId, query.getLoginId())
        );

        if (user == null) {
            throw new BizException("USER_NOT_FOUND", "登录账号不存在");
        }

        if (!passwordEncoder.matches(query.getPassword(), user.getPassword())) {
            throw new BizException("PASSWORD_ERROR", "密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BizException("USER_DISABLED", "用户已禁用");
        }

        return userService.getById(user.getId());
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
