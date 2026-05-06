package com.zenith.admin.service.impl;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.AuthService;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SingleResponse<UserDTO> login(String loginId, String password, String ip, String userAgent) {
        UserDO user = userMapper.selectOne(
                new LambdaQueryWrapper<UserDO>().eq(UserDO::getLoginId, loginId)
        );

        if (user == null) {
            return SingleResponse.buildFailure("USER_NOT_FOUND", "登录账号不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return SingleResponse.buildFailure("PASSWORD_ERROR", "密码错误");
        }

        if (user.getStatus() != 1) {
            return SingleResponse.buildFailure("USER_DISABLED", "用户已禁用");
        }

        UserDTO userDTO = userService.getById(user.getId());
        return SingleResponse.of(userDTO);
    }

    @Override
    public UserDTO getCurrentUser(Long userId) {
        return userService.getById(userId);
    }

    @Override
    public Response changePassword(Long userId, String oldPassword, String newPassword) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            return Response.buildFailure("USER_NOT_FOUND", "用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Response.buildFailure("OLD_PASSWORD_ERROR", "旧密码错误");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userMapper.updateById(user);

        return Response.buildSuccess();
    }

    @Override
    public Response updateProfile(Long userId, String username, String email, String phone) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            return Response.buildFailure("USER_NOT_FOUND", "用户不存在");
        }

        if (username != null && !username.isBlank()) {
            user.setUsername(username.trim());
        }
        if (email != null) {
            user.setEmail(email.trim());
        }
        if (phone != null) {
            user.setPhone(phone.trim());
        }
        userMapper.updateById(user);

        return Response.buildSuccess();
    }
}
