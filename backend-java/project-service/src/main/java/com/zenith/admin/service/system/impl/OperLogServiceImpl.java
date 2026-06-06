package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.system.OperLogService;
import com.zenith.admin.dto.data.OperLogDTO;
import com.zenith.admin.service.system.executor.cmd.OperLogDeleteCmdExe;
import com.zenith.admin.service.system.executor.cmd.OperLogSaveCmdExe;
import com.zenith.admin.service.system.executor.qry.OperLogListAllQryExe;
import com.zenith.admin.service.system.executor.qry.OperLogPageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperLogServiceImpl implements OperLogService {

    private final OperLogPageQryExe operLogPageQryExe;
    private final OperLogListAllQryExe operLogListAllQryExe;
    private final OperLogDeleteCmdExe operLogDeleteCmdExe;
    private final OperLogSaveCmdExe operLogSaveCmdExe;

    @Override
    public PageInfo<OperLogDTO> listByPage(int pageIndex, int pageSize, String operator, String module, String result) {
        return operLogPageQryExe.execute(pageIndex, pageSize, operator, module, result);
    }

    @Override
    public List<OperLogDTO> listAll(String operator, String module, String result) {
        return operLogListAllQryExe.execute(operator, module, result);
    }

    @Override
    public void delete(Long id) {
        operLogDeleteCmdExe.execute(id);
    }

    @Override
    public void save(OperLogDTO operLogDTO) {
        operLogSaveCmdExe.execute(operLogDTO);
    }
}
