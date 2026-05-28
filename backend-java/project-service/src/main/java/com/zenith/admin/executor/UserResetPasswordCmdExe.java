package com.zenith.admin.executor;

import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserResetPasswordCmdExe {

    private final UserMapper userMapper;

    public void execute(Long id) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            userMapper.updateById(userDO);
        }
    }
}
