package com.zenith.admin.executor;

import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WorkflowCheckApprovalQryExe {

    private final TaskMapper taskMapper;

    public boolean execute(Long processInstanceId, Integer nodeOrder) {
        List<TaskDO> nodeTasks = taskMapper.selectByProcessInstanceAndNode(processInstanceId, nodeOrder);
        
        List<TaskDO> originalTasks = nodeTasks.stream()
                .filter(t -> t.getAssigneeType() == 1)
                .collect(Collectors.toList());
        
        boolean allApproved = originalTasks.stream()
                .allMatch(t -> TaskStatusEnum.APPROVED.getCode().equals(t.getStatus()) 
                        || TaskStatusEnum.COUNTERSIGNED.getCode().equals(t.getStatus()));
        
        if (!allApproved) {
            return false;
        }

        List<TaskDO> countersignTasks = nodeTasks.stream()
                .filter(t -> t.getAssigneeType() == 2 && t.getParentTaskId() != null)
                .collect(Collectors.toList());
        
        for (TaskDO csTask : countersignTasks) {
            if (TaskStatusEnum.PENDING.getCode().equals(csTask.getStatus())) {
                return false;
            }
        }
        
        return true;
    }
}
