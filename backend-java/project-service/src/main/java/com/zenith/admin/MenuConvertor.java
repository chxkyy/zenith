package com.zenith.admin;

import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dataobject.MenuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuConvertor {
    MenuConvertor INSTANCE = Mappers.getMapper(MenuConvertor.class);

    MenuDO toDataObject(MenuDTO menuDTO);

    MenuDTO toDTO(MenuDO menuDO);

    List<MenuDTO> toDTOList(List<MenuDO> menuDOList);

    List<MenuDO> toDataObjectList(List<MenuDTO> menuDTOList);
}
