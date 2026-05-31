package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import com.zenith.admin.mapper.TaskMapper;
import com.zenith.admin.service.system.executor.qry.WorkflowResolveApproversQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkflowCreateTaskCmdExe {

    private final TaskMapper taskMapper;
    private final ProcessInstanceMapper processInstanceMapper;
    private final NodeTemplateMapper nodeTemplateMapper;
    private final WorkflowResolveApproversQryExe resolveApproversQryExe;

    public void execute(Long processInstanceId, Long nodeTemplateId) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }

        NodeTemplateDO node = nodeTemplateMapper.selectById(nodeTemplateId);
        if (node == null) {
            throw new RuntimeException("节点模板不存在");
        }

        List<Long> approverIds = resolveApproversQryExe.execute(nodeTemplateId, instance.getInitiatorId());

        for (Long approverId : approverIds) {
            TaskDO task = new TaskDO();
            task.setProcessInstanceId(instance.getId());
            task.setNodeOrder(node.getNodeOrder());
            task.setNodeName(node.getNodeName());
            task.setNodeType(node.getNodeType());
            task.setAssigneeId(approverId);
            task.setAssigneeType(1);
            task.setStatus(TaskStatusEnum.PENDING.getCode());
            task.setVersion(0);
            taskMapper.insert(task);
        }
    }
}
