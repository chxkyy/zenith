package com.zenith.admin.infrastructure.gateway;

import com.zenith.admin.domain.gateway.RoleGateway;
import com.zenith.admin.domain.model.RoleEntity;
import com.zenith.admin.infrastructure.convertor.RoleConvertor;
import com.zenith.admin.infrastructure.dataobject.RoleDO;
import com.zenith.admin.infrastructure.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleGatewayImpl implements RoleGateway {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleConvertor roleConvertor;

    @Override
    public List<RoleEntity> listAll() {
        List<RoleDO> roleDOS = roleMapper.selectList(null);
        return roleConvertor.toEntityList(roleDOS);
    }

    @Override
    public void save(RoleEntity role) {
        RoleDO roleDO = roleConvertor.toDataObject(role);
        if (roleDO.getId() == null) {
            roleMapper.insert(roleDO);
        } else {
            roleMapper.updateById(roleDO);
        }
    }

    @Override
    public RoleEntity getById(Long id) {
        RoleDO roleDO = roleMapper.selectById(id);
        return roleConvertor.toEntity(roleDO);
    }

    @Override
    public void deleteById(Long id) {
        roleMapper.deleteById(id);
    }
}
