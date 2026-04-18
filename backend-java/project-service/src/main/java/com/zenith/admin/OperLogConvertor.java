package com.zenith.admin;

import com.zenith.admin.domain.model.OperLogEntity;
import com.zenith.admin.dto.dataobject.OperLogDTO;
import com.zenith.admin.dataobject.OperLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OperLogConvertor {
    OperLogConvertor INSTANCE = Mappers.getMapper(OperLogConvertor.class);

    OperLogEntity toEntity(OperLogDO operLogDO);
    OperLogEntity toEntity(OperLogDTO operLogDTO);
    OperLogDO toDataObject(OperLogEntity operLogEntity);
    OperLogDTO toDTO(OperLogEntity operLogEntity);
    List<OperLogEntity> toEntityList(List<OperLogDO> operLogDOList);
    List<OperLogDTO> toDTOList(List<OperLogEntity> operLogEntityList);
}
