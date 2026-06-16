package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.util.PageResponseUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dataobject.*;
import com.zenith.admin.dto.system.data.TaskDTO;
import com.zenith.admin.dto.system.qry.TaskPageQuery;
import com.zenith.admin.enums.NodeTypeEnum;
import com.zenith.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskDonePageQryExe {

    private final TaskMapper taskMapper;
    private final ProcessInstanceMapper processInstanceMapper;
    private final ProcessTemplateMapper processTemplateMapper;
    private final UserMapper userMapper;

    public PageInfo<TaskDTO> execute(TaskPageQuery query, Long currentUserId) {
        PageInfo<TaskDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> taskMapper.selectDoneByAssignee(currentUserId));

        return PageResponseUtils.convert(pageInfo, list -> list.stream()
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
                .collect(Collectors.toList()));
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
