package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.TaskService;
import com.zenith.admin.api.WorkflowDomainService;
import com.zenith.admin.dataobject.*;
import com.zenith.admin.dto.data.*;
import com.zenith.admin.enums.*;
import com.zenith.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final ProcessInstanceMapper processInstanceMapper;
    private final ProcessTemplateMapper processTemplateMapper;
    private final NodeTemplateMapper nodeTemplateMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final WorkflowDomainService workflowDomainService;

    @Override
    public PageInfo<TaskDTO> pageTodo(TaskPageQuery query, Long currentUserId) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());

        List<TaskDO> tasks = taskMapper.selectPendingByAssignee(currentUserId);
        
        List<TaskDTO> dtos = tasks.stream()
                .filter(t -> {
                    if (query.getProcessTemplateName() != null && !query.getProcessTemplateName().isEmpty()) {
                        ProcessInstanceDO instance = processInstanceMapper.selectById(t.getProcessInstanceId());
                        if (instance != null) {
                            ProcessTemplateDO template = processTemplateMapper.selectById(instance.getProcessTemplateId());
                            if (template != null && !template.getName().contains(query.getProcessTemplateName())) {
                                return false;
                            }
                        }
                    }
                    return true;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageInfo<TaskDTO> result = new PageInfo<>();
        result.setTotal(dtos.size());
        result.setPageNum(query.getPageIndex());
        result.setPageSize(query.getPageSize());
        result.setList(dtos);
        return result;
    }

    @Override
    public PageInfo<TaskDTO> pageDone(TaskPageQuery query, Long currentUserId) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());

        List<TaskDO> tasks = taskMapper.selectDoneByAssignee(currentUserId);
        
        List<TaskDTO> dtos = tasks.stream()
                .filter(t -> {
                    if (query.getProcessTemplateName() != null && !query.getProcessTemplateName().isEmpty()) {
                        ProcessInstanceDO instance = processInstanceMapper.selectById(t.getProcessInstanceId());
                        if (instance != null) {
                            ProcessTemplateDO template = processTemplateMapper.selectById(instance.getProcessTemplateId());
                            if (template != null && !template.getName().contains(query.getProcessTemplateName())) {
                                return false;
                            }
                        }
                    }
                    return true;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageInfo<TaskDTO> result = new PageInfo<>();
        result.setTotal(dtos.size());
        result.setPageNum(query.getPageIndex());
        result.setPageSize(query.getPageSize());
        result.setList(dtos);
        return result;
    }

    @Override
    @Transactional
    public void approve(TaskApproveCmd cmd, Long currentUserId) {
        TaskDO task = taskMapper.selectById(cmd.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!TaskStatusEnum.PENDING.getCode().equals(task.getStatus())) {
            throw new RuntimeException("任务已被处理");
        }
        if (!task.getAssigneeId().equals(currentUserId)) {
            throw new RuntimeException("无权处理此任务");
        }

        task.setStatus(TaskStatusEnum.APPROVED.getCode());
        task.setOpinion(cmd.getOpinion());
        task.setActionType(ActionTypeEnum.APPROVE.getCode());
        task.setActionTime(LocalDateTime.now());
        int rows = taskMapper.updateById(task);
        if (rows == 0) {
            throw new RuntimeException("任务已被其他人处理");
        }

        ProcessInstanceDO instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        workflowDomainService.createApprovalRecord(instance, task, currentUserId, ActionTypeEnum.APPROVE, cmd.getOpinion());

        processAfterApprove(instance, task);
    }

    @Override
    @Transactional
    public void reject(TaskRejectCmd cmd, Long currentUserId) {
        TaskDO task = taskMapper.selectById(cmd.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!TaskStatusEnum.PENDING.getCode().equals(task.getStatus())) {
            throw new RuntimeException("任务已被处理");
        }
        if (!task.getAssigneeId().equals(currentUserId)) {
            throw new RuntimeException("无权处理此任务");
        }

        task.setStatus(TaskStatusEnum.REJECTED.getCode());
        task.setOpinion(cmd.getOpinion());
        task.setActionType(ActionTypeEnum.REJECT.getCode());
        task.setActionTime(LocalDateTime.now());
        taskMapper.updateById(task);

        ProcessInstanceDO instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        workflowDomainService.createApprovalRecord(instance, task, currentUserId, ActionTypeEnum.REJECT, cmd.getOpinion());

        if (NodeTypeEnum.COUNTERSIGN.getCode().equals(task.getNodeType())) {
            LambdaUpdateWrapper<TaskDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TaskDO::getProcessInstanceId, task.getProcessInstanceId())
                    .eq(TaskDO::getNodeOrder, task.getNodeOrder())
                    .eq(TaskDO::getStatus, TaskStatusEnum.PENDING.getCode())
                    .set(TaskDO::getStatus, TaskStatusEnum.TERMINATED.getCode());
            taskMapper.update(null, updateWrapper);
        }

        if (task.getNodeOrder() == 1) {
            instance.setStatus(ProcessStatusEnum.RETURNED.getCode());
            instance.setCurrentNodeOrder(null);
        } else {
            List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
            int prevNodeOrder = task.getNodeOrder() - 1;
            NodeTemplateDO prevNode = nodes.stream()
                    .filter(n -> n.getNodeOrder() == prevNodeOrder)
                    .findFirst()
                    .orElse(null);
            
            if (prevNode != null) {
                instance.setCurrentNodeOrder(prevNodeOrder);
                workflowDomainService.createTasksForNode(instance, prevNode);
            }
        }
        processInstanceMapper.updateById(instance);
    }

    @Override
    @Transactional
    public void countersign(TaskCountersignCmd cmd, Long currentUserId) {
        TaskDO task = taskMapper.selectById(cmd.getTaskId());
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!TaskStatusEnum.PENDING.getCode().equals(task.getStatus())) {
            throw new RuntimeException("任务已被处理");
        }
        if (!task.getAssigneeId().equals(currentUserId)) {
            throw new RuntimeException("无权处理此任务");
        }

        task.setStatus(TaskStatusEnum.COUNTERSIGNED.getCode());
        task.setOpinion(cmd.getOpinion());
        task.setActionType(ActionTypeEnum.COUNTERSIGN.getCode());
        task.setActionTime(LocalDateTime.now());
        taskMapper.updateById(task);

        ProcessInstanceDO instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        workflowDomainService.createApprovalRecord(instance, task, currentUserId, ActionTypeEnum.COUNTERSIGN, cmd.getOpinion());

        createCountersignTasks(cmd, task, instance);
    }

    private void createCountersignTasks(TaskCountersignCmd cmd, TaskDO task, ProcessInstanceDO instance) {
        List<Long> approverIds = cmd.getApproverIds();
        if (ApproverTypeEnum.ROLE.getCode().equals(cmd.getApproverType())) {
            LambdaQueryWrapper<UserRoleDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(UserRoleDO::getRoleId, approverIds);
            List<UserRoleDO> userRoles = userRoleMapper.selectList(queryWrapper);
            approverIds = userRoles.stream()
                    .map(UserRoleDO::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
        }

        for (Long approverId : approverIds) {
            TaskDO newTask = new TaskDO();
            newTask.setProcessInstanceId(instance.getId());
            newTask.setNodeOrder(task.getNodeOrder());
            newTask.setNodeName(task.getNodeName() + "(加签)");
            newTask.setNodeType(task.getNodeType());
            newTask.setAssigneeId(approverId);
            newTask.setAssigneeType(2);
            newTask.setParentTaskId(task.getId());
            newTask.setStatus(TaskStatusEnum.PENDING.getCode());
            newTask.setVersion(0);
            taskMapper.insert(newTask);
        }
    }

    @Override
    @Transactional
    public void terminate(Long taskId, String opinion, Long currentUserId) {
        TaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!TaskStatusEnum.PENDING.getCode().equals(task.getStatus())) {
            throw new RuntimeException("任务已被处理");
        }
        if (!task.getAssigneeId().equals(currentUserId)) {
            throw new RuntimeException("无权处理此任务");
        }

        task.setStatus(TaskStatusEnum.TERMINATED.getCode());
        task.setOpinion(opinion);
        task.setActionType(ActionTypeEnum.TERMINATE.getCode());
        task.setActionTime(LocalDateTime.now());
        taskMapper.updateById(task);

        ProcessInstanceDO instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        instance.setStatus(ProcessStatusEnum.PASSED.getCode());
        processInstanceMapper.updateById(instance);

        workflowDomainService.terminatePendingTasks(task.getProcessInstanceId());
        workflowDomainService.createApprovalRecord(instance, task, currentUserId, ActionTypeEnum.TERMINATE, opinion);
    }

    @Override
    @Transactional
    public void forceEnd(Long processInstanceId, Integer result, String reason) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }

        instance.setStatus(result == 1 ? ProcessStatusEnum.PASSED.getCode() : ProcessStatusEnum.CANCELLED.getCode());
        processInstanceMapper.updateById(instance);

        workflowDomainService.terminatePendingTasks(processInstanceId);

        ApprovalRecordDO record = new ApprovalRecordDO();
        record.setProcessInstanceId(processInstanceId);
        record.setNodeOrder(instance.getCurrentNodeOrder() != null ? instance.getCurrentNodeOrder() : 0);
        record.setNodeName("管理员强制结束");
        record.setOperatorId(0L);
        record.setOperatorName("系统管理员");
        record.setActionType(ActionTypeEnum.TERMINATE.getCode());
        record.setOpinion(reason);
        approvalRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void transferTask(Long taskId, Long targetUserId) {
        TaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!TaskStatusEnum.PENDING.getCode().equals(task.getStatus())) {
            throw new RuntimeException("任务已被处理");
        }

        task.setAssigneeId(targetUserId);
        taskMapper.updateById(task);
    }

    @Override
    @Transactional
    public void assignApprover(Long taskId, Long approverId) {
        TaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (!TaskStatusEnum.PENDING.getCode().equals(task.getStatus())) {
            throw new RuntimeException("任务已被处理");
        }

        task.setAssigneeId(approverId);
        taskMapper.updateById(task);
    }

    private void processAfterApprove(ProcessInstanceDO instance, TaskDO currentTask) {
        if (NodeTypeEnum.COUNTERSIGN.getCode().equals(currentTask.getNodeType())) {
            if (!workflowDomainService.isAllOriginalTasksApproved(instance.getId(), currentTask.getNodeOrder())) {
                return;
            }
        }

        List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
        int nextNodeOrder = currentTask.getNodeOrder() + 1;
        
        NodeTemplateDO nextNode = nodes.stream()
                .filter(n -> n.getNodeOrder() == nextNodeOrder)
                .findFirst()
                .orElse(null);

        if (nextNode == null) {
            instance.setStatus(ProcessStatusEnum.PASSED.getCode());
            processInstanceMapper.updateById(instance);
        } else {
            instance.setCurrentNodeOrder(nextNodeOrder);
            processInstanceMapper.updateById(instance);
            workflowDomainService.createTasksForNode(instance, nextNode);
        }
    }

    private TaskDTO convertToDTO(TaskDO dO) {
        TaskDTO dto = new TaskDTO();
        dto.setId(dO.getId());
        dto.setProcessInstanceId(dO.getProcessInstanceId());
        dto.setNodeOrder(dO.getNodeOrder());
        dto.setNodeName(dO.getNodeName());
        dto.setNodeType(dO.getNodeType());
        dto.setNodeTypeName(NodeTypeEnum.getByCode(dO.getNodeType()) != null 
                ? NodeTypeEnum.getByCode(dO.getNodeType()).getName() : "");
        if (dO.getCreatedTime() != null) {
            dto.setCreatedTime(dO.getCreatedTime().toEpochSecond(ZoneOffset.ofHours(8)));
        }

        ProcessInstanceDO instance = processInstanceMapper.selectById(dO.getProcessInstanceId());
        if (instance != null) {
            dto.setProcessNo(instance.getProcessNo());
            dto.setTitle(instance.getTitle());
            dto.setInitiatorId(instance.getInitiatorId());

            UserDO initiator = userMapper.selectById(instance.getInitiatorId());
            if (initiator != null) {
                dto.setInitiatorName(initiator.getUsername());
            }

            ProcessTemplateDO template = processTemplateMapper.selectById(instance.getProcessTemplateId());
            if (template != null) {
                dto.setProcessTemplateName(template.getName());
            }
        }

        return dto;
    }
}
