package com.zenith.admin;

import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.domain.model.RoleEntity;
import com.zenith.admin.dto.dataobject.RoleDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-18T16:08:20+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class RoleConvertorImpl implements RoleConvertor {

    @Override
    public RoleEntity toEntity(RoleDO roleDO) {
        if ( roleDO == null ) {
            return null;
        }

        RoleEntity roleEntity = new RoleEntity();

        roleEntity.setId( roleDO.getId() );
        roleEntity.setName( roleDO.getName() );
        roleEntity.setCode( roleDO.getCode() );
        roleEntity.setDescription( roleDO.getDescription() );
        roleEntity.setStatus( roleDO.getStatus() );
        roleEntity.setMemberCount( roleDO.getMemberCount() );
        roleEntity.setCreatedAt( roleDO.getCreatedAt() );

        return roleEntity;
    }

    @Override
    public RoleEntity toEntity(RoleDTO roleDTO) {
        if ( roleDTO == null ) {
            return null;
        }

        RoleEntity roleEntity = new RoleEntity();

        roleEntity.setId( roleDTO.getId() );
        roleEntity.setName( roleDTO.getName() );
        roleEntity.setCode( roleDTO.getCode() );
        roleEntity.setDescription( roleDTO.getDescription() );
        roleEntity.setStatus( roleDTO.getStatus() );
        roleEntity.setMemberCount( roleDTO.getMemberCount() );

        return roleEntity;
    }

    @Override
    public RoleDO toDataObject(RoleEntity roleEntity) {
        if ( roleEntity == null ) {
            return null;
        }

        RoleDO roleDO = new RoleDO();

        roleDO.setId( roleEntity.getId() );
        roleDO.setName( roleEntity.getName() );
        roleDO.setCode( roleEntity.getCode() );
        roleDO.setDescription( roleEntity.getDescription() );
        roleDO.setStatus( roleEntity.getStatus() );
        roleDO.setMemberCount( roleEntity.getMemberCount() );
        roleDO.setCreatedAt( roleEntity.getCreatedAt() );

        return roleDO;
    }

    @Override
    public RoleDTO toDTO(RoleEntity roleEntity) {
        if ( roleEntity == null ) {
            return null;
        }

        RoleDTO roleDTO = new RoleDTO();

        roleDTO.setId( roleEntity.getId() );
        roleDTO.setName( roleEntity.getName() );
        roleDTO.setCode( roleEntity.getCode() );
        roleDTO.setDescription( roleEntity.getDescription() );
        roleDTO.setStatus( roleEntity.getStatus() );
        roleDTO.setMemberCount( roleEntity.getMemberCount() );

        return roleDTO;
    }

    @Override
    public List<RoleEntity> toEntityList(List<RoleDO> roleDOList) {
        if ( roleDOList == null ) {
            return null;
        }

        List<RoleEntity> list = new ArrayList<RoleEntity>( roleDOList.size() );
        for ( RoleDO roleDO : roleDOList ) {
            list.add( toEntity( roleDO ) );
        }

        return list;
    }

    @Override
    public List<RoleDTO> toDTOList(List<RoleEntity> roleEntityList) {
        if ( roleEntityList == null ) {
            return null;
        }

        List<RoleDTO> list = new ArrayList<RoleDTO>( roleEntityList.size() );
        for ( RoleEntity roleEntity : roleEntityList ) {
            list.add( toDTO( roleEntity ) );
        }

        return list;
    }
}
