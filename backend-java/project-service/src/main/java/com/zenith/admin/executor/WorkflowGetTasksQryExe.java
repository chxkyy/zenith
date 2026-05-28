package com.zenith.admin.executor;

import com.zenith.admin.TaskConvertor;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.dto.data.TaskDTO;
import com.zenith.admin.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkflowGetTasksQryExe {

    private final TaskMapper taskMapper;
    private final TaskConvertor taskConvertor;

    public List<TaskDTO> execute(Long processInstanceId, Integer nodeOrder) {
        List<TaskDO> taskDOs = taskMapper.selectByProcessInstanceAndNode(processInstanceId, nodeOrder);
        return taskConvertor.toDTOList(taskDOs);
    }
}
