package com.zenith.admin.service.system.executor.converter;

import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dto.system.data.ProcessInstanceDTO;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import com.zenith.admin.mapper.UserMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ProcessInstanceConvertor {
    public static final ProcessInstanceConvertor INSTANCE = Mappers.getMapper(ProcessInstanceConvertor.class);

    protected UserMapper userMapper;
    protected ProcessTemplateMapper processTemplateMapper;

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void setProcessTemplateMapper(ProcessTemplateMapper processTemplateMapper) {
        this.processTemplateMapper = processTemplateMapper;
    }

    @BeanMapping(resultType = ProcessInstanceDTO.class)
    public abstract ProcessInstanceDTO toDTO(ProcessInstanceDO processInstanceDO);

    public abstract List<ProcessInstanceDTO> toDTOList(List<ProcessInstanceDO> processInstanceDOList);

    @AfterMapping
    protected void afterMapping(ProcessInstanceDO source, @MappingTarget ProcessInstanceDTO target) {
        if (source.getCreatedTime() != null) {
            target.setCreatedTime(source.getCreatedTime().toEpochSecond(ZoneOffset.ofHours(8)));
        }
        if (source.getStatus() != null) {
            ProcessStatusEnum statusEnum = ProcessStatusEnum.getByCode(source.getStatus());
            target.setStatusName(statusEnum != null ? statusEnum.getName() : "");
        }
        if (source.getInitiatorId() != null && userMapper != null) {
            UserDO initiator = userMapper.selectById(source.getInitiatorId());
            if (initiator != null) {
                target.setInitiatorName(initiator.getUsername());
            }
        }
        if (source.getProcessTemplateId() != null && processTemplateMapper != null) {
            ProcessTemplateDO template = processTemplateMapper.selectById(source.getProcessTemplateId());
            if (template != null) {
                target.setProcessTemplateName(template.getName());
            }
        }
    }

    protected Long map(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toEpochSecond(ZoneOffset.ofHours(8));
    }
}
