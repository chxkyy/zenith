package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.system.ErrorLogService;
import com.zenith.admin.dto.system.data.ErrorLogDTO;
import com.zenith.admin.service.system.executor.cmd.ErrorLogClearCmdExe;
import com.zenith.admin.service.system.executor.cmd.ErrorLogDeleteCmdExe;
import com.zenith.admin.service.system.executor.cmd.ErrorLogSaveCmdExe;
import com.zenith.admin.service.system.executor.qry.ErrorLogPageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogPageQryExe errorLogPageQryExe;
    private final ErrorLogDeleteCmdExe errorLogDeleteCmdExe;
    private final ErrorLogClearCmdExe errorLogClearCmdExe;
    private final ErrorLogSaveCmdExe errorLogSaveCmdExe;

    @Override
    public PageInfo<ErrorLogDTO> listByPage(int pageIndex, int pageSize, String module, String ip) {
        return errorLogPageQryExe.execute(pageIndex, pageSize, module, ip);
    }

    @Override
    public void delete(Long id) {
        errorLogDeleteCmdExe.execute(id);
    }

    @Override
    public void clear(int months) {
        errorLogClearCmdExe.execute(months);
    }

    @Override
    public void save(ErrorLogDTO errorLogDTO) {
        errorLogSaveCmdExe.execute(errorLogDTO);
    }
}
