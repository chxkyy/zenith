package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.UserDO;
import com.alibaba.cola.exception.BizException;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserChangeStatusCmdExe {

    private final UserMapper userMapper;

    public void execute(Long id, Integer status) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            if (0 == status && "admin".equals(userDO.getLoginId())) {
                throw new BizException("USER_STATUS_001", "超级管理员账号不可禁用");
            }
            userDO.setStatus(status);
            userMapper.updateById(userDO);
        }
    }
}
