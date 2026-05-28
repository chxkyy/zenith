package com.zenith.admin;

import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.data.TaskDTO;
import com.zenith.admin.enums.NodeTypeEnum;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import com.zenith.admin.mapper.UserMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class TaskConvertor {
    public static final TaskConvertor INSTANCE = Mappers.getMapper(TaskConvertor.class);

    protected ProcessInstanceMapper processInstanceMapper;
    protected ProcessTemplateMapper processTemplateMapper;
    protected UserMapper userMapper;

    public void setProcessInstanceMapper(ProcessInstanceMapper processInstanceMapper) {
        this.processInstanceMapper = processInstanceMapper;
    }

    public void setProcessTemplateMapper(ProcessTemplateMapper processTemplateMapper) {
        this.processTemplateMapper = processTemplateMapper;
    }

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Mapping(target = "processNo", ignore = true)
    @Mapping(target = "processTemplateName", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "nodeTypeName", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "initiatorName", ignore = true)
    @Mapping(target = "createdTime", source = "createdTime", qualifiedByName = "localDateTimeToLong")
    public abstract TaskDTO toDTO(TaskDO taskDO);

    public abstract List<TaskDTO> toDTOList(List<TaskDO> taskDOList);

    @Named("localDateTimeToLong")
    protected Long localDateTimeToLong(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toEpochSecond(ZoneOffset.ofHours(8));
    }

    @AfterMapping
    protected void afterMapping(TaskDO source, @MappingTarget TaskDTO target) {
        if (source.getNodeType() != null) {
            NodeTypeEnum nodeTypeEnum = NodeTypeEnum.getByCode(source.getNodeType());
            target.setNodeTypeName(nodeTypeEnum != null ? nodeTypeEnum.getName() : "");
        }

        if (source.getProcessInstanceId() != null && processInstanceMapper != null) {
            ProcessInstanceDO instance = processInstanceMapper.selectById(source.getProcessInstanceId());
            if (instance != null) {
                target.setProcessNo(instance.getProcessNo());
                target.setTitle(instance.getTitle());
                target.setInitiatorId(instance.getInitiatorId());

                if (instance.getInitiatorId() != null && userMapper != null) {
                    UserDO initiator = userMapper.selectById(instance.getInitiatorId());
                    if (initiator != null) {
                        target.setInitiatorName(initiator.getUsername());
                    }
                }

                if (instance.getProcessTemplateId() != null && processTemplateMapper != null) {
                    ProcessTemplateDO template = processTemplateMapper.selectById(instance.getProcessTemplateId());
                    if (template != null) {
                        target.setProcessTemplateName(template.getName());
                    }
                }
            }
        }
    }
}
