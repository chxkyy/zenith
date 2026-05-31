package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.ApprovalRecordDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.mapper.ApprovalRecordMapper;
import com.zenith.admin.mapper.TaskMapper;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowCreateApprovalRecordCmdExe {

    private final ApprovalRecordMapper approvalRecordMapper;
    private final UserMapper userMapper;
    private final TaskMapper taskMapper;

    public void execute(Long processInstanceId, Integer nodeOrder, String nodeName,
            Long taskId, Long operatorId, ActionTypeEnum actionType, String opinion) {
        UserDO operator = userMapper.selectById(operatorId);
        
        String actualNodeName = nodeName;
        if (taskId != null) {
            TaskDO task = taskMapper.selectById(taskId);
            if (task != null) {
                actualNodeName = task.getNodeName();
                if (nodeOrder == null) {
                    nodeOrder = task.getNodeOrder();
                }
            }
        }

        ApprovalRecordDO record = new ApprovalRecordDO();
        record.setProcessInstanceId(processInstanceId);
        record.setNodeOrder(nodeOrder != null ? nodeOrder : 0);
        record.setNodeName(actualNodeName != null ? actualNodeName : "");
        record.setOperatorId(operatorId);
        record.setOperatorName(operator != null ? operator.getUsername() : "");
        record.setActionType(actionType.getCode());
        record.setOpinion(opinion);
        approvalRecordMapper.insert(record);
    }
}
