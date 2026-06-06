package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.system.LoginLogService;
import com.zenith.admin.dto.data.LoginLogDTO;
import com.zenith.admin.service.system.executor.cmd.LoginLogDeleteCmdExe;
import com.zenith.admin.service.system.executor.cmd.LoginLogSaveCmdExe;
import com.zenith.admin.service.system.executor.cmd.LoginLogUpdateLogoutAtCmdExe;
import com.zenith.admin.service.system.executor.qry.LoginLogPageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogPageQryExe loginLogPageQryExe;
    private final LoginLogDeleteCmdExe loginLogDeleteCmdExe;
    private final LoginLogSaveCmdExe loginLogSaveCmdExe;
    private final LoginLogUpdateLogoutAtCmdExe loginLogUpdateLogoutAtCmdExe;

    @Override
    public void delete(Long id) {
        loginLogDeleteCmdExe.execute(id);
    }

    @Override
    public PageInfo<LoginLogDTO> listByPage(int pageIndex, int pageSize, String username, String status, String ip) {
        return loginLogPageQryExe.execute(pageIndex, pageSize, username, status, ip);
    }

    @Override
    public void save(LoginLogDTO loginLogDTO) {
        loginLogSaveCmdExe.execute(loginLogDTO);
    }

    @Override
    public void updateLogoutAt(String username) {
        loginLogUpdateLogoutAtCmdExe.execute(username);
    }
}
