package com.zenith.admin;

import com.zenith.admin.dto.dataobject.DictDTO;
import com.zenith.admin.dto.dataobject.DictItemDTO;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dataobject.DictItemDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DictConvertor {
    DictConvertor INSTANCE = Mappers.getMapper(DictConvertor.class);

    DictDO toDataObject(DictDTO dictDTO);

    DictDTO toDTO(DictDO dictDO);

    List<DictDTO> toDTOList(List<DictDO> dictDOList);

    List<DictDO> toDataObjectList(List<DictDTO> dictDTOList);

    DictItemDO toItemDataObject(DictItemDTO dictItemDTO);

    DictItemDTO toItemDTO(DictItemDO dictItemDO);

    List<DictItemDTO> toItemDTOList(List<DictItemDO> dictItemDOList);

    List<DictItemDO> toItemDataObjectList(List<DictItemDTO> dictItemDTOList);
}
