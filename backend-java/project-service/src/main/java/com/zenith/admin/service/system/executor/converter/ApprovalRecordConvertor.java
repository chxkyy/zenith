package com.zenith.admin.service.system.executor.converter;

import com.zenith.admin.dataobject.ApprovalRecordDO;
import com.zenith.admin.dto.system.data.ApprovalRecordDTO;
import com.zenith.admin.enums.ActionTypeEnum;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalRecordConvertor {
    ApprovalRecordConvertor INSTANCE = Mappers.getMapper(ApprovalRecordConvertor.class);

    @Mapping(target = "actionName", ignore = true)
    @Mapping(target = "operateTime", source = "operateTime", qualifiedByName = "localDateTimeToLong")
    ApprovalRecordDTO toDTO(ApprovalRecordDO approvalRecordDO);

    List<ApprovalRecordDTO> toDTOList(List<ApprovalRecordDO> approvalRecordDOList);

    @Named("localDateTimeToLong")
    default Long localDateTimeToLong(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toEpochSecond(ZoneOffset.ofHours(8));
    }

    @AfterMapping
    default void afterMapping(ApprovalRecordDO source, @MappingTarget ApprovalRecordDTO target) {
        if (source.getActionType() != null) {
            ActionTypeEnum actionTypeEnum = ActionTypeEnum.getByCode(source.getActionType());
            target.setActionName(actionTypeEnum != null ? actionTypeEnum.getName() : "");
        }
    }
}
