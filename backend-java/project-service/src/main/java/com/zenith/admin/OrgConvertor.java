package com.zenith.admin;

import com.zenith.admin.dto.data.OrgDTO;
import com.zenith.admin.dataobject.OrgDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrgConvertor {
    OrgConvertor INSTANCE = Mappers.getMapper(OrgConvertor.class);

    OrgDO toDataObject(OrgDTO orgDTO);

    OrgDTO toDTO(OrgDO orgDO);

    List<OrgDTO> toDTOList(List<OrgDO> orgDOList);

    List<OrgDO> toDataObjectList(List<OrgDTO> orgDTOList);
}
