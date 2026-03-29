package com.zenith.admin.domain.gateway;

import com.zenith.admin.domain.model.RoleEntity;
import com.zenith.admin.dto.RolePageQuery;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface RoleGateway {
    List<RoleEntity> listAll();
    PageInfo<RoleEntity> listByPage(RolePageQuery query);
    void save(RoleEntity role);
    RoleEntity getById(Long id);
    void deleteById(Long id);
}
