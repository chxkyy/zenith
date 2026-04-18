package com.zenith.admin;

import com.zenith.admin.dataobject.OperLogDO;
import com.zenith.admin.domain.model.OperLogEntity;
import com.zenith.admin.dto.dataobject.OperLogDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-18T16:04:36+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class OperLogConvertorImpl implements OperLogConvertor {

    @Override
    public OperLogEntity toEntity(OperLogDO operLogDO) {
        if ( operLogDO == null ) {
            return null;
        }

        OperLogEntity operLogEntity = new OperLogEntity();

        operLogEntity.setId( operLogDO.getId() );
        operLogEntity.setModule( operLogDO.getModule() );
        operLogEntity.setContent( operLogDO.getContent() );
        operLogEntity.setOperator( operLogDO.getOperator() );
        operLogEntity.setIp( operLogDO.getIp() );
        operLogEntity.setResult( operLogDO.getResult() );
        operLogEntity.setRemark( operLogDO.getRemark() );
        operLogEntity.setCreatedAt( operLogDO.getCreatedAt() );

        return operLogEntity;
    }

    @Override
    public OperLogEntity toEntity(OperLogDTO operLogDTO) {
        if ( operLogDTO == null ) {
            return null;
        }

        OperLogEntity operLogEntity = new OperLogEntity();

        operLogEntity.setId( operLogDTO.getId() );
        operLogEntity.setModule( operLogDTO.getModule() );
        operLogEntity.setContent( operLogDTO.getContent() );
        operLogEntity.setOperator( operLogDTO.getOperator() );
        operLogEntity.setIp( operLogDTO.getIp() );
        operLogEntity.setResult( operLogDTO.getResult() );
        operLogEntity.setRemark( operLogDTO.getRemark() );
        operLogEntity.setCreatedAt( operLogDTO.getCreatedAt() );

        return operLogEntity;
    }

    @Override
    public OperLogDO toDataObject(OperLogEntity operLogEntity) {
        if ( operLogEntity == null ) {
            return null;
        }

        OperLogDO operLogDO = new OperLogDO();

        operLogDO.setId( operLogEntity.getId() );
        operLogDO.setModule( operLogEntity.getModule() );
        operLogDO.setContent( operLogEntity.getContent() );
        operLogDO.setOperator( operLogEntity.getOperator() );
        operLogDO.setIp( operLogEntity.getIp() );
        operLogDO.setResult( operLogEntity.getResult() );
        operLogDO.setRemark( operLogEntity.getRemark() );
        operLogDO.setCreatedAt( operLogEntity.getCreatedAt() );

        return operLogDO;
    }

    @Override
    public OperLogDTO toDTO(OperLogEntity operLogEntity) {
        if ( operLogEntity == null ) {
            return null;
        }

        OperLogDTO operLogDTO = new OperLogDTO();

        operLogDTO.setId( operLogEntity.getId() );
        operLogDTO.setModule( operLogEntity.getModule() );
        operLogDTO.setContent( operLogEntity.getContent() );
        operLogDTO.setOperator( operLogEntity.getOperator() );
        operLogDTO.setIp( operLogEntity.getIp() );
        operLogDTO.setResult( operLogEntity.getResult() );
        operLogDTO.setRemark( operLogEntity.getRemark() );
        operLogDTO.setCreatedAt( operLogEntity.getCreatedAt() );

        return operLogDTO;
    }

    @Override
    public List<OperLogEntity> toEntityList(List<OperLogDO> operLogDOList) {
        if ( operLogDOList == null ) {
            return null;
        }

        List<OperLogEntity> list = new ArrayList<OperLogEntity>( operLogDOList.size() );
        for ( OperLogDO operLogDO : operLogDOList ) {
            list.add( toEntity( operLogDO ) );
        }

        return list;
    }

    @Override
    public List<OperLogDTO> toDTOList(List<OperLogEntity> operLogEntityList) {
        if ( operLogEntityList == null ) {
            return null;
        }

        List<OperLogDTO> list = new ArrayList<OperLogDTO>( operLogEntityList.size() );
        for ( OperLogEntity operLogEntity : operLogEntityList ) {
            list.add( toDTO( operLogEntity ) );
        }

        return list;
    }
}
