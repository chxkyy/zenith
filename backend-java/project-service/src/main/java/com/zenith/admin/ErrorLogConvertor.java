package com.zenith.admin;

import com.zenith.admin.dto.data.ErrorLogDTO;
import com.zenith.admin.dataobject.ErrorLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ErrorLogConvertor {
    ErrorLogConvertor INSTANCE = Mappers.getMapper(ErrorLogConvertor.class);

    ErrorLogDO toDataObject(ErrorLogDTO errorLogDTO);

    ErrorLogDTO toDTO(ErrorLogDO errorLogDO);

    List<ErrorLogDTO> toDTOList(List<ErrorLogDO> errorLogDOList);

    List<ErrorLogDO> toDataObjectList(List<ErrorLogDTO> errorLogDTOList);
}
