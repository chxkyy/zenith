package com.zenith.admin.domain.gateway;

import com.zenith.admin.domain.model.UserEntity;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.UserPageQuery;

public interface UserGateway {
    PageInfo<UserEntity> listByPage(UserPageQuery query);
    void save(UserEntity user);
    UserEntity getById(Long id);
    void deleteById(Long id);
}
