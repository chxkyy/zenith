package com.zenith.admin;

import com.zenith.admin.domain.model.MenuEntity;
import com.zenith.admin.dto.dataobject.MenuDTO;
import com.zenith.admin.dataobject.MenuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuConvertor {
    MenuConvertor INSTANCE = Mappers.getMapper(MenuConvertor.class);

    MenuEntity toEntity(MenuDO menuDO);

    MenuEntity toEntity(MenuDTO menuDTO);

    MenuDO toDataObject(MenuEntity menuEntity);

    MenuDTO toDTO(MenuEntity menuEntity);

    List<MenuEntity> toEntityList(List<MenuDO> menuDOList);

    List<MenuDTO> toDTOList(List<MenuEntity> menuEntityList);
}
