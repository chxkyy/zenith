package com.zenith.admin.infrastructure.convertor;

import com.zenith.admin.domain.model.OrgEntity;
import com.zenith.admin.dto.OrgDTO;
import com.zenith.admin.infrastructure.dataobject.OrgDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrgConvertor {
    OrgConvertor INSTANCE = Mappers.getMapper(OrgConvertor.class);

    OrgEntity toEntity(OrgDO orgDO);

    OrgEntity toEntity(OrgDTO orgDTO);

    OrgDO toDataObject(OrgEntity orgEntity);

    OrgDTO toDTO(OrgEntity orgEntity);

    List<OrgEntity> toEntityList(List<OrgDO> orgDOList);

    List<OrgDTO> toDTOList(List<OrgEntity> orgEntityList);
}
