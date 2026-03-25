package com.zenith.admin.infrastructure.convertor;

import com.zenith.admin.domain.model.UserEntity;
import com.zenith.admin.dto.UserDTO;
import com.zenith.admin.infrastructure.dataobject.UserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConvertor {
    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    UserEntity toEntity(UserDO userDO);

    UserEntity toEntity(UserDTO userDTO);

    UserDO toDataObject(UserEntity userEntity);

    UserDTO toDTO(UserEntity userEntity);

    List<UserEntity> toEntityList(List<UserDO> userDOList);

    List<UserDTO> toDTOList(List<UserEntity> userEntityList);
}
