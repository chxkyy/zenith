package com.zenith.admin;

import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.domain.model.MenuEntity;
import com.zenith.admin.dto.dataobject.MenuDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-18T15:53:39+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class MenuConvertorImpl implements MenuConvertor {

    @Override
    public MenuEntity toEntity(MenuDO menuDO) {
        if ( menuDO == null ) {
            return null;
        }

        MenuEntity menuEntity = new MenuEntity();

        menuEntity.setId( menuDO.getId() );
        menuEntity.setParentId( menuDO.getParentId() );
        menuEntity.setName( menuDO.getName() );
        menuEntity.setPath( menuDO.getPath() );
        menuEntity.setComponent( menuDO.getComponent() );
        menuEntity.setIcon( menuDO.getIcon() );
        menuEntity.setSort( menuDO.getSort() );
        menuEntity.setType( menuDO.getType() );
        menuEntity.setPermission( menuDO.getPermission() );
        menuEntity.setCreatedAt( menuDO.getCreatedAt() );

        return menuEntity;
    }

    @Override
    public MenuEntity toEntity(MenuDTO menuDTO) {
        if ( menuDTO == null ) {
            return null;
        }

        MenuEntity menuEntity = new MenuEntity();

        menuEntity.setId( menuDTO.getId() );
        menuEntity.setParentId( menuDTO.getParentId() );
        menuEntity.setName( menuDTO.getName() );
        menuEntity.setPath( menuDTO.getPath() );
        menuEntity.setComponent( menuDTO.getComponent() );
        menuEntity.setIcon( menuDTO.getIcon() );
        menuEntity.setSort( menuDTO.getSort() );
        menuEntity.setType( menuDTO.getType() );
        menuEntity.setPermission( menuDTO.getPermission() );

        return menuEntity;
    }

    @Override
    public MenuDO toDataObject(MenuEntity menuEntity) {
        if ( menuEntity == null ) {
            return null;
        }

        MenuDO menuDO = new MenuDO();

        menuDO.setId( menuEntity.getId() );
        menuDO.setParentId( menuEntity.getParentId() );
        menuDO.setName( menuEntity.getName() );
        menuDO.setPath( menuEntity.getPath() );
        menuDO.setComponent( menuEntity.getComponent() );
        menuDO.setIcon( menuEntity.getIcon() );
        menuDO.setSort( menuEntity.getSort() );
        menuDO.setType( menuEntity.getType() );
        menuDO.setPermission( menuEntity.getPermission() );
        menuDO.setCreatedAt( menuEntity.getCreatedAt() );

        return menuDO;
    }

    @Override
    public MenuDTO toDTO(MenuEntity menuEntity) {
        if ( menuEntity == null ) {
            return null;
        }

        MenuDTO menuDTO = new MenuDTO();

        menuDTO.setId( menuEntity.getId() );
        menuDTO.setParentId( menuEntity.getParentId() );
        menuDTO.setName( menuEntity.getName() );
        menuDTO.setPath( menuEntity.getPath() );
        menuDTO.setComponent( menuEntity.getComponent() );
        menuDTO.setIcon( menuEntity.getIcon() );
        menuDTO.setSort( menuEntity.getSort() );
        menuDTO.setType( menuEntity.getType() );
        menuDTO.setPermission( menuEntity.getPermission() );

        return menuDTO;
    }

    @Override
    public List<MenuEntity> toEntityList(List<MenuDO> menuDOList) {
        if ( menuDOList == null ) {
            return null;
        }

        List<MenuEntity> list = new ArrayList<MenuEntity>( menuDOList.size() );
        for ( MenuDO menuDO : menuDOList ) {
            list.add( toEntity( menuDO ) );
        }

        return list;
    }

    @Override
    public List<MenuDTO> toDTOList(List<MenuEntity> menuEntityList) {
        if ( menuEntityList == null ) {
            return null;
        }

        List<MenuDTO> list = new ArrayList<MenuDTO>( menuEntityList.size() );
        for ( MenuEntity menuEntity : menuEntityList ) {
            list.add( toDTO( menuEntity ) );
        }

        return list;
    }
}
