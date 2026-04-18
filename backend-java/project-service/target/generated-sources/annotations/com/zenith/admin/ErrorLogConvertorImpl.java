package com.zenith.admin;

import com.zenith.admin.dataobject.ErrorLogDO;
import com.zenith.admin.domain.model.ErrorLogEntity;
import com.zenith.admin.dto.dataobject.ErrorLogDTO;
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
public class ErrorLogConvertorImpl implements ErrorLogConvertor {

    @Override
    public ErrorLogEntity toEntity(ErrorLogDO errorLogDO) {
        if ( errorLogDO == null ) {
            return null;
        }

        ErrorLogEntity errorLogEntity = new ErrorLogEntity();

        errorLogEntity.setId( errorLogDO.getId() );
        errorLogEntity.setModule( errorLogDO.getModule() );
        errorLogEntity.setIp( errorLogDO.getIp() );
        errorLogEntity.setErrorMsg( errorLogDO.getErrorMsg() );
        errorLogEntity.setStackTrace( errorLogDO.getStackTrace() );
        errorLogEntity.setCreatedAt( errorLogDO.getCreatedAt() );

        return errorLogEntity;
    }

    @Override
    public ErrorLogEntity toEntity(ErrorLogDTO errorLogDTO) {
        if ( errorLogDTO == null ) {
            return null;
        }

        ErrorLogEntity errorLogEntity = new ErrorLogEntity();

        errorLogEntity.setId( errorLogDTO.getId() );
        errorLogEntity.setModule( errorLogDTO.getModule() );
        errorLogEntity.setIp( errorLogDTO.getIp() );
        errorLogEntity.setErrorMsg( errorLogDTO.getErrorMsg() );
        errorLogEntity.setStackTrace( errorLogDTO.getStackTrace() );
        errorLogEntity.setCreatedAt( errorLogDTO.getCreatedAt() );

        return errorLogEntity;
    }

    @Override
    public ErrorLogDO toDataObject(ErrorLogEntity errorLogEntity) {
        if ( errorLogEntity == null ) {
            return null;
        }

        ErrorLogDO errorLogDO = new ErrorLogDO();

        errorLogDO.setId( errorLogEntity.getId() );
        errorLogDO.setModule( errorLogEntity.getModule() );
        errorLogDO.setIp( errorLogEntity.getIp() );
        errorLogDO.setErrorMsg( errorLogEntity.getErrorMsg() );
        errorLogDO.setStackTrace( errorLogEntity.getStackTrace() );
        errorLogDO.setCreatedAt( errorLogEntity.getCreatedAt() );

        return errorLogDO;
    }

    @Override
    public ErrorLogDTO toDTO(ErrorLogEntity errorLogEntity) {
        if ( errorLogEntity == null ) {
            return null;
        }

        ErrorLogDTO errorLogDTO = new ErrorLogDTO();

        errorLogDTO.setId( errorLogEntity.getId() );
        errorLogDTO.setModule( errorLogEntity.getModule() );
        errorLogDTO.setIp( errorLogEntity.getIp() );
        errorLogDTO.setErrorMsg( errorLogEntity.getErrorMsg() );
        errorLogDTO.setStackTrace( errorLogEntity.getStackTrace() );
        errorLogDTO.setCreatedAt( errorLogEntity.getCreatedAt() );

        return errorLogDTO;
    }

    @Override
    public List<ErrorLogEntity> toEntityList(List<ErrorLogDO> errorLogDOList) {
        if ( errorLogDOList == null ) {
            return null;
        }

        List<ErrorLogEntity> list = new ArrayList<ErrorLogEntity>( errorLogDOList.size() );
        for ( ErrorLogDO errorLogDO : errorLogDOList ) {
            list.add( toEntity( errorLogDO ) );
        }

        return list;
    }

    @Override
    public List<ErrorLogDTO> toDTOList(List<ErrorLogEntity> errorLogEntityList) {
        if ( errorLogEntityList == null ) {
            return null;
        }

        List<ErrorLogDTO> list = new ArrayList<ErrorLogDTO>( errorLogEntityList.size() );
        for ( ErrorLogEntity errorLogEntity : errorLogEntityList ) {
            list.add( toDTO( errorLogEntity ) );
        }

        return list;
    }
}
