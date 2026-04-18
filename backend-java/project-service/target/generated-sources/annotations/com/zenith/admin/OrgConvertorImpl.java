package com.zenith.admin;

import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.domain.model.OrgEntity;
import com.zenith.admin.dto.dataobject.OrgDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-18T16:08:19+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class OrgConvertorImpl implements OrgConvertor {

    @Override
    public OrgEntity toEntity(OrgDO orgDO) {
        if ( orgDO == null ) {
            return null;
        }

        OrgEntity orgEntity = new OrgEntity();

        orgEntity.setId( orgDO.getId() );
        orgEntity.setParentId( orgDO.getParentId() );
        orgEntity.setName( orgDO.getName() );
        orgEntity.setSort( orgDO.getSort() );
        orgEntity.setStatus( orgDO.getStatus() );
        orgEntity.setCreatedAt( orgDO.getCreatedAt() );

        return orgEntity;
    }

    @Override
    public OrgEntity toEntity(OrgDTO orgDTO) {
        if ( orgDTO == null ) {
            return null;
        }

        OrgEntity orgEntity = new OrgEntity();

        orgEntity.setId( orgDTO.getId() );
        orgEntity.setParentId( orgDTO.getParentId() );
        orgEntity.setName( orgDTO.getName() );
        orgEntity.setSort( orgDTO.getSort() );
        orgEntity.setStatus( orgDTO.getStatus() );

        return orgEntity;
    }

    @Override
    public OrgDO toDataObject(OrgEntity orgEntity) {
        if ( orgEntity == null ) {
            return null;
        }

        OrgDO orgDO = new OrgDO();

        orgDO.setId( orgEntity.getId() );
        orgDO.setParentId( orgEntity.getParentId() );
        orgDO.setName( orgEntity.getName() );
        orgDO.setSort( orgEntity.getSort() );
        orgDO.setStatus( orgEntity.getStatus() );
        orgDO.setCreatedAt( orgEntity.getCreatedAt() );

        return orgDO;
    }

    @Override
    public OrgDTO toDTO(OrgEntity orgEntity) {
        if ( orgEntity == null ) {
            return null;
        }

        OrgDTO orgDTO = new OrgDTO();

        orgDTO.setId( orgEntity.getId() );
        orgDTO.setParentId( orgEntity.getParentId() );
        orgDTO.setName( orgEntity.getName() );
        orgDTO.setSort( orgEntity.getSort() );
        orgDTO.setStatus( orgEntity.getStatus() );

        return orgDTO;
    }

    @Override
    public List<OrgEntity> toEntityList(List<OrgDO> orgDOList) {
        if ( orgDOList == null ) {
            return null;
        }

        List<OrgEntity> list = new ArrayList<OrgEntity>( orgDOList.size() );
        for ( OrgDO orgDO : orgDOList ) {
            list.add( toEntity( orgDO ) );
        }

        return list;
    }

    @Override
    public List<OrgDTO> toDTOList(List<OrgEntity> orgEntityList) {
        if ( orgEntityList == null ) {
            return null;
        }

        List<OrgDTO> list = new ArrayList<OrgDTO>( orgEntityList.size() );
        for ( OrgEntity orgEntity : orgEntityList ) {
            list.add( toDTO( orgEntity ) );
        }

        return list;
    }
}
