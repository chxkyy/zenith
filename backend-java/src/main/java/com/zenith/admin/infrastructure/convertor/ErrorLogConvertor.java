package com.zenith.admin.infrastructure.convertor;

import com.zenith.admin.domain.model.ErrorLogEntity;
import com.zenith.admin.dto.ErrorLogDTO;
import com.zenith.admin.infrastructure.dataobject.ErrorLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ErrorLogConvertor {
    ErrorLogConvertor INSTANCE = Mappers.getMapper(ErrorLogConvertor.class);

    ErrorLogEntity toEntity(ErrorLogDO errorLogDO);
    ErrorLogDO toDataObject(ErrorLogEntity errorLogEntity);
    ErrorLogDTO toDTO(ErrorLogEntity errorLogEntity);
    List<ErrorLogEntity> toEntityList(List<ErrorLogDO> errorLogDOList);
    List<ErrorLogDTO> toDTOList(List<ErrorLogEntity> errorLogEntityList);
}
