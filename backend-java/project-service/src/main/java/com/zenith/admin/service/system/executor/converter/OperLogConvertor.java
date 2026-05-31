package com.zenith.admin.service.system.executor.converter;

import com.zenith.admin.dto.data.OperLogDTO;
import com.zenith.admin.dataobject.OperLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OperLogConvertor {
    OperLogConvertor INSTANCE = Mappers.getMapper(OperLogConvertor.class);

    OperLogDO toDataObject(OperLogDTO operLogDTO);

    OperLogDTO toDTO(OperLogDO operLogDO);

    List<OperLogDTO> toDTOList(List<OperLogDO> operLogDOList);

    List<OperLogDO> toDataObjectList(List<OperLogDTO> operLogDTOList);
}
