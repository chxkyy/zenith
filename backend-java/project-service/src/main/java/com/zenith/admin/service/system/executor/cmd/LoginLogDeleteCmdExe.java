package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.mapper.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginLogDeleteCmdExe {

    private final LoginLogMapper loginLogMapper;

    public void execute(Long id) {
        loginLogMapper.deleteById(id);
    }
}
