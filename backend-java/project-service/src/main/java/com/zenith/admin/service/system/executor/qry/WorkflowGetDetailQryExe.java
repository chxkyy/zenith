package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.dataobject.ApprovalRecordDO;
import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.data.ApprovalRecordDTO;
import com.zenith.admin.dto.data.NodeProgressDTO;
import com.zenith.admin.dto.data.ProcessInstanceDTO;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.ApprovalRecordMapper;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import com.zenith.admin.mapper.TaskMapper;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WorkflowGetDetailQryExe {

    private final ProcessInstanceMapper processInstanceMapper;
    private final ProcessTemplateMapper processTemplateMapper;
    private final NodeTemplateMapper nodeTemplateMapper;
    private final TaskMapper taskMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final UserMapper userMapper;

    public ProcessInstanceDTO execute(Long id, Long currentUserId) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(id);
        if (instance == null) {
            return null;
        }

        ProcessInstanceDTO dto = convertToDTO(instance);

        ProcessTemplateDO template = processTemplateMapper.selectById(instance.getProcessTemplateId());
        if (template != null) {
            dto.setProcessTemplateName(template.getName());

            List<NodeTemplateDO> nodeTemplates =
                    nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
            List<TaskDO> tasks = taskMapper.selectByProcessInstanceId(id);

            List<NodeProgressDTO> nodeProgressList = new ArrayList<>();
            for (NodeTemplateDO nodeTemplate : nodeTemplates) {
                NodeProgressDTO progress = new NodeProgressDTO();
                progress.setNodeOrder(nodeTemplate.getNodeOrder());
                progress.setNodeName(nodeTemplate.getNodeName());

                List<TaskDO> nodeTasks = tasks.stream()
                        .filter(t -> t.getNodeOrder().equals(nodeTemplate.getNodeOrder()) && t.getAssigneeType() == 1)
                        .collect(Collectors.toList());

                boolean allApproved = !nodeTasks.isEmpty() && nodeTasks.stream()
                        .allMatch(t -> TaskStatusEnum.APPROVED.getCode().equals(t.getStatus()));
                boolean hasPending =
                        nodeTasks.stream().anyMatch(t -> TaskStatusEnum.PENDING.getCode().equals(t.getStatus()));

                if (allApproved) {
                    progress.setStatus("completed");
                } else if (hasPending && nodeTemplate.getNodeOrder().equals(instance.getCurrentNodeOrder())) {
                    progress.setStatus("current");
                } else if (nodeTemplate.getNodeOrder() < instance.getCurrentNodeOrder()) {
                    progress.setStatus("completed");
                } else {
                    progress.setStatus("pending");
                }

                List<String> assigneeNames = new ArrayList<>();
                for (TaskDO task : nodeTasks) {
                    UserDO user = userMapper.selectById(task.getAssigneeId());
                    if (user != null) {
                        assigneeNames.add(user.getUsername());
                    }
                }
                progress.setAssigneeNames(String.join("、", assigneeNames));

                nodeProgressList.add(progress);
            }
            dto.setNodes(nodeProgressList);
        }

        List<ApprovalRecordDO> records = approvalRecordMapper.selectByProcessInstanceId(id);
        List<ApprovalRecordDTO> recordDTOs = records.stream()
                .map(this::convertRecordToDTO)
                .collect(Collectors.toList());
        dto.setApprovalRecords(recordDTOs);

        return dto;
    }

    private ApprovalRecordDTO convertRecordToDTO(ApprovalRecordDO dO) {
        ApprovalRecordDTO dto = new ApprovalRecordDTO();
        dto.setId(dO.getId());
        dto.setNodeOrder(dO.getNodeOrder());
        dto.setNodeName(dO.getNodeName());
        dto.setOperatorId(dO.getOperatorId());
        dto.setOperatorName(dO.getOperatorName());
        dto.setActionType(dO.getActionType());
        dto.setActionName(ActionTypeEnum.getByCode(dO.getActionType()) != null
                ? ActionTypeEnum.getByCode(dO.getActionType()).getName() : "");
        dto.setOpinion(dO.getOpinion());
        if (dO.getOperateTime() != null) {
            dto.setOperateTime(dO.getOperateTime().toEpochSecond(ZoneOffset.ofHours(8)));
        }
        return dto;
    }

    private ProcessInstanceDTO convertToDTO(ProcessInstanceDO dO) {
        ProcessInstanceDTO dto = new ProcessInstanceDTO();
        dto.setId(dO.getId());
        dto.setProcessNo(dO.getProcessNo());
        dto.setProcessTemplateId(dO.getProcessTemplateId());
        dto.setTitle(dO.getTitle());
        dto.setFormData(dO.getFormData());
        dto.setStatus(dO.getStatus());
        dto.setStatusName(ProcessStatusEnum.getByCode(dO.getStatus()) != null
                ? ProcessStatusEnum.getByCode(dO.getStatus()).getName() : "");
        dto.setInitiatorId(dO.getInitiatorId());
        dto.setCurrentNodeOrder(dO.getCurrentNodeOrder());
        if (dO.getCreatedTime() != null) {
            dto.setCreatedTime(dO.getCreatedTime().toEpochSecond(ZoneOffset.ofHours(8)));
        }

        UserDO initiator = userMapper.selectById(dO.getInitiatorId());
        if (initiator != null) {
            dto.setInitiatorName(initiator.getUsername());
        }

        ProcessTemplateDO template = processTemplateMapper.selectById(dO.getProcessTemplateId());
        if (template != null) {
            dto.setProcessTemplateName(template.getName());
        }

        return dto;
    }
}
