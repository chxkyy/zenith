package com.zenith.admin.infrastructure.convertor;

import com.zenith.admin.domain.model.LoginLogEntity;
import com.zenith.admin.dto.LoginLogDTO;
import com.zenith.admin.infrastructure.dataobject.LoginLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoginLogConvertor {
    LoginLogConvertor INSTANCE = Mappers.getMapper(LoginLogConvertor.class);

    LoginLogEntity toEntity(LoginLogDO loginLogDO);
    LoginLogDO toDataObject(LoginLogEntity loginLogEntity);
    LoginLogDTO toDTO(LoginLogEntity loginLogEntity);
    List<LoginLogEntity> toEntityList(List<LoginLogDO> loginLogDOList);
    List<LoginLogDTO> toDTOList(List<LoginLogEntity> loginLogEntityList);
}
