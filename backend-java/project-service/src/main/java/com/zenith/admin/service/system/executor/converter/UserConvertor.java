package com.zenith.admin.service.system.executor.converter;

import com.zenith.admin.dto.system.data.UserDTO;
import com.zenith.admin.dataobject.UserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserConvertor {
    UserConvertor INSTANCE = Mappers.getMapper(UserConvertor.class);

    UserDTO toDTO(UserDO userDO);

    UserDO toDataObject(UserDTO userDTO);

    List<UserDTO> toDTOList(List<UserDO> userDOList);
}
