package com.zenith.admin;

import com.zenith.admin.domain.model.LoginLogEntity;
import com.zenith.admin.dto.dataobject.LoginLogDTO;
import com.zenith.admin.dataobject.LoginLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoginLogConvertor {
    LoginLogConvertor INSTANCE = Mappers.getMapper(LoginLogConvertor.class);

    LoginLogEntity toEntity(LoginLogDO loginLogDO);
    LoginLogEntity toEntity(LoginLogDTO loginLogDTO);
    LoginLogDO toDataObject(LoginLogEntity loginLogEntity);
    LoginLogDTO toDTO(LoginLogEntity loginLogEntity);
    List<LoginLogEntity> toEntityList(List<LoginLogDO> loginLogDOList);
    List<LoginLogDTO> toDTOList(List<LoginLogEntity> loginLogEntityList);
}
