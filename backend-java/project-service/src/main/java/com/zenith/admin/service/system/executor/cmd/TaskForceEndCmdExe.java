package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.api.system.WorkflowDomainService;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskForceEndCmdExe {

    private final ProcessInstanceMapper processInstanceMapper;
    private final WorkflowDomainService workflowDomainService;

    public void execute(Long processInstanceId, Integer result, String reason) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(processInstanceId);
        if (instance == null) {
            throw new BizException("INSTANCE_NOT_FOUND", "流程实例不存在");
        }

        instance.setStatus(result == 1 ? ProcessStatusEnum.PASSED.getCode() : ProcessStatusEnum.CANCELLED.getCode());
        processInstanceMapper.updateById(instance);

        workflowDomainService.terminatePendingTasks(processInstanceId);

        workflowDomainService.createApprovalRecord(processInstanceId, instance.getCurrentNodeOrder(), "管理员强制结束",
                null, 0L, ActionTypeEnum.TERMINATE, reason);
    }
}
