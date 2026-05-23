package com.zenith.admin.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.WorkflowService;
import com.zenith.admin.dataobject.ApprovalRecordDO;
import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.data.ApprovalRecordDTO;
import com.zenith.admin.dto.data.NodeProgressDTO;
import com.zenith.admin.dto.data.ProcessInstanceCreateCmd;
import com.zenith.admin.dto.data.ProcessInstanceDTO;
import com.zenith.admin.dto.data.ProcessInstancePageQuery;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.enums.ApproverTypeEnum;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.ApprovalRecordMapper;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.OrgMapper;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import com.zenith.admin.mapper.TaskMapper;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final ProcessTemplateMapper processTemplateMapper;
    private final NodeTemplateMapper nodeTemplateMapper;
    private final ProcessInstanceMapper processInstanceMapper;
    private final TaskMapper taskMapper;
    private final ApprovalRecordMapper approvalRecordMapper;
    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final OrgMapper orgMapper;

    @Override
    public Long saveDraft(ProcessInstanceCreateCmd cmd, Long currentUserId) {
        ProcessTemplateDO template = processTemplateMapper.selectById(cmd.getProcessTemplateId());
        if (template == null) {
            throw new RuntimeException("流程模板不存在");
        }

        ProcessInstanceDO instance = new ProcessInstanceDO();
        instance.setProcessNo(generateProcessNo(template.getCode()));
        instance.setProcessTemplateId(cmd.getProcessTemplateId());
        instance.setProcessTemplateVersion(template.getVersion());
        instance.setTitle(cmd.getTitle());
        instance.setAmount(cmd.getAmount());
        if (cmd.getStartDate() != null) {
            instance.setStartDate(LocalDate.ofEpochDay(cmd.getStartDate() / 86400));
        }
        if (cmd.getEndDate() != null) {
            instance.setEndDate(LocalDate.ofEpochDay(cmd.getEndDate() / 86400));
        }
        instance.setFormData(cmd.getFormData());
        instance.setStatus(ProcessStatusEnum.DRAFT.getCode());
        instance.setInitiatorId(currentUserId);
        processInstanceMapper.insert(instance);

        return instance.getId();
    }

    @Override
    @Transactional
    public void submit(Long processInstanceId, Long currentUserId) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }
        if (!ProcessStatusEnum.DRAFT.getCode().equals(instance.getStatus()) 
                && !ProcessStatusEnum.RETURNED.getCode().equals(instance.getStatus())) {
            throw new RuntimeException("当前状态不允许提交");
        }

        List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
        if (nodes.isEmpty()) {
            throw new RuntimeException("流程模板没有配置审批节点");
        }

        NodeTemplateDO firstNode = nodes.stream()
                .filter(n -> n.getNodeOrder() == 1)
                .findFirst()
                .orElse(null);
        if (firstNode == null) {
            throw new RuntimeException("流程模板配置错误");
        }

        instance.setStatus(ProcessStatusEnum.IN_PROGRESS.getCode());
        instance.setCurrentNodeOrder(1);
        processInstanceMapper.updateById(instance);

        createTasksForNode(instance, firstNode);

        UserDO operator = userMapper.selectById(currentUserId);
        ApprovalRecordDO record = new ApprovalRecordDO();
        record.setProcessInstanceId(processInstanceId);
        record.setNodeOrder(0);
        record.setNodeName("发起申请");
        record.setOperatorId(currentUserId);
        record.setOperatorName(operator != null ? operator.getUsername() : "");
        record.setActionType(ActionTypeEnum.SUBMIT.getCode());
        approvalRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void revoke(Long processInstanceId, Long currentUserId) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }
        if (!ProcessStatusEnum.IN_PROGRESS.getCode().equals(instance.getStatus())) {
            throw new RuntimeException("当前状态不允许撤销");
        }
        if (!instance.getInitiatorId().equals(currentUserId)) {
            throw new RuntimeException("只有发起人可以撤销");
        }

        instance.setStatus(ProcessStatusEnum.REVOKED.getCode());
        processInstanceMapper.updateById(instance);

        LambdaUpdateWrapper<TaskDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TaskDO::getProcessInstanceId, processInstanceId)
                .eq(TaskDO::getStatus, TaskStatusEnum.PENDING.getCode())
                .set(TaskDO::getStatus, TaskStatusEnum.TERMINATED.getCode());
        taskMapper.update(null, updateWrapper);

        UserDO operator = userMapper.selectById(currentUserId);
        ApprovalRecordDO record = new ApprovalRecordDO();
        record.setProcessInstanceId(processInstanceId);
        record.setNodeOrder(instance.getCurrentNodeOrder());
        record.setNodeName("");
        record.setOperatorId(currentUserId);
        record.setOperatorName(operator != null ? operator.getUsername() : "");
        record.setActionType(ActionTypeEnum.REVOKE.getCode());
        approvalRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void cancel(Long processInstanceId, Long currentUserId) {
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

        UserDO operator = userMapper.selectById(currentUserId);
        ApprovalRecordDO record = new ApprovalRecordDO();
        record.setProcessInstanceId(processInstanceId);
        record.setNodeOrder(0);
        record.setNodeName("");
        record.setOperatorId(currentUserId);
        record.setOperatorName(operator != null ? operator.getUsername() : "");
        record.setActionType(ActionTypeEnum.CANCEL.getCode());
        approvalRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void resubmit(Long processInstanceId, String formData, Long currentUserId) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }
        if (!ProcessStatusEnum.RETURNED.getCode().equals(instance.getStatus())) {
            throw new RuntimeException("当前状态不允许重新提交");
        }
        if (!instance.getInitiatorId().equals(currentUserId)) {
            throw new RuntimeException("只有发起人可以重新提交");
        }

        if (formData != null && !formData.isEmpty()) {
            instance.setFormData(formData);
        }
        instance.setStatus(ProcessStatusEnum.IN_PROGRESS.getCode());
        instance.setCurrentNodeOrder(1);
        processInstanceMapper.updateById(instance);

        List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
        NodeTemplateDO firstNode = nodes.stream()
                .filter(n -> n.getNodeOrder() == 1)
                .findFirst()
                .orElse(null);
        if (firstNode != null) {
            createTasksForNode(instance, firstNode);
        }

        UserDO operator = userMapper.selectById(currentUserId);
        ApprovalRecordDO record = new ApprovalRecordDO();
        record.setProcessInstanceId(processInstanceId);
        record.setNodeOrder(0);
        record.setNodeName("重新提交");
        record.setOperatorId(currentUserId);
        record.setOperatorName(operator != null ? operator.getUsername() : "");
        record.setActionType(ActionTypeEnum.RESUBMIT.getCode());
        approvalRecordMapper.insert(record);
    }

    @Override
    public ProcessInstanceDTO getDetail(Long id, Long currentUserId) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(id);
        if (instance == null) {
            return null;
        }

        ProcessInstanceDTO dto = convertToDTO(instance);

        ProcessTemplateDO template = processTemplateMapper.selectById(instance.getProcessTemplateId());
        if (template != null) {
            dto.setProcessTemplateName(template.getName());

            List<NodeTemplateDO> nodeTemplates = nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
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
                boolean hasPending = nodeTasks.stream().anyMatch(t -> TaskStatusEnum.PENDING.getCode().equals(t.getStatus()));

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

    @Override
    public PageInfo<ProcessInstanceDTO> pageMyProcess(ProcessInstancePageQuery query, Long currentUserId) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());

        LambdaQueryWrapper<ProcessInstanceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessInstanceDO::getInitiatorId, currentUserId);
        if (query.getStatus() != null) {
            queryWrapper.eq(ProcessInstanceDO::getStatus, query.getStatus());
        }
        queryWrapper.orderByDesc(ProcessInstanceDO::getCreatedTime);

        List<ProcessInstanceDO> list = processInstanceMapper.selectList(queryWrapper);
        PageInfo<ProcessInstanceDO> pageInfo = new PageInfo<>(list);

        List<ProcessInstanceDTO> dtos = pageInfo.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageInfo<ProcessInstanceDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    @Override
    public PageInfo<ProcessInstanceDTO> pageAllProcess(ProcessInstancePageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());

        LambdaQueryWrapper<ProcessInstanceDO> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getStatus() != null) {
            queryWrapper.eq(ProcessInstanceDO::getStatus, query.getStatus());
        }
        queryWrapper.orderByDesc(ProcessInstanceDO::getCreatedTime);

        List<ProcessInstanceDO> list = processInstanceMapper.selectList(queryWrapper);
        PageInfo<ProcessInstanceDO> pageInfo = new PageInfo<>(list);

        List<ProcessInstanceDTO> dtos = pageInfo.getList().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        PageInfo<ProcessInstanceDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    private void createTasksForNode(ProcessInstanceDO instance, NodeTemplateDO node) {
        List<Long> approverIds = getApprovers(node, instance.getInitiatorId());

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

    private List<Long> getApprovers(NodeTemplateDO node, Long initiatorId) {
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

    private String generateProcessNo(String code) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = getProcessTypePrefix(code);
        return prefix + dateStr + String.format("%04d", System.currentTimeMillis() % 10000);
    }

    private String getProcessTypePrefix(String code) {
        if (code == null) {
            return "LC";
        }
        if (code.contains("LEAVE")) {
            return "QJ";
        } else if (code.contains("EXPENSE")) {
            return "BX";
        } else if (code.contains("TRAVEL")) {
            return "CC";
        }
        return "LC";
    }

    private ProcessInstanceDTO convertToDTO(ProcessInstanceDO dO) {
        ProcessInstanceDTO dto = new ProcessInstanceDTO();
        dto.setId(dO.getId());
        dto.setProcessNo(dO.getProcessNo());
        dto.setProcessTemplateId(dO.getProcessTemplateId());
        dto.setTitle(dO.getTitle());
        dto.setAmount(dO.getAmount());
        if (dO.getStartDate() != null) {
            dto.setStartDate(dO.getStartDate().toEpochDay() * 86400);
        }
        if (dO.getEndDate() != null) {
            dto.setEndDate(dO.getEndDate().toEpochDay() * 86400);
        }
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
}

