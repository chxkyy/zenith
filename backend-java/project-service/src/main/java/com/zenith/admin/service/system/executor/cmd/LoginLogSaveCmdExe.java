package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dto.system.data.LoginLogDTO;
import com.zenith.admin.service.system.executor.converter.LoginLogConvertor;
import com.zenith.admin.dataobject.LoginLogDO;
import com.zenith.admin.mapper.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginLogSaveCmdExe {

    private final LoginLogMapper loginLogMapper;
    private final LoginLogConvertor loginLogConvertor;

    public void execute(LoginLogDTO loginLogDTO) {
        LoginLogDO loginLogDO = loginLogConvertor.toDataObject(loginLogDTO);
        loginLogMapper.insert(loginLogDO);
    }
}
