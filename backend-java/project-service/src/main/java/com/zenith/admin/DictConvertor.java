package com.zenith.admin;

import com.zenith.admin.domain.model.DictEntity;
import com.zenith.admin.domain.model.DictItemEntity;
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

    DictEntity toEntity(DictDO dictDO);

    DictEntity toEntity(DictDTO dictDTO);

    DictDO toDataObject(DictEntity dictEntity);

    DictDTO toDTO(DictEntity dictEntity);

    List<DictEntity> toEntityList(List<DictDO> dictDOList);

    List<DictDTO> toDTOList(List<DictEntity> dictEntityList);

    // 字典项转换方法
    DictItemEntity toItemEntity(DictItemDO dictItemDO);

    DictItemEntity toItemEntity(DictItemDTO dictItemDTO);

    DictItemDO toItemDataObject(DictItemEntity dictItemEntity);

    DictItemDTO toItemDTO(DictItemEntity dictItemEntity);

    List<DictItemEntity> toItemEntityList(List<DictItemDO> dictItemDOList);

    List<DictItemDTO> toItemDTOList(List<DictItemEntity> dictItemEntityList);
}
