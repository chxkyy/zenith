package com.zenith.admin;

import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.domain.model.UserEntity;
import com.zenith.admin.dto.dataobject.UserDTO;
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
public class UserConvertorImpl implements UserConvertor {

    @Override
    public UserEntity toEntity(UserDO userDO) {
        if ( userDO == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setId( userDO.getId() );
        userEntity.setUsername( userDO.getUsername() );
        userEntity.setNickname( userDO.getNickname() );
        userEntity.setEmail( userDO.getEmail() );
        userEntity.setStatus( userDO.getStatus() );
        userEntity.setRole( userDO.getRole() );
        userEntity.setOrgName( userDO.getOrgName() );
        userEntity.setCreatedAt( userDO.getCreatedAt() );

        return userEntity;
    }

    @Override
    public UserEntity toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setId( userDTO.getId() );
        userEntity.setUsername( userDTO.getUsername() );
        userEntity.setNickname( userDTO.getNickname() );
        userEntity.setEmail( userDTO.getEmail() );
        userEntity.setStatus( userDTO.getStatus() );
        userEntity.setRole( userDTO.getRole() );
        userEntity.setOrgName( userDTO.getOrgName() );

        return userEntity;
    }

    @Override
    public UserDO toDataObject(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserDO userDO = new UserDO();

        userDO.setId( userEntity.getId() );
        userDO.setUsername( userEntity.getUsername() );
        userDO.setNickname( userEntity.getNickname() );
        userDO.setEmail( userEntity.getEmail() );
        userDO.setStatus( userEntity.getStatus() );
        userDO.setRole( userEntity.getRole() );
        userDO.setOrgName( userEntity.getOrgName() );
        userDO.setCreatedAt( userEntity.getCreatedAt() );

        return userDO;
    }

    @Override
    public UserDTO toDTO(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( userEntity.getId() );
        userDTO.setUsername( userEntity.getUsername() );
        userDTO.setNickname( userEntity.getNickname() );
        userDTO.setEmail( userEntity.getEmail() );
        userDTO.setStatus( userEntity.getStatus() );
        userDTO.setRole( userEntity.getRole() );
        userDTO.setOrgName( userEntity.getOrgName() );

        return userDTO;
    }

    @Override
    public List<UserEntity> toEntityList(List<UserDO> userDOList) {
        if ( userDOList == null ) {
            return null;
        }

        List<UserEntity> list = new ArrayList<UserEntity>( userDOList.size() );
        for ( UserDO userDO : userDOList ) {
            list.add( toEntity( userDO ) );
        }

        return list;
    }

    @Override
    public List<UserDTO> toDTOList(List<UserEntity> userEntityList) {
        if ( userEntityList == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>( userEntityList.size() );
        for ( UserEntity userEntity : userEntityList ) {
            list.add( toDTO( userEntity ) );
        }

        return list;
    }
}
