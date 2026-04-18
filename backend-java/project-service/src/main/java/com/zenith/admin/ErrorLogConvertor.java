package com.zenith.admin;

import com.zenith.admin.domain.model.ErrorLogEntity;
import com.zenith.admin.dto.dataobject.ErrorLogDTO;
import com.zenith.admin.dataobject.ErrorLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ErrorLogConvertor {
    ErrorLogConvertor INSTANCE = Mappers.getMapper(ErrorLogConvertor.class);

    ErrorLogEntity toEntity(ErrorLogDO errorLogDO);
    ErrorLogEntity toEntity(ErrorLogDTO errorLogDTO);
    ErrorLogDO toDataObject(ErrorLogEntity errorLogEntity);
    ErrorLogDTO toDTO(ErrorLogEntity errorLogEntity);
    List<ErrorLogEntity> toEntityList(List<ErrorLogDO> errorLogDOList);
    List<ErrorLogDTO> toDTOList(List<ErrorLogEntity> errorLogEntityList);
}
