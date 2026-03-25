package com.zenith.admin.domain.gateway;

import com.zenith.admin.domain.model.RoleEntity;
import java.util.List;

public interface RoleGateway {
    List<RoleEntity> listAll();
    void save(RoleEntity role);
    RoleEntity getById(Long id);
    void deleteById(Long id);
}
