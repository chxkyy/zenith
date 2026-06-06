package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserChangePasswordCmdExe {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void execute(Long userId, String oldPassword, String newPassword) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("USER_NOT_FOUND", "用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BizException("OLD_PASSWORD_ERROR", "旧密码错误");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userMapper.updateById(user);
    }
}
