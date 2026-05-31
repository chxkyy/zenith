package com.zenith.admin.service.system.executor.converter;

import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.dataobject.FunctionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FunctionConvertor {
    FunctionConvertor INSTANCE = Mappers.getMapper(FunctionConvertor.class);

    FunctionDO toDataObject(FunctionDTO functionDTO);

    FunctionDTO toDTO(FunctionDO functionDO);

    List<FunctionDTO> toDTOList(List<FunctionDO> functionDOList);

    List<FunctionDO> toDataObjectList(List<FunctionDTO> functionDTOList);
}
