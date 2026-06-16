package com.zenith.admin.service.inst.impl;

import com.zenith.admin.api.inst.InstAdmissionLogService;
import com.zenith.admin.dto.inst.data.InstAdmissionLogDTO;
import com.zenith.admin.service.inst.executor.cmd.InstAdmissionLogAppendCmdExe;
import com.zenith.admin.service.inst.executor.qry.InstAdmissionLogListQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 准入申请操作日志服务实现类
 *
 * <p>记录和查询准入申请过程中的所有操作日志。</p>
 */
@Service
@RequiredArgsConstructor
public class InstAdmissionLogServiceImpl implements InstAdmissionLogService {

    private final InstAdmissionLogAppendCmdExe appendCmdExe;
    private final InstAdmissionLogListQryExe listQryExe;

    @Override
    public void append(Long admissionId, String action, Long operatorId, String operatorName, Object detail) {
        appendCmdExe.execute(admissionId, action, operatorId, operatorName, detail);
    }

    @Override
    public List<InstAdmissionLogDTO> listByAdmissionId(Long admissionId) {
        return listQryExe.execute(admissionId);
    }
}
