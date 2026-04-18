package com.zenith.admin;

import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.domain.model.DictEntity;
import com.zenith.admin.domain.model.DictItemEntity;
import com.zenith.admin.dto.dataobject.DictDTO;
import com.zenith.admin.dto.dataobject.DictItemDTO;
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
public class DictConvertorImpl implements DictConvertor {

    @Override
    public DictEntity toEntity(DictDO dictDO) {
        if ( dictDO == null ) {
            return null;
        }

        DictEntity dictEntity = new DictEntity();

        dictEntity.setId( dictDO.getId() );
        dictEntity.setName( dictDO.getName() );
        dictEntity.setType( dictDO.getType() );
        dictEntity.setStatus( dictDO.getStatus() );
        dictEntity.setRemark( dictDO.getRemark() );
        dictEntity.setCreatedAt( dictDO.getCreatedAt() );
        dictEntity.setUpdatedAt( dictDO.getUpdatedAt() );

        return dictEntity;
    }

    @Override
    public DictEntity toEntity(DictDTO dictDTO) {
        if ( dictDTO == null ) {
            return null;
        }

        DictEntity dictEntity = new DictEntity();

        dictEntity.setId( dictDTO.getId() );
        dictEntity.setName( dictDTO.getName() );
        dictEntity.setType( dictDTO.getType() );
        dictEntity.setStatus( dictDTO.getStatus() );
        dictEntity.setRemark( dictDTO.getRemark() );
        dictEntity.setCreatedAt( dictDTO.getCreatedAt() );
        dictEntity.setUpdatedAt( dictDTO.getUpdatedAt() );

        return dictEntity;
    }

    @Override
    public DictDO toDataObject(DictEntity dictEntity) {
        if ( dictEntity == null ) {
            return null;
        }

        DictDO dictDO = new DictDO();

        dictDO.setId( dictEntity.getId() );
        dictDO.setName( dictEntity.getName() );
        dictDO.setType( dictEntity.getType() );
        dictDO.setStatus( dictEntity.getStatus() );
        dictDO.setRemark( dictEntity.getRemark() );
        dictDO.setCreatedAt( dictEntity.getCreatedAt() );
        dictDO.setUpdatedAt( dictEntity.getUpdatedAt() );

        return dictDO;
    }

    @Override
    public DictDTO toDTO(DictEntity dictEntity) {
        if ( dictEntity == null ) {
            return null;
        }

        DictDTO dictDTO = new DictDTO();

        dictDTO.setId( dictEntity.getId() );
        dictDTO.setName( dictEntity.getName() );
        dictDTO.setType( dictEntity.getType() );
        dictDTO.setStatus( dictEntity.getStatus() );
        dictDTO.setRemark( dictEntity.getRemark() );
        dictDTO.setCreatedAt( dictEntity.getCreatedAt() );
        dictDTO.setUpdatedAt( dictEntity.getUpdatedAt() );

        return dictDTO;
    }

    @Override
    public List<DictEntity> toEntityList(List<DictDO> dictDOList) {
        if ( dictDOList == null ) {
            return null;
        }

        List<DictEntity> list = new ArrayList<DictEntity>( dictDOList.size() );
        for ( DictDO dictDO : dictDOList ) {
            list.add( toEntity( dictDO ) );
        }

        return list;
    }

    @Override
    public List<DictDTO> toDTOList(List<DictEntity> dictEntityList) {
        if ( dictEntityList == null ) {
            return null;
        }

        List<DictDTO> list = new ArrayList<DictDTO>( dictEntityList.size() );
        for ( DictEntity dictEntity : dictEntityList ) {
            list.add( toDTO( dictEntity ) );
        }

        return list;
    }

    @Override
    public DictItemEntity toItemEntity(DictItemDO dictItemDO) {
        if ( dictItemDO == null ) {
            return null;
        }

        DictItemEntity dictItemEntity = new DictItemEntity();

        dictItemEntity.setId( dictItemDO.getId() );
        dictItemEntity.setType( dictItemDO.getType() );
        dictItemEntity.setLabel( dictItemDO.getLabel() );
        dictItemEntity.setDictValue( dictItemDO.getDictValue() );
        dictItemEntity.setSort( dictItemDO.getSort() );
        dictItemEntity.setStatus( dictItemDO.getStatus() );
        dictItemEntity.setRemark( dictItemDO.getRemark() );
        dictItemEntity.setCreatedAt( dictItemDO.getCreatedAt() );
        dictItemEntity.setUpdatedAt( dictItemDO.getUpdatedAt() );

        return dictItemEntity;
    }

    @Override
    public DictItemEntity toItemEntity(DictItemDTO dictItemDTO) {
        if ( dictItemDTO == null ) {
            return null;
        }

        DictItemEntity dictItemEntity = new DictItemEntity();

        dictItemEntity.setId( dictItemDTO.getId() );
        dictItemEntity.setType( dictItemDTO.getType() );
        dictItemEntity.setLabel( dictItemDTO.getLabel() );
        dictItemEntity.setDictValue( dictItemDTO.getDictValue() );
        dictItemEntity.setSort( dictItemDTO.getSort() );
        dictItemEntity.setStatus( dictItemDTO.getStatus() );
        dictItemEntity.setRemark( dictItemDTO.getRemark() );
        dictItemEntity.setCreatedAt( dictItemDTO.getCreatedAt() );
        dictItemEntity.setUpdatedAt( dictItemDTO.getUpdatedAt() );

        return dictItemEntity;
    }

    @Override
    public DictItemDO toItemDataObject(DictItemEntity dictItemEntity) {
        if ( dictItemEntity == null ) {
            return null;
        }

        DictItemDO dictItemDO = new DictItemDO();

        dictItemDO.setId( dictItemEntity.getId() );
        dictItemDO.setType( dictItemEntity.getType() );
        dictItemDO.setLabel( dictItemEntity.getLabel() );
        dictItemDO.setDictValue( dictItemEntity.getDictValue() );
        dictItemDO.setSort( dictItemEntity.getSort() );
        dictItemDO.setStatus( dictItemEntity.getStatus() );
        dictItemDO.setRemark( dictItemEntity.getRemark() );
        dictItemDO.setCreatedAt( dictItemEntity.getCreatedAt() );
        dictItemDO.setUpdatedAt( dictItemEntity.getUpdatedAt() );

        return dictItemDO;
    }

    @Override
    public DictItemDTO toItemDTO(DictItemEntity dictItemEntity) {
        if ( dictItemEntity == null ) {
            return null;
        }

        DictItemDTO dictItemDTO = new DictItemDTO();

        dictItemDTO.setId( dictItemEntity.getId() );
        dictItemDTO.setType( dictItemEntity.getType() );
        dictItemDTO.setLabel( dictItemEntity.getLabel() );
        dictItemDTO.setDictValue( dictItemEntity.getDictValue() );
        dictItemDTO.setSort( dictItemEntity.getSort() );
        dictItemDTO.setStatus( dictItemEntity.getStatus() );
        dictItemDTO.setRemark( dictItemEntity.getRemark() );
        dictItemDTO.setCreatedAt( dictItemEntity.getCreatedAt() );
        dictItemDTO.setUpdatedAt( dictItemEntity.getUpdatedAt() );

        return dictItemDTO;
    }

    @Override
    public List<DictItemEntity> toItemEntityList(List<DictItemDO> dictItemDOList) {
        if ( dictItemDOList == null ) {
            return null;
        }

        List<DictItemEntity> list = new ArrayList<DictItemEntity>( dictItemDOList.size() );
        for ( DictItemDO dictItemDO : dictItemDOList ) {
            list.add( toItemEntity( dictItemDO ) );
        }

        return list;
    }

    @Override
    public List<DictItemDTO> toItemDTOList(List<DictItemEntity> dictItemEntityList) {
        if ( dictItemEntityList == null ) {
            return null;
        }

        List<DictItemDTO> list = new ArrayList<DictItemDTO>( dictItemEntityList.size() );
        for ( DictItemEntity dictItemEntity : dictItemEntityList ) {
            list.add( toItemDTO( dictItemEntity ) );
        }

        return list;
    }
}
