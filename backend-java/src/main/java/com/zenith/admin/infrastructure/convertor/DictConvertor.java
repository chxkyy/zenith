package com.zenith.admin.infrastructure.convertor;

import com.zenith.admin.domain.model.DictEntity;
import com.zenith.admin.dto.DictDTO;
import com.zenith.admin.infrastructure.dataobject.DictDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DictConvertor {
    DictConvertor INSTANCE = Mappers.getMapper(DictConvertor.class);

    DictEntity toEntity(DictDO dictDO);

    DictEntity toEntity(DictDTO dictDTO);

    DictDO toDataObject(DictEntity dictEntity);

    DictDTO toDTO(DictEntity dictEntity);

    List<DictEntity> toEntityList(List<DictDO> dictDOList);

    List<DictDTO> toDTOList(List<DictEntity> dictEntityList);
}
