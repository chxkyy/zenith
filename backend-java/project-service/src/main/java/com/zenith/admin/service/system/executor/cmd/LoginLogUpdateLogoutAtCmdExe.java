package com.zenith.admin.service.system.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.LoginLogDO;
import com.zenith.admin.mapper.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginLogUpdateLogoutAtCmdExe {

    private final LoginLogMapper loginLogMapper;

    public void execute(String username) {
        LambdaQueryWrapper<LoginLogDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginLogDO::getUsername, username)
                .eq(LoginLogDO::getStatus, "成功")
                .isNull(LoginLogDO::getLogoutAt)
                .orderByDesc(LoginLogDO::getLoginAt)
                .last("LIMIT 1");
        LoginLogDO loginLogDO = loginLogMapper.selectOne(queryWrapper);
        if (loginLogDO != null) {
            loginLogDO.setLogoutAt(LocalDateTime.now());
            loginLogMapper.updateById(loginLogDO);
        }
    }
}
