package com.zenith.admin.executor;

import com.zenith.admin.api.WorkflowDomainService;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowCancelCmdExe {

    private final ProcessInstanceMapper processInstanceMapper;
    private final WorkflowDomainService workflowDomainService;

    public void execute(Long processInstanceId, Long currentUserId) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }
        if (!ProcessStatusEnum.RETURNED.getCode().equals(instance.getStatus())) {
            throw new RuntimeException("当前状态不允许取消");
        }
        if (!instance.getInitiatorId().equals(currentUserId)) {
            throw new RuntimeException("只有发起人可以取消");
        }

        instance.setStatus(ProcessStatusEnum.CANCELLED.getCode());
        processInstanceMapper.updateById(instance);

        workflowDomainService.createApprovalRecord(processInstanceId, 0, "",
                null, currentUserId, ActionTypeEnum.CANCEL, null);
    }
}
