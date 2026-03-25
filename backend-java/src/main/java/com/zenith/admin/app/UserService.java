package com.zenith.admin.app;

import com.alibaba.cola.dto.PageResponse;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.domain.gateway.UserGateway;
import com.zenith.admin.domain.model.UserEntity;
import com.zenith.admin.dto.UserDTO;
import com.zenith.admin.dto.UserPageQuery;
import com.zenith.admin.infrastructure.convertor.UserConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserGateway userGateway;

    @Autowired
    private UserConvertor userConvertor;

    public PageResponse<UserDTO> listByPage(UserPageQuery query) {
        PageInfo<UserEntity> pageInfo = userGateway.listByPage(query.getPageIndex(), query.getPageSize());
        
        List<UserDTO> dtos = userConvertor.toDTOList(pageInfo.getList());

        return PageResponse.of(dtos, (int) pageInfo.getTotal(), query.getPageSize(), query.getPageIndex());
    }

    public void save(UserDTO userDTO) {
        UserEntity entity = userConvertor.toEntity(userDTO);
        userGateway.save(entity);
    }

    public void update(UserDTO userDTO) {
        UserEntity entity = userConvertor.toEntity(userDTO);
        userGateway.save(entity);
    }

    public void delete(Long id) {
        userGateway.deleteById(id);
    }

    public UserDTO getById(Long id) {
        UserEntity entity = userGateway.getById(id);
        return userConvertor.toDTO(entity);
    }
}
