package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUpdateProfileCmdExe {

    private final UserMapper userMapper;

    public void execute(Long userId, String username, String email, String phone) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("USER_NOT_FOUND", "用户不存在");
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
    }
}
