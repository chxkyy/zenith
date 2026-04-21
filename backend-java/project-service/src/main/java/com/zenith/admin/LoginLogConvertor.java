package com.zenith.admin;

import com.zenith.admin.dto.data.LoginLogDTO;
import com.zenith.admin.dataobject.LoginLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoginLogConvertor {
    LoginLogConvertor INSTANCE = Mappers.getMapper(LoginLogConvertor.class);

    LoginLogDO toDataObject(LoginLogDTO loginLogDTO);

    LoginLogDTO toDTO(LoginLogDO loginLogDO);

    List<LoginLogDTO> toDTOList(List<LoginLogDO> loginLogDOList);

    List<LoginLogDO> toDataObjectList(List<LoginLogDTO> loginLogDTOList);
}
