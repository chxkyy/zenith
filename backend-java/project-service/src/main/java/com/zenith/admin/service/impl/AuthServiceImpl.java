package com.zenith.admin.service.impl;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.AuthService;
import com.zenith.admin.api.TokenService;
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
    private final TokenService tokenService;
    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final ThreadLocal<String> TOKEN_HOLDER = new ThreadLocal<>();

    @Override
    public SingleResponse<UserDTO> login(String username, String password, String ip) {
        UserDO user = userMapper.selectOne(
                new LambdaQueryWrapper<UserDO>().eq(UserDO::getUsername, username)
        );

        if (user == null) {
            return SingleResponse.buildFailure("USER_NOT_FOUND", "用户名不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return SingleResponse.buildFailure("PASSWORD_ERROR", "密码错误");
        }

        if (user.getStatus() != 1) {
            return SingleResponse.buildFailure("USER_DISABLED", "用户已禁用");
        }

        String token = tokenService.generateToken(user.getId(), ip);
        TOKEN_HOLDER.set(token);

        UserDTO userDTO = userService.getById(user.getId());
        return SingleResponse.of(userDTO);
    }

    @Override
    public String getTokenAndClean() {
        String token = TOKEN_HOLDER.get();
        TOKEN_HOLDER.remove();
        return token;
    }

    @Override
    public Response logout(String token) {
        if (token != null && !token.isEmpty()) {
            tokenService.deleteToken(token);
        }
        return Response.buildSuccess();
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
}