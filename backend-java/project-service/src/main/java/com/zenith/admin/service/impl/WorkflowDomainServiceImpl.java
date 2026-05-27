package com.zenith.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zenith.admin.api.WorkflowDomainService;
import com.zenith.admin.dataobject.*;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.enums.ApproverTypeEnum;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowDomainServiceImpl implements WorkflowDomainService {

    private final TaskMapper taskMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final OrgMapper orgMapper;
    private final ApprovalRecordMapper approvalRecordMapper;

    @Override
    public List<Long> resolveApprovers(NodeTemplateDO node, Long initiatorId) {
        List<Long> approverIds = new ArrayList<>();

        if (ApproverTypeEnum.ROLE.getCode().equals(node.getApproverType())) {
            List<Long> roleIds = JSON.parseArray(node.getApproverValue(), Long.class);
            if (roleIds != null && !roleIds.isEmpty()) {
                LambdaQueryWrapper<UserRoleDO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(UserRoleDO::getRoleId, roleIds);
                List<UserRoleDO> userRoles = userRoleMapper.selectList(queryWrapper);
                approverIds = userRoles.stream()
                        .map(UserRoleDO::getUserId)
                        .distinct()
                        .collect(Collectors.toList());
            }
        } else if (ApproverTypeEnum.USER.getCode().equals(node.getApproverType())) {
            approverIds = JSON.parseArray(node.getApproverValue(), Long.class);
            if (approverIds == null) {
                approverIds = new ArrayList<>();
            }
        } else if (ApproverTypeEnum.SUPERIOR.getCode().equals(node.getApproverType())) {
            UserDO initiator = userMapper.selectById(initiatorId);
            if (initiator != null && initiator.getOrgId() != null) {
                OrgDO org = orgMapper.selectById(initiator.getOrgId());
                if (org != null && org.getParentId() != null) {
                    OrgDO parentOrg = orgMapper.selectById(org.getParentId());
                    if (parentOrg != null) {
                        LambdaQueryWrapper<UserDO> userQuery = new LambdaQueryWrapper<>();
                        userQuery.eq(UserDO::getOrgId, parentOrg.getId());
                        userQuery.eq(UserDO::getStatus, 1);
                        userQuery.last("LIMIT 1");
                        UserDO superior = userMapper.selectOne(userQuery);
                        if (superior != null) {
                            approverIds.add(superior.getId());
                        }
                    }
                }
            }
        }

        return approverIds;
    }

    @Override
    public void createTasksForNode(ProcessInstanceDO instance, NodeTemplateDO node) {
        List<Long> approverIds = resolveApprovers(node, instance.getInitiatorId());

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

    @Override
    public ApprovalRecordDO createApprovalRecord(ProcessInstanceDO instance, TaskDO task, Long operatorId,
            ActionTypeEnum actionType, String opinion) {
        UserDO operator = userMapper.selectById(operatorId);
        
        ApprovalRecordDO record = new ApprovalRecordDO();
        record.setProcessInstanceId(instance.getId());
        record.setNodeOrder(task != null ? task.getNodeOrder() : 0);
        record.setNodeName(task != null ? task.getNodeName() : "");
        record.setOperatorId(operatorId);
        record.setOperatorName(operator != null ? operator.getUsername() : "");
        record.setActionType(actionType.getCode());
        record.setOpinion(opinion);
        approvalRecordMapper.insert(record);
        
        return record;
    }

    @Override
    public List<TaskDO> getTasksByProcessInstanceAndNode(Long processInstanceId, Integer nodeOrder) {
        return taskMapper.selectByProcessInstanceAndNode(processInstanceId, nodeOrder);
    }

    @Override
    public boolean isAllOriginalTasksApproved(Long processInstanceId, Integer nodeOrder) {
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

    @Override
    public void terminatePendingTasks(Long processInstanceId) {
        LambdaUpdateWrapper<TaskDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TaskDO::getProcessInstanceId, processInstanceId)
                .eq(TaskDO::getStatus, TaskStatusEnum.PENDING.getCode())
                .set(TaskDO::getStatus, TaskStatusEnum.TERMINATED.getCode());
        taskMapper.update(null, updateWrapper);
    }

    @Override
    public void terminatePendingTasksExcept(Long processInstanceId, Long excludeTaskId) {
        LambdaUpdateWrapper<TaskDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TaskDO::getProcessInstanceId, processInstanceId)
                .eq(TaskDO::getStatus, TaskStatusEnum.PENDING.getCode())
                .ne(TaskDO::getId, excludeTaskId)
                .set(TaskDO::getStatus, TaskStatusEnum.TERMINATED.getCode());
        taskMapper.update(null, updateWrapper);
    }
}
