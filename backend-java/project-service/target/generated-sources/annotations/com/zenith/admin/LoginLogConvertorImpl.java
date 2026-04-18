package com.zenith.admin;

import com.zenith.admin.dataobject.LoginLogDO;
import com.zenith.admin.domain.model.LoginLogEntity;
import com.zenith.admin.dto.dataobject.LoginLogDTO;
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
public class LoginLogConvertorImpl implements LoginLogConvertor {

    @Override
    public LoginLogEntity toEntity(LoginLogDO loginLogDO) {
        if ( loginLogDO == null ) {
            return null;
        }

        LoginLogEntity loginLogEntity = new LoginLogEntity();

        loginLogEntity.setId( loginLogDO.getId() );
        loginLogEntity.setUsername( loginLogDO.getUsername() );
        loginLogEntity.setIp( loginLogDO.getIp() );
        loginLogEntity.setStatus( loginLogDO.getStatus() );
        loginLogEntity.setMsg( loginLogDO.getMsg() );
        loginLogEntity.setLoginAt( loginLogDO.getLoginAt() );
        loginLogEntity.setLogoutAt( loginLogDO.getLogoutAt() );

        return loginLogEntity;
    }

    @Override
    public LoginLogEntity toEntity(LoginLogDTO loginLogDTO) {
        if ( loginLogDTO == null ) {
            return null;
        }

        LoginLogEntity loginLogEntity = new LoginLogEntity();

        loginLogEntity.setId( loginLogDTO.getId() );
        loginLogEntity.setUsername( loginLogDTO.getUsername() );
        loginLogEntity.setIp( loginLogDTO.getIp() );
        loginLogEntity.setStatus( loginLogDTO.getStatus() );
        loginLogEntity.setMsg( loginLogDTO.getMsg() );
        loginLogEntity.setLoginAt( loginLogDTO.getLoginAt() );
        loginLogEntity.setLogoutAt( loginLogDTO.getLogoutAt() );

        return loginLogEntity;
    }

    @Override
    public LoginLogDO toDataObject(LoginLogEntity loginLogEntity) {
        if ( loginLogEntity == null ) {
            return null;
        }

        LoginLogDO loginLogDO = new LoginLogDO();

        loginLogDO.setId( loginLogEntity.getId() );
        loginLogDO.setUsername( loginLogEntity.getUsername() );
        loginLogDO.setIp( loginLogEntity.getIp() );
        loginLogDO.setStatus( loginLogEntity.getStatus() );
        loginLogDO.setMsg( loginLogEntity.getMsg() );
        loginLogDO.setLoginAt( loginLogEntity.getLoginAt() );
        loginLogDO.setLogoutAt( loginLogEntity.getLogoutAt() );

        return loginLogDO;
    }

    @Override
    public LoginLogDTO toDTO(LoginLogEntity loginLogEntity) {
        if ( loginLogEntity == null ) {
            return null;
        }

        LoginLogDTO loginLogDTO = new LoginLogDTO();

        loginLogDTO.setId( loginLogEntity.getId() );
        loginLogDTO.setUsername( loginLogEntity.getUsername() );
        loginLogDTO.setIp( loginLogEntity.getIp() );
        loginLogDTO.setStatus( loginLogEntity.getStatus() );
        loginLogDTO.setMsg( loginLogEntity.getMsg() );
        loginLogDTO.setLoginAt( loginLogEntity.getLoginAt() );
        loginLogDTO.setLogoutAt( loginLogEntity.getLogoutAt() );

        return loginLogDTO;
    }

    @Override
    public List<LoginLogEntity> toEntityList(List<LoginLogDO> loginLogDOList) {
        if ( loginLogDOList == null ) {
            return null;
        }

        List<LoginLogEntity> list = new ArrayList<LoginLogEntity>( loginLogDOList.size() );
        for ( LoginLogDO loginLogDO : loginLogDOList ) {
            list.add( toEntity( loginLogDO ) );
        }

        return list;
    }

    @Override
    public List<LoginLogDTO> toDTOList(List<LoginLogEntity> loginLogEntityList) {
        if ( loginLogEntityList == null ) {
            return null;
        }

        List<LoginLogDTO> list = new ArrayList<LoginLogDTO>( loginLogEntityList.size() );
        for ( LoginLogEntity loginLogEntity : loginLogEntityList ) {
            list.add( toDTO( loginLogEntity ) );
        }

        return list;
    }
}
