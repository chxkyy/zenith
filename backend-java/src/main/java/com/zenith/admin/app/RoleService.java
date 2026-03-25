package com.zenith.admin.app;

import com.alibaba.cola.dto.MultiResponse;
import com.zenith.admin.domain.gateway.RoleGateway;
import com.zenith.admin.domain.model.RoleEntity;
import com.zenith.admin.dto.RoleDTO;
import com.zenith.admin.infrastructure.convertor.RoleConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleGateway roleGateway;

    @Autowired
    private RoleConvertor roleConvertor;

    public MultiResponse<RoleDTO> listAll() {
        List<RoleEntity> entities = roleGateway.listAll();
        List<RoleDTO> dtos = roleConvertor.toDTOList(entities);
        return MultiResponse.of(dtos);
    }

    public void save(RoleDTO roleDTO) {
        RoleEntity entity = roleConvertor.toEntity(roleDTO);
        roleGateway.save(entity);
    }

    public void update(RoleDTO roleDTO) {
        RoleEntity entity = roleConvertor.toEntity(roleDTO);
        roleGateway.save(entity);
    }

    public void delete(Long id) {
        roleGateway.deleteById(id);
    }

    public RoleDTO getById(Long id) {
        RoleEntity entity = roleGateway.getById(id);
        return roleConvertor.toDTO(entity);
    }
}
