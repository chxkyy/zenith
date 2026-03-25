package com.zenith.admin.infrastructure.convertor;

import com.zenith.admin.domain.model.RoleEntity;
import com.zenith.admin.dto.RoleDTO;
import com.zenith.admin.infrastructure.dataobject.RoleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleConvertor {
    RoleConvertor INSTANCE = Mappers.getMapper(RoleConvertor.class);

    RoleEntity toEntity(RoleDO roleDO);

    RoleEntity toEntity(RoleDTO roleDTO);

    RoleDO toDataObject(RoleEntity roleEntity);

    RoleDTO toDTO(RoleEntity roleEntity);

    List<RoleEntity> toEntityList(List<RoleDO> roleDOList);

    List<RoleDTO> toDTOList(List<RoleEntity> roleEntityList);
}
