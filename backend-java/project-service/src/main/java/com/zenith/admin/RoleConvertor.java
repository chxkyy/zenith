package com.zenith.admin;

import com.zenith.admin.dto.dataobject.RoleDTO;
import com.zenith.admin.dataobject.RoleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleConvertor {
    RoleConvertor INSTANCE = Mappers.getMapper(RoleConvertor.class);

    RoleDO toDataObject(RoleDTO roleDTO);

    RoleDTO toDTO(RoleDO roleDO);

    List<RoleDTO> toDTOList(List<RoleDO> roleDOList);

    List<RoleDO> toDataObjectList(List<RoleDTO> roleDTOList);
}
